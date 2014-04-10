package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.MetricDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.MetricValueDto;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import com.griddynamics.jagger.util.TimeUtils;

import java.math.BigInteger;
import java.util.*;

public class DurationMetricSummaryFetcher extends SummaryDbMetricDataFetcher {

    @Override
    protected Set<MetricDto> fetchData(List<MetricNameDto> durationMetricNames) {
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

    private static final String DURATION_METRIC_ID = "duration";

    private Set<MetricDto> processDurationDataFromDatabase(List<Object[]> rawData, List<MetricNameDto> durationMetricNames) {

        Map<Long, Map<String, MetricNameDto>> mappedMetricDtos = MetricNameUtil.getMappedMetricDtos(durationMetricNames);

        Map<MetricNameDto, MetricDto> resultMap = new HashMap<MetricNameDto, MetricDto>();

        for (Object[] entry : rawData) {
            BigInteger taskId = (BigInteger) entry[3];
            Map<String, MetricNameDto> metricIdMap = mappedMetricDtos.get(taskId.longValue());
            if (metricIdMap == null) {
                throw new IllegalArgumentException("unknown task id in mapped metrics : " + taskId.longValue());
            }
            MetricNameDto metricNameDto = metricIdMap.get(DURATION_METRIC_ID);
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
            Date[] date = new Date[2];
            date[0] = (Date)entry [1];
            date[1] = (Date)entry [2];
            value.setValueRepresentation(TimeUtils.formatDuration(date[0].getTime() - date[1].getTime()));
            value.setValue(String.valueOf((date[0].getTime() - date[1].getTime()) / 1000));
            value.setSessionId(Long.parseLong(String.valueOf(entry[0])));
            metricDto.getValues().add(value);
        }

        for (MetricDto md : resultMap.values()) {
            md.setPlotSeriesDtos(generatePlotSeriesDto(md));
        }

        return new HashSet<MetricDto>(resultMap.values());
    }
}
