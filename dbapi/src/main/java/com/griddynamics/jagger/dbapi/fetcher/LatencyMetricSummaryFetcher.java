package com.griddynamics.jagger.dbapi.fetcher;


import com.griddynamics.jagger.dbapi.dto.MetricDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.MetricValueDto;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;

import java.util.*;

public class LatencyMetricSummaryFetcher extends SummaryDbMetricDataFetcher {

    @Override
    protected Set<MetricDto> fetchData(List<MetricNameDto> metricNames) {

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

        Map<MetricNameDto, MetricDto> resultMap = new HashMap<MetricNameDto, MetricDto>();

        for (Object[] temp : latency){

            Long taskId = (Long)temp[1];

            Map<String, MetricNameDto> metricIdMap = mappedMetricNames.get(taskId);
            if (metricIdMap == null) {
                continue;
            }
            String metricId = MetricNameUtil.getLatencyMetricName((Double)temp[3]);
            MetricNameDto metricNameDto = metricIdMap.get(metricId);
            if (metricNameDto == null) {
                continue;
            }

            if (!resultMap.containsKey(metricNameDto)) {
                MetricDto metricDto = new MetricDto();
                metricDto.setMetricName(metricNameDto);
                metricDto.setValues(new HashSet<MetricValueDto>());
                resultMap.put(metricNameDto, metricDto);
            }

            MetricDto metricDto = resultMap.get(metricNameDto);

            MetricValueDto value = new MetricValueDto();
            value.setValue(String.format("%.3f", (Double)temp[0] / 1000));
            value.setTestId(Long.parseLong(temp[1].toString()));
            value.setSessionId(Long.parseLong(temp[2].toString()));
            metricDto.getValues().add(value);
        }

        for (MetricDto md : resultMap.values()) {
            md.setPlotSeriesDtos(generatePlotSeriesDto(md));
        }

        return new HashSet<MetricDto>(resultMap.values());
    }
}
