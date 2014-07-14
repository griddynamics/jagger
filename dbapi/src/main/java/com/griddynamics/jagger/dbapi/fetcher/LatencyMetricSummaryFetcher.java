package com.griddynamics.jagger.dbapi.fetcher;


import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.*;

public class LatencyMetricSummaryFetcher extends DbMetricDataFetcher<SummarySingleDto> {

    @Override
    protected Set<SummarySingleDto> fetchData(List<MetricNameDto> metricNames) {

        if (metricNames.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<Long> taskIds = new HashSet<Long>();
        Set<Double> percentileKeys = new HashSet<Double>();
        for (MetricNameDto metricName : metricNames) {
            taskIds.addAll(metricName.getTaskIds());
            percentileKeys.add(Double.parseDouble(metricName.getMetricName().split(" ")[1]));
        }
        //it is a latency metric
        List<Object[]> latency = entityManager.createQuery("select s.percentileValue, s.workloadProcessDescriptiveStatistics.taskData.id, s.workloadProcessDescriptiveStatistics.taskData.sessionId, s.percentileKey " +
                "from  WorkloadProcessLatencyPercentile as s " +
                "where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                "and s.percentileKey in (:latencyKey) ")
                .setParameter("taskIds", taskIds)
                .setParameter("latencyKey", percentileKeys)
                .getResultList();

        if (latency.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Map<Long, Map<String, MetricNameDto>> mappedMetricNames = MetricNameUtil.getMappedMetricDtos(metricNames);

        Map<MetricNameDto, SummarySingleDto> resultMap = new HashMap<MetricNameDto, SummarySingleDto>();

        for (Object[] temp : latency){

            Long taskId = (Long)temp[1];

            Map<String, MetricNameDto> metricIdMap = mappedMetricNames.get(taskId);
            if (metricIdMap == null) {
                continue;
            }
            String metricId = StandardMetricsNamesUtil.getLatencyMetricName((Double) temp[3]);
            MetricNameDto metricNameDto = metricIdMap.get(metricId);
            if (metricNameDto == null) {
                continue;
            }

            if (!resultMap.containsKey(metricNameDto)) {
                SummarySingleDto metricDto = new SummarySingleDto();
                metricDto.setMetricName(metricNameDto);
                metricDto.setValues(new HashSet<SummaryMetricValueDto>());
                resultMap.put(metricNameDto, metricDto);
            }

            SummarySingleDto metricDto = resultMap.get(metricNameDto);

            SummaryMetricValueDto value = new SummaryMetricValueDto();
            value.setValue(String.format(Locale.ENGLISH, "%.3f", (Double)temp[0] / 1000));
            value.setSessionId(Long.parseLong(temp[2].toString()));
            metricDto.getValues().add(value);
        }

        return new HashSet<SummarySingleDto>(resultMap.values());
    }
}
