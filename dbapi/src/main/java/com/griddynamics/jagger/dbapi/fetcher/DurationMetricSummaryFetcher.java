package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import com.griddynamics.jagger.util.TimeUtils;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;

@Component
@Deprecated
public class DurationMetricSummaryFetcher extends DbMetricDataFetcher<SummarySingleDto> {

    @Override
    protected Set<SummarySingleDto> fetchData(List<MetricNameDto> durationMetricNames) {
        if (durationMetricNames.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<Long> taskIds = new HashSet<Long>();

        for (MetricNameDto metricName : durationMetricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        List<Object[]> result = entityManager.createNativeQuery("select workload.sessionId, workload.endTime, workload.startTime, taskData.id " +
                "  from WorkloadData as workload join TaskData as taskData on taskData.id in (:ids)" +
                "    and workload.taskId=taskData.taskId and workload.sessionId=taskData.sessionId")
                .setParameter("ids", taskIds)
                .getResultList();


        if (result.isEmpty()) {
            log.warn("Could not find data for {}", durationMetricNames);
            return Collections.EMPTY_SET;
        }

        return processDurationDataFromDatabase(result, durationMetricNames);
    }

    private Set<SummarySingleDto> processDurationDataFromDatabase(List<Object[]> rawData, List<MetricNameDto> durationMetricNames) {

        Map<Long, Map<String, MetricNameDto>> mappedMetricDtos = MetricNameUtil.getMappedMetricDtos(durationMetricNames);

        Map<MetricNameDto, SummarySingleDto> resultMap = new HashMap<MetricNameDto, SummarySingleDto>();

        for (Object[] entry : rawData) {
            BigInteger taskId = (BigInteger) entry[3];
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
