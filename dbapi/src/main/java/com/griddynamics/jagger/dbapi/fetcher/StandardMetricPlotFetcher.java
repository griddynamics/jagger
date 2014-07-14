package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.*;


public class StandardMetricPlotFetcher extends AbstractMetricPlotFetcher {


    @Override
    protected Collection<MetricRawData> getAllRawData(List<MetricNameDto> metricNames) {

        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto mnd : metricNames) {
            taskIds.addAll(mnd.getTaskIds());
        }

        @SuppressWarnings("all")
        List<Object[]> rawDataList =  entityManager.createQuery(
                "select tis.time, tis.latency, tis.latencyStdDev, tis.taskData.id, tis.taskData.sessionId, tis.throughput " +
                        "from TimeInvocationStatistics as tis where tis.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .getResultList();

        List<MetricRawData> resultList = new ArrayList<MetricRawData>(rawDataList.size());

        for (Object[] objects : rawDataList) {
            String sessionId = (String) objects[4];
            Long taskDataId = (Long) objects[3];
            Long time = (Long) objects[0];
            Double latency = (Double) objects[1];
            Double latencyStdDev = (Double) objects[2];
            Double throughput = (Double) objects[5];

            resultList.add(new MetricRawData(sessionId, taskDataId, StandardMetricsNamesUtil.LATENCY_ID, time, latency));
            resultList.add(new MetricRawData(sessionId, taskDataId, StandardMetricsNamesUtil.LATENCY_STD_DEV_ID, time, latencyStdDev));
            resultList.add(new MetricRawData(sessionId, taskDataId, StandardMetricsNamesUtil.THROUGHPUT_ID, time, throughput));
        }

        return resultList;
    }
}
