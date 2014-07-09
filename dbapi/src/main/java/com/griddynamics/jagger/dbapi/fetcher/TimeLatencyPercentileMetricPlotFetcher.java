package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.*;

public class TimeLatencyPercentileMetricPlotFetcher extends AbstractMetricPlotFetcher {

    @Override
    protected Collection<MetricRawData> getAllRawData(List<MetricNameDto> metricNames) {

        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto mnd : metricNames) {
            taskIds.addAll(mnd.getTaskIds());
        }

        @SuppressWarnings("all")
        List<Object[]> rawDataList =  entityManager.createQuery(
                "select tis.time, ps.percentileKey, ps.percentileValue, tis.taskData.id, tis.taskData.sessionId from TimeLatencyPercentile as ps " +
                        "inner join ps.timeInvocationStatistics as tis where tis.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds).getResultList();

        List<MetricRawData> resultList = new ArrayList<MetricRawData>(rawDataList.size());

        for (Object[] objects : rawDataList) {

            String sessionId = (String) objects[4];
            Long taskDataId = (Long) objects[3];
            Long time = (Long) objects[0];
            Double percentileKey = (Double) objects[1];
            Double percentileValue = (Double) objects[2];

            resultList.add(new MetricRawData(sessionId, taskDataId, StandardMetricsNamesUtil.getLatencyMetricName(percentileKey), time, percentileValue));
        }

        return resultList;
    }
}
