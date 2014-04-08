package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.MetricDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.MetricValueDto;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;

import java.util.*;

public class StandardMetricSummaryFetcher extends SummaryDbMetricDataFetcher {

    private static final String THROUGHPUT = "throughput";
    private static final String AVG_LATENCY = "avgLatency";
    private static final String SUCCESS_RATE = "successRate";
    private static final String SAMPLES = "samples";

    @Override
    protected Set<MetricDto> fetchData(List<MetricNameDto> standardMetricNames) {

        Set<MetricDto> resultSet = new HashSet<MetricDto>();

        resultSet.addAll(getRestMetrics(standardMetricNames));

        return resultSet;
    }

    private Collection<? extends MetricDto> getRestMetrics(List<MetricNameDto> restMetricNames) {

        if (restMetricNames.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto metricName : restMetricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        List<WorkloadTaskData> workloadTaskDatas = (List<WorkloadTaskData>)entityManager.createNativeQuery("select * " +
                "        from WorkloadTaskData as wtd join TaskData as td on td.id in (:ids)" +
                "         and wtd.taskId=td.taskId and wtd.sessionId=td.sessionId", WorkloadTaskData.class)
                .setParameter("ids", taskIds)
                .getResultList();

        if (workloadTaskDatas.isEmpty()) {
            log.warn("Could not find data for {}", restMetricNames);
            return Collections.EMPTY_LIST;
        }

        Set<MetricDto> resultSet = new HashSet<MetricDto>();
        for (MetricNameDto metricName : restMetricNames) {

            MetricDto metricDto = new MetricDto();
            resultSet.add(metricDto);
            metricDto.setMetricName(metricName);
            metricDto.setValues(new HashSet<MetricValueDto>());
            for (WorkloadTaskData workloadTaskData : workloadTaskDatas) {

                // check if this WorkloadTaskData suit to given MetricNameDto
                if (metricName.getTest().getTaskName().equals(workloadTaskData.getScenario().getName())
                        && metricName.getTest().getSessionIds().contains(workloadTaskData.getSessionId())) {

                    MetricValueDto mvd = new MetricValueDto();
                    mvd.setSessionId(Long.parseLong(workloadTaskData.getSessionId()));
                    String metricId = metricName.getMetricName();

                    String value = null;
                    if (AVG_LATENCY.equals(metricId)) {
                        value = workloadTaskData.getAvgLatency().toString();
                    } else if (SAMPLES.equals(metricId)) {
                        value = workloadTaskData.getSamples().toString();
                    } else if (SUCCESS_RATE.equals(metricId)) {
                        value = workloadTaskData.getSuccessRate().toString();
                    } else if (THROUGHPUT.equals(metricId)) {
                        value = workloadTaskData.getThroughput().toString();
                    }

                    if (value != null)  {
                        mvd.setValue(value);
                        metricDto.getValues().add(mvd);
                    }
                }
            }
            metricDto.setPlotSeriesDtos(generatePlotSeriesDto(metricDto));
        }

        return resultSet;
    }
}
