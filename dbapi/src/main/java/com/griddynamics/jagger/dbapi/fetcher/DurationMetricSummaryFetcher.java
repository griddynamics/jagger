package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import com.griddynamics.jagger.util.TimeUtils;

import java.math.BigInteger;
import java.util.*;

public class DurationMetricSummaryFetcher extends DbMetricDataFetcher<SummarySingleDto> {

    @Override
    protected Set<SummarySingleDto> fetchData(List<MetricNameDto> durationMetricNames) {
        if (durationMetricNames.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> taskIds = new HashSet<Long>();

        for (MetricNameDto metricName : durationMetricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        // list of [sessionId, endTime, startTime, taskData_id]
        List<Object[]> result = entityManager.createNativeQuery("select workload.sessionId, workload.endTime, workload.startTime, taskData.id " +
                "  from WorkloadData as workload join TaskData as taskData on taskData.id in (:ids)" +
                "    and workload.taskId=taskData.taskId and workload.sessionId=taskData.sessionId")
                .setParameter("ids", taskIds)
                .getResultList();

        // needs to determine whether specific task data has duration as custom metric
        List<BigInteger> taskDataIdsWithDurationDescriptions = (List<BigInteger>) entityManager.createNativeQuery(
                "select md.taskData_id from MetricDescriptionEntity md where md.taskData_id in (:taskIds) and md.metricId=:durationId")
                .setParameter("taskIds", taskIds)
                .setParameter("durationId", StandardMetricsNamesUtil.DURATION_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX)
                .getResultList();

        if (result.isEmpty()) {
            log.warn("Could not find data for {}", durationMetricNames);
            return Collections.emptySet();
        }

        return processDurationDataFromDatabase(result, durationMetricNames, taskDataIdsWithDurationDescriptions);
    }

    /**
     * @param rawData list of arrays as [sessionId, endTime, startTime, taskData_id] */
    private Set<SummarySingleDto> processDurationDataFromDatabase(
            List<Object[]> rawData,
            List<MetricNameDto> durationMetricNames,
            List<BigInteger> taskDataIdsWithDurationDescriptions) {

        Map<Long, Map<String, MetricNameDto>> mappedMetricDtos = MetricNameUtil.getMappedMetricDtos(durationMetricNames);

        Map<MetricNameDto, SummarySingleDto> resultMap = new HashMap<MetricNameDto, SummarySingleDto>();

        for (Object[] entry : rawData) {
            BigInteger taskId = (BigInteger) entry[3];

            if (taskDataIdsWithDurationDescriptions.contains(taskId)) {
                // For this particular test there is standard metric as custom metric
                continue;
            }

            Map<String, MetricNameDto> metricIdMap = mappedMetricDtos.get(taskId.longValue());
            if (metricIdMap == null) {
                throw new IllegalArgumentException("unknown task id in mapped metrics : " + taskId.longValue());
            }
            MetricNameDto metricNameDto = metricIdMap.get(StandardMetricsNamesUtil.DURATION_OLD_ID);
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
            Date[] date = new Date[2];
            date[0] = (Date)entry [1];
            date[1] = (Date)entry [2];
            value.setValueRepresentation(TimeUtils.formatDuration(date[0].getTime() - date[1].getTime()));
            value.setValue(String.valueOf((date[0].getTime() - date[1].getTime()) / 1000));
            value.setSessionId(Long.parseLong(String.valueOf(entry[0])));
            metricDto.getValues().add(value);
        }

        return new HashSet<SummarySingleDto>(resultMap.values());
    }
}
