package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import com.griddynamics.jagger.util.FormatCalculator;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

@Component
@Deprecated
public class CustomMetricSummaryFetcher extends DbMetricDataFetcher<SummarySingleDto> {

    @Override
    protected Set<SummarySingleDto> fetchData(List<MetricNameDto> metricNames) {

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
            return Collections.emptySet();
        }

        Map<MetricNameDto, SummarySingleDto> resultMap = new HashMap<MetricNameDto, SummarySingleDto>();
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
                SummarySingleDto metricDto = new SummarySingleDto();
                metricDto.setMetricName(metricNameDto);
                metricDto.setValues(new HashSet<SummaryMetricValueDto>());
                resultMap.put(metricNameDto, metricDto);
            }
            SummarySingleDto metricDto = resultMap.get(metricNameDto);

            if (mas[0] == null) continue;

            SummaryMetricValueDto value = new SummaryMetricValueDto();
            double val = ((Number) mas[0]).doubleValue();
            value.setValue(
                    new DecimalFormat(FormatCalculator.getNumberFormat(val), new DecimalFormatSymbols(Locale.ENGLISH))
                            .format(val)
            );

            value.setSessionId(Long.parseLong((String)mas[1]));
            metricDto.getValues().add(value);
        }

        return new HashSet<SummarySingleDto>(resultMap.values());
    }

    /**
     * @param taskIds ids of all tasks
     * @param metricIds identifiers of metric
     * @return list of object[] (value, sessionId, metricId, taskDataId)
     */
    @Deprecated
    protected List<Object[]> getCustomMetricsDataOldModel(Set<Long> taskIds, Set<String> metricIds) {
        return entityManager.createNativeQuery(
                "SELECT metric.total, taskData.sessionId, metric.name, taskData.taskDataId " +
                "FROM DiagnosticResultEntity AS metric " +
                "JOIN ( " +
                        "SELECT wd.sessionId, wd.id, taskData.id AS taskDataId " +
                        "FROM WorkloadData AS wd " +
                        "JOIN ( " +
                            "SELECT taskData.taskId, taskData.sessionId, taskData.id " +
                            "FROM TaskData AS taskData " +
                            "WHERE taskData.id IN (:ids) " +
                        ") AS taskData " +
                        "ON wd.sessionId=taskData.sessionId AND wd.taskId=taskData.taskId " +
                ") AS taskData " +
                "ON metric.workloadData_id=taskData.id AND metric.name IN (:metricIds);"
        ).setParameter("ids", taskIds).setParameter("metricIds", metricIds).getResultList();
    }


    /**
     * @param taskIds ids of all tasks
     * @param metricId identifier of metric
     * @return list of object[] (value, sessionId, metricId, taskDataId)
     */
    protected List<Object[]> getCustomMetricsDataNewModel(Set<Long> taskIds, Set<String> metricId) {
        try {
            if (taskIds.isEmpty() || metricId.isEmpty()){
                return Collections.emptyList();
            }

            return entityManager.createQuery(
                    "SELECT summary.total, summary.metricDescription.taskData.sessionId, summary.metricDescription.metricId, summary.metricDescription.taskData.id " +
                    "FROM MetricSummaryEntity AS summary " +
                    "WHERE summary.metricDescription.taskData.id IN (:ids) " +
                    "AND summary.metricDescription.metricId IN (:metricIds)")
                    .setParameter("ids", taskIds)
                    .setParameter("metricIds", metricId)
                    .getResultList();
        } catch (PersistenceException e) {
            log.debug("Could not fetch metric summary values from MetricSummaryEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.emptyList();
        }
    }
}
