package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.griddynamics.jagger.dbapi.util.ColorCodeGenerator.getHexColorCode;
import static com.griddynamics.jagger.dbapi.util.PlotPointShapeGenerator.generatePointShape;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.getLatencyMetricName;
import static java.util.Collections.singletonList;

@Component
@Deprecated
public class TimeLatencyPercentileMetricPlotFetcher extends StandardMetricPlotFetcher<TimeLatencyPercentileMetricPlotFetcher.LatencyPercentileRawData> {

    @Override
    protected Iterable<? extends PlotSingleDto> assemble(Collection<LatencyPercentileRawData> rawData) {

        List<PlotSingleDto> plotDatasetDtoList = new ArrayList<PlotSingleDto>();
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
            String latencyNameNewModel = getLatencyMetricName(entry.getKey(), false);
            String legend = legendProvider.generatePlotLegend(sessionId, latencyNameNewModel, true);
            plotDatasetDtoList.add(new PlotSingleDto(entry.getValue(), legend,
                    getHexColorCode(getLatencyMetricName(entry.getKey(), true),
                            singletonList(latencyNameNewModel),
                            sessionId),
                    generatePointShape(getLatencyMetricName(entry.getKey(), true),
                            singletonList(latencyNameNewModel),
                            sessionId)));
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
