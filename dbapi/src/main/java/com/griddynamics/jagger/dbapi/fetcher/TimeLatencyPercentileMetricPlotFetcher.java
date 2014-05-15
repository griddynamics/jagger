package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.util.ColorCodeGenerator;
import com.griddynamics.jagger.dbapi.dto.PlotDatasetDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import com.griddynamics.jagger.dbapi.parameter.DefaultWorkloadParameters;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;

import java.util.*;

public class TimeLatencyPercentileMetricPlotFetcher extends StandardMetricPlotFetcher<TimeLatencyPercentileMetricPlotFetcher.LatencyPercentileRawData> {

    @Override
    protected Iterable<? extends PlotDatasetDto> assemble(Collection<LatencyPercentileRawData> rawData) {

        List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();
        Map<Double, List<PointDto>> percentiles = new TreeMap<Double, List<PointDto>>();

        String sessionId = rawData.iterator().next().getSessionId();
        double previousPercentileValue = 0.0;

        for (LatencyPercentileRawData raw : rawData) {
            if (!percentiles.containsKey(raw.getPercentileKey())) {
                percentiles.put(raw.getPercentileKey(), new ArrayList<PointDto>(rawData.size()));
            }
            List<PointDto> list = percentiles.get(raw.getPercentileKey());

            double x = DataProcessingUtil.round(raw.getTime() / 1000.0D);
            double y = DataProcessingUtil.round((raw.getPercentileValue() - previousPercentileValue) / 1000);
            list.add(new PointDto(x, y));

            previousPercentileValue = y;
        }

        for (Map.Entry<Double, List<PointDto>> entry : percentiles.entrySet()) {
            DefaultWorkloadParameters parameter = DefaultWorkloadParameters.fromDescription(entry.getKey().toString());
            String description = (parameter == null ? entry.getKey().toString() : parameter.getDescription());
            String legend = legendProvider.generatePlotLegend(sessionId, description, true);
            plotDatasetDtoList.add(new PlotDatasetDto(entry.getValue(), legend, ColorCodeGenerator.getHexColorCode()));
        }

        return plotDatasetDtoList;

    }

    @Override
    protected List<LatencyPercentileRawData> findRawDataByTaskData(Set<Long> taskIds) {

        @SuppressWarnings("all")
        List<Object[]> rawDataList =  entityManager.createQuery(
                "select tis.time, ps.percentileKey, ps.percentileValue, tis.taskData.id, tis.taskData.sessionId from TimeLatencyPercentile as ps " +
                        "inner join ps.timeInvocationStatistics as tis where tis.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds).getResultList();

        List<LatencyPercentileRawData> resultList = new ArrayList<LatencyPercentileRawData>(rawDataList.size());

        for (Object[] objects : rawDataList) {

            String sessionId = (String) objects[4];
            Long taskDataId = (Long) objects[3];
            Long time = (Long) objects[0];
            Double percentileKey = (Double) objects[1];
            Double percentileValue = (Double) objects[2];

            resultList.add(new LatencyPercentileRawData(sessionId, taskDataId, time, percentileKey, percentileValue));
        }

        return resultList;

    }


    public static class LatencyPercentileRawData implements StandardMetricPlotFetcher.StandardMetricRawData {

        private String sessionId;
        private Long taskDataId;
        private Long time;
        private Double percentileKey;
        private Double percentileValue;

        public LatencyPercentileRawData(String sessionId, Long taskDataId, Long time, Double percentileKey, Double percentileValue) {
            this.sessionId = sessionId;
            this.taskDataId = taskDataId;
            this.time = time;
            this.percentileKey = percentileKey;
            this.percentileValue = percentileValue;
        }

        @Override
        public Long getTaskDataId() {
            return taskDataId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public Long getTime() {
            return time;
        }

        public Double getPercentileKey() {
            return percentileKey;
        }

        public Double getPercentileValue() {
            return percentileValue;
        }
    }
}
