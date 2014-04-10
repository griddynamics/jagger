package com.griddynamics.jagger.dbapi.util;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;

import java.util.*;

public class MetricNameUtil {

    /**
     * Map Collection of MetricNameDto first of all by TaskDataDto id, after all by metric name (metric id)
     * @param metricNameDtos collection of MetricNameDto to be mapped
     * @return Map <TaskDataDto.id, Map<metricName, MetricDto>>
     */
    public static Map<Long, Map<String, MetricNameDto>> getMappedMetricDtos(Collection<MetricNameDto> metricNameDtos) {
        Map<Long, Map<String, MetricNameDto>> taskIdMap = new HashMap<Long, Map<String, MetricNameDto>>();
        for (MetricNameDto metricName : metricNameDtos) {
            Set<Long> ids = metricName.getTaskIds();
            for (Long id : ids) {
                if (!taskIdMap.containsKey(id)) {
                    taskIdMap.put(id, new HashMap<String, MetricNameDto>());
                }
                Map<String, MetricNameDto> metricIdMap = taskIdMap.get(id);
                if (!metricIdMap.containsKey(metricName.getMetricName())) {
                    metricIdMap.put(metricName.getMetricName(), metricName);
                } else {
                    throw new IllegalStateException(metricName.toString() + " already in Map");
                }
            }
        }
        return taskIdMap;
    }

    /**
     * Map Collection of MetricNameDto first of all by TaskDataDto id, after all by metric name (metric id)
     * @param metricNameDtos collection of MetricNameDto to be mapped
     * @return Map <TaskDataDto.id, MetricNameDto>
     */
    public static Map<Long, MetricNameDto> getMappedMetricDtosByTaskIds(Collection<MetricNameDto> metricNameDtos) {
        Map<Long, MetricNameDto> taskIdMap = new HashMap<Long, MetricNameDto>();
        for (MetricNameDto metricName : metricNameDtos) {
            Set<Long> ids = metricName.getTaskIds();
            for (Long id : ids) {
                if (!taskIdMap.containsKey(id)) {
                    taskIdMap.put(id, metricName);
                }
            }
        }
        return taskIdMap;
    }

    public static String getLatencyMetricName(double latencyKey) {
        return "Latency " + latencyKey + " %";
    }
}
