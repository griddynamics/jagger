package com.griddynamics.jagger.dbapi.util;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetricNameUtil {

    /**
     * Map Collection of MetricNameDto first of all by TaskDataDto id, after all by metric name (metric id)
     *
     * @param metricNameDtos collection of MetricNameDto to be mapped
     * @return Map &lt;TaskDataDto.id, Map&lt;metricName, MetricDto&gt;&gt;
     */
    public static Map<Long, Map<String, MetricNameDto>> getMappedMetricDtos(Collection<MetricNameDto> metricNameDtos) {
        Map<Long, Map<String, MetricNameDto>> taskIdMap = new HashMap<>();
        for (MetricNameDto metricName : metricNameDtos) {
            Set<Long> ids = metricName.getTaskIds();
            for (Long id : ids) {
                if (!taskIdMap.containsKey(id)) {
                    taskIdMap.put(id, new HashMap<>());
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
     *
     * @param metricNameDtos collection of MetricNameDto to be mapped
     * @return Map &lt;TaskDataDto.id, MetricNameDto&gt;
     */
    public static Map<Long, MetricNameDto> getMappedMetricDtosByTaskIds(Collection<MetricNameDto> metricNameDtos) {
        Map<Long, MetricNameDto> taskIdMap = new HashMap<>();
        for (MetricNameDto metricName : metricNameDtos) {
            Set<Long> ids = metricName.getTaskIds();
            ids.stream().filter(id -> !taskIdMap.containsKey(id)).forEach(id -> taskIdMap.put(id, metricName));
        }
        return taskIdMap;
    }

    /**
     * Returns a set of MetricNameDto objects contained in metricNodes
     *
     * @param metricNodes collection of MetricNode objects
     * @return Set of MetricNameDto objects containing in metricNodes
     */
    public static Set<MetricNameDto> getMetricNameDtoSet(Collection<MetricNode> metricNodes) {
        Set<MetricNameDto> metricNameDtoSet = new HashSet<>();

        for (MetricNode metricNode : metricNodes) {
            metricNameDtoSet.addAll(metricNode.getMetricNameDtoList());
        }

        return metricNameDtoSet;
    }
}
