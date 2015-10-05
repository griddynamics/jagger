package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * Returns set of MetricNameDto objects containing in metricNodes
     * @param metricNodes collection of MetricNode objects
     * @return Set of MetricNameDto objects containing in metricNodes */
    public static Set<MetricNameDto> getMetricNameDtoSet(Collection<MetricNode> metricNodes) {
        Set<MetricNameDto> metricNameDtoSet = new HashSet<MetricNameDto>();

        for (MetricNode metricNode : metricNodes) {
            metricNameDtoSet.addAll(metricNode.getMetricNameDtoList());
        }

        return metricNameDtoSet;
    }

    /**
     * Combines SummarySingleDto into single SummarySingleDto using synonyms.
     *
     * @throws java.lang.NullPointerException if ${code rowList} is null.
     */
    public static List<SummarySingleDto> combineSynonyms(List<SummarySingleDto> rowList) {

        List<SummarySingleDto> resultList = new ArrayList<SummarySingleDto>();
        Map<String, List<SummarySingleDto>> groupMap = new HashMap<String, List<SummarySingleDto>>();
        for (SummarySingleDto summarySingleDto: rowList) {
            if (summarySingleDto.getMetricName().getMetricNameSynonyms() == null) {
                // no synonyms, just add to result list
                resultList.add(summarySingleDto);
            } else {
                List<String> synonyms = summarySingleDto.getMetricName().getMetricNameSynonyms();
                boolean grouped = false;
                for (String synonym: synonyms) {
                    if (groupMap.containsKey(synonym)) {
                        // update entry with new summarySingleDto
                        groupMap.get(synonym).add(summarySingleDto);
                        grouped = true;
                        break;
                    }
                }
                if (!grouped) {
                    // new entry to the map
                    groupMap.put(summarySingleDto.getMetricName().getMetricName(), Lists.newArrayList(summarySingleDto));
                }
            }
        }


        if (!groupMap.isEmpty()) {
            // add combined summary:
            for (List<SummarySingleDto> groupedSummaries: groupMap.values()) {
                if (groupedSummaries.size() == 1) {
                    resultList.add(groupedSummaries.get(0));
                    continue;
                }
                // generate new combined summary
                SummarySingleDto combinedSummary = new SummarySingleDto();

                // use first MetricNameDto
                combinedSummary.setMetricName(groupedSummaries.get(0).getMetricName());

                Set<SummaryMetricValueDto> combinedValues = new HashSet<SummaryMetricValueDto>();
                for (SummarySingleDto ssd: groupedSummaries) {
                    combinedValues.addAll(ssd.getValues());
                }
                combinedSummary.setValues(combinedValues);

                resultList.add(combinedSummary);
            }
        }

        return resultList;
    }
}
