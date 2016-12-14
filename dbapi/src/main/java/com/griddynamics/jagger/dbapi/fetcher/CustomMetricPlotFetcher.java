package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CustomMetricPlotFetcher extends AbstractMetricPlotFetcher {

    @Override
    protected Collection<MetricRawData> getAllRawData(List<MetricNameDto> metricNames) {

        Set<String> metricIds = new HashSet<>();
        Set<Long> taskIds = new HashSet<>();

        for (MetricNameDto metricName : metricNames) {
            metricIds.add(metricName.getMetricName());
            taskIds.addAll(metricName.getTaskIds());
        }

        List<Object[]> rawData = getRawData(taskIds, metricIds);

        if (rawData.isEmpty()) {
            log.warn("No plot data found for metrics {} within taskData ids {}", metricIds, taskIds);
            return Collections.emptyList();
        }

        List<MetricRawData> resultList = new ArrayList<>();
        for (Object[] objects : rawData) {
            MetricRawData metricRawData = new MetricRawData();

            metricRawData.setWorkloadTaskDataId(((Number) objects[0]).longValue());
            metricRawData.setTime((Long) objects[1]);
            metricRawData.setValue((Double) objects[2]);
            metricRawData.setSessionId((String) objects[3]);
            metricRawData.setMetricId((String) objects[4]);

            resultList.add(metricRawData);
        }

        return resultList;
    }

    /**
     * @return collection of objects {Task Data id, time, value, sessionId, metricId}
     */
    protected List<Object[]> getRawData(Set<Long> taskDataIds, Set<String> metricIds) {

        if (taskDataIds.isEmpty() || metricIds.isEmpty()) {
            log.warn("Empty data for getRawData() method {}; {}" + taskDataIds, metricIds);
            return Collections.emptyList();
        }
        List<Object[]> resultList = new ArrayList<>();
        resultList.addAll(getPlotDataNewModel(taskDataIds, metricIds));
        return resultList;
    }

    /**
     * @return collection of objects {Task Data id, time, value, sessionId, metricId}
     */
    protected Collection<? extends Object[]> getPlotDataNewModel(Set<Long> taskIds, Set<String> metricIds) {
        try {
            return entityManager.createQuery(
                    "SELECT mpe.metricDescription.taskData.id, mpe.time, mpe.value, mpe.metricDescription.taskData.sessionId, " +
                            "mpe.metricDescription.metricId " +
                            "from MetricPointEntity AS mpe " +
                            "WHERE mpe.metricDescription.taskData.id IN (:taskIds) " +
                            "AND mpe.metricDescription.metricId IN (:metricIds)")
                    .setParameter("taskIds", taskIds)
                    .setParameter("metricIds", metricIds)
                    .getResultList();
        } catch (Exception e) {
            log.debug("Could not fetch metric plots from MetricPointEntity: ", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.emptyList();
        }
    }
}
