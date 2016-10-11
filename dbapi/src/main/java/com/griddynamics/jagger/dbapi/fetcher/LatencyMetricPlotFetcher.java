package com.griddynamics.jagger.dbapi.fetcher;


import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.util.ColorCodeGenerator;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import com.griddynamics.jagger.dbapi.parameter.DefaultWorkloadParameters;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Deprecated
public class LatencyMetricPlotFetcher extends StandardMetricPlotFetcher<LatencyMetricPlotFetcher.LatencyRawData> {

    @Override
    protected Iterable<? extends PlotSingleDto> assemble(Collection<LatencyRawData> rawDataList) {

        List<PlotSingleDto> plotDatasetDtoList = new ArrayList<PlotSingleDto>(2);

        String sessionId = rawDataList.iterator().next().getSessionId();

        List<PointDto> pointDtoLatencyList = new ArrayList<PointDto>(rawDataList.size());
        List<PointDto> pointDtoLatencyStdDevList = new ArrayList<PointDto>(rawDataList.size());

        for (LatencyRawData rawData : rawDataList) {
            double x = DataProcessingUtil.round(rawData.getTime() / 1000.0D);
            double yLatency = DataProcessingUtil.round(rawData.getLatency());
            double yLatencyStdDev = DataProcessingUtil.round(rawData.getLatencyStdDev());
            pointDtoLatencyList.add(new PointDto(x, yLatency));
            pointDtoLatencyStdDevList.add(new PointDto(x, yLatencyStdDev));
        }

        String legend = legendProvider.generatePlotLegend(sessionId, DefaultWorkloadParameters.LATENCY.getDescription(), true);
        PlotSingleDto plotDatasetDto = new PlotSingleDto(pointDtoLatencyList, legend,
                ColorCodeGenerator.getHexColorCode(StandardMetricsNamesUtil.LATENCY_OLD_ID,
                        StandardMetricsNamesUtil.getSynonyms(StandardMetricsNamesUtil.LATENCY_OLD_ID),
                        sessionId));
        plotDatasetDtoList.add(plotDatasetDto);

        legend = legendProvider.generatePlotLegend(sessionId, DefaultWorkloadParameters.LATENCY_STD_DEV.getDescription(), true);
        plotDatasetDto = new PlotSingleDto(pointDtoLatencyStdDevList, legend,
                ColorCodeGenerator.getHexColorCode(StandardMetricsNamesUtil.LATENCY_STD_DEV_OLD_ID,
                        StandardMetricsNamesUtil.getSynonyms(StandardMetricsNamesUtil.LATENCY_STD_DEV_OLD_ID),
                        sessionId));
        plotDatasetDtoList.add(plotDatasetDto);

        return plotDatasetDtoList;
    }

    @Override
    @Deprecated
    protected List<LatencyRawData> findRawDataByTaskData(Set<Long> taskIds) {

        @SuppressWarnings("all")
        List<Object[]> rawDataList =  entityManager.createQuery(
                "select tis.time, tis.latency, tis.latencyStdDev, tis.taskData.id, tis.taskData.sessionId " +
                        "from TimeInvocationStatistics as tis where tis.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .getResultList();

        List<LatencyRawData> resultList = new ArrayList<LatencyRawData>(rawDataList.size());

        for (Object[] objects : rawDataList) {
            String sessionId = (String) objects[4];
            Long taskDataId = (Long) objects[3];
            Long time = (Long) objects[0];
            Double latency = (Double) objects[1];
            Double latencyStdDev = (Double) objects[2];

            resultList.add(new LatencyRawData(sessionId, taskDataId, time, latency, latencyStdDev));
        }

        return resultList;
    }


    public static class LatencyRawData implements StandardMetricPlotFetcher.StandardMetricRawData {

        private String sessionId;
        private Long taskDataId;
        private Long time;
        private Double latency;
        private Double latencyStdDev;

        public LatencyRawData(String sessionId, Long taskDataId, Long time, Double latency, Double latencyStdDev) {
            this.sessionId = sessionId;
            this.taskDataId = taskDataId;
            this.time = time;
            this.latency = latency;
            this.latencyStdDev = latencyStdDev;
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

        public Double getLatency() {
            return latency;
        }

        public Double getLatencyStdDev() {
            return latencyStdDev;
        }
    }
}