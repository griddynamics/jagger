package com.griddynamics.jagger.dbapi.fetcher;


import com.griddynamics.jagger.dbapi.dto.MetricDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.MetricValueDto;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;

import javax.persistence.PersistenceException;
import java.text.DecimalFormat;
import java.util.*;

public class CustomMetricSummaryFetcher extends SummaryDbMetricDataFetcher {

    @Override
    protected Set<MetricDto> fetchData(List<MetricNameDto> metricNames) {

        //custom metric

        Set<Long> taskIds = new HashSet<Long>();
        Set<String> metricIds = new HashSet<String>();
        for (MetricNameDto metricName : metricNames) {
            taskIds.addAll(metricName.getTaskIds());
            metricIds.add(metricName.getMetricName());
        }

        List<Object[]> metrics = new ArrayList<Object[]>();

        // check old model
        metrics.addAll(getCustomMetricsDataOldModel(taskIds, metricIds));

        // check new model
        metrics.addAll(getCustomMetricsDataNewModel(taskIds, metricIds));

        if (metrics.isEmpty()){
            log.warn("Could not find data for {}", metricNames);
            return Collections.EMPTY_SET;
        }

        Map<MetricNameDto, MetricDto> resultMap = new HashMap<MetricNameDto, MetricDto>();
        Map<Long, Map<String, MetricNameDto>> mappedMetricDtos = MetricNameUtil.getMappedMetricDtos(metricNames);

        for (Object[] mas : metrics){

            Number taskDataId = (Number)mas[3];
            String metricId = (String)mas[2];

            MetricNameDto metricNameDto;
            try {
                metricNameDto = mappedMetricDtos.get(taskDataId.longValue()).get(metricId);
                if (metricNameDto == null) {   // means that we fetched data that we had not wanted to fetch
                    continue;
                }
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("could not find appropriate MetricDto : " + taskDataId + " : " + metricId);
            }

            if (!resultMap.containsKey(metricNameDto)) {
                MetricDto metricDto = new MetricDto();
                metricDto.setMetricName(metricNameDto);
                metricDto.setValues(new HashSet<MetricValueDto>());
                resultMap.put(metricNameDto, metricDto);
            }
            MetricDto metricDto = resultMap.get(metricNameDto);

            if (mas[0] == null) continue;

            MetricValueDto value = new MetricValueDto();
            value.setValue(new DecimalFormat("0.0###").format(mas[0]));

            value.setSessionId(Long.parseLong((String)mas[1]));
            metricDto.getValues().add(value);
        }

        for (MetricDto md : resultMap.values()) {
            md.setPlotSeriesDtos(generatePlotSeriesDto(md));
        }

        return new HashSet<MetricDto>(resultMap.values());
    }

    /**
     * @param taskIds ids of all tasks
     * @param metricIds identifiers of metric
     * @return list of object[] (value, sessionId, metricId, taskDataId)
     */
    protected List<Object[]> getCustomMetricsDataOldModel(Set<Long> taskIds, Set<String> metricIds) {
        return entityManager.createNativeQuery(
                "select metric.total, taskData.sessionId, metric.name, taskData.taskDataId from DiagnosticResultEntity as metric join " +
                        "  (" +
                        "    select wd.sessionId, wd.id, taskData.id as taskDataId from WorkloadData wd join " +
                        "      (" +
                        "        select taskData.taskId, taskData.sessionId, taskData.id from TaskData as taskData where taskData.id in (:ids)" +
                        "      ) as taskData on wd.sessionId=taskData.sessionId and wd.taskId=taskData.taskId" +
                        "  ) as taskData on metric.workloadData_id=taskData.id and metric.name in (:metricIds);  ")
                .setParameter("ids", taskIds)
                .setParameter("metricIds", metricIds)
                .getResultList();
    }


    /**
     * @param taskIds ids of all tasks
     * @param metricId identifier of metric
     * @return list of object[] (value, sessionId, metricId, taskDataId)
     */
    protected List<Object[]> getCustomMetricsDataNewModel(Set<Long> taskIds, Set<String> metricId) {
        try {
            if (taskIds.isEmpty() || metricId.isEmpty()){
                return Collections.EMPTY_LIST;
            }

            return entityManager.createQuery(
                    "select summary.total, summary.metricDescription.taskData.sessionId, summary.metricDescription.metricId, summary.metricDescription.taskData.id" +
                            " from MetricSummaryEntity as summary" +
                            " where summary.metricDescription.taskData.id in (:ids) and summary.metricDescription.metricId in (:metricIds)")
                    .setParameter("ids", taskIds)
                    .setParameter("metricIds", metricId)
                    .getResultList();
        } catch (PersistenceException e) {
            log.debug("Could not fetch metric summary values from MetricSummaryEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.EMPTY_LIST;
        }
    }

}
