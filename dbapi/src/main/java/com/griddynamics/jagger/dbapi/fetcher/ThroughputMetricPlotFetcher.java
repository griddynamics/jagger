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
public class ThroughputMetricPlotFetcher extends StandardMetricPlotFetcher<ThroughputMetricPlotFetcher.ThroughputRawData> {

    @Override
    protected Iterable<? extends PlotSingleDto> assemble(Collection<ThroughputRawData> rawData) {
        List<PointDto> pointDtoList = new ArrayList<PointDto>(rawData.size());

        String sessionId = rawData.iterator().next().getSessionId();

        for (ThroughputRawData raw : rawData) {
            double x = DataProcessingUtil.round(raw.getTime() / 1000.0D);
            double y = DataProcessingUtil.round(raw.getThroughput());
            pointDtoList.add(new PointDto(x, y));
        }

        String legend = legendProvider.generatePlotLegend(sessionId, DefaultWorkloadParameters.THROUGHPUT.getDescription(), true);
        return Arrays.asList(new PlotSingleDto(pointDtoList, legend,
                ColorCodeGenerator.getHexColorCode(StandardMetricsNamesUtil.THROUGHPUT_OLD_ID,
                        StandardMetricsNamesUtil.getSynonyms(StandardMetricsNamesUtil.THROUGHPUT_OLD_ID),
                        sessionId)));
    }

    @Override
    protected List<ThroughputRawData> findRawDataByTaskData(Set<Long> taskIds) {
        @SuppressWarnings("all")
        List<Object[]> rawDataList = entityManager.createQuery(
                "select tis.time, tis.throughput, tis.taskData.id, tis.taskData.sessionId from TimeInvocationStatistics as tis " +
                        "where tis.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .getResultList();

        List<ThroughputRawData> resultList = new ArrayList<ThroughputRawData>(rawDataList.size());

        for (Object[] objects : rawDataList) {

            String sessionId = (String) objects[3];
            Long taskDataId = (Long) objects[2];
            Long time = (Long) objects[0];
            Double throughput = (Double) objects[1];

            resultList.add(new ThroughputRawData(sessionId, taskDataId, time, throughput));
        }

        return resultList;
    }

    public static class ThroughputRawData implements StandardMetricPlotFetcher.StandardMetricRawData {

        private String sessionId;
        private Long taskDataId;
        private Long time;
        private Double throughput;

        public ThroughputRawData(String sessionId, Long taskDataId, Long time, Double throughput) {
            this.sessionId = sessionId;
            this.taskDataId = taskDataId;
            this.time = time;
            this.throughput = throughput;
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

        public Double getThroughput() {
            return throughput;
        }
    }
}
