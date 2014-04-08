package com.griddynamics.jagger.dbapi.provider;

import com.google.common.collect.Multimap;
import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.util.CommonUtils;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import com.griddynamics.jagger.dbapi.util.FetchUtil;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by kgribov on 4/7/14.
 */
public class CustomMetricNameProvider {
    private Logger log = LoggerFactory.getLogger(CustomMetricNameProvider.class);

    private EntityManager entityManager;
    private FetchUtil fetchUtil;
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Required
    public void setFetchUtil(FetchUtil fetchUtil) {
        this.fetchUtil = fetchUtil;
    }

    @Required
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    ///////////////////////
    /**
     * Fetch custom metrics names from database
     * @param tests tests data
     * @return set of MetricNameDto representing name of metric
     */
    public Set<MetricNameDto> getCustomMetricsNames(List<TaskDataDto> tests){

        Set<MetricNameDto>  metrics = new HashSet<MetricNameDto>();

        long temp = System.currentTimeMillis();

        metrics.addAll(getCustomMetricsNamesNewModel(tests));

        metrics.addAll(getCustomTestGroupMetricsNamesNewModel(tests));

        metrics.addAll(getCustomMetricsNamesOldModel(tests));

        log.debug("{} ms spent for fetching {} custom metrics", System.currentTimeMillis() - temp, metrics.size());

        return metrics;
    }

    private Set<MetricNameDto> getCustomMetricsNamesOldModel(List<TaskDataDto> tests) {
        Set<Long> taskIds = new HashSet<Long>();
        Set<String> sessionIds = new HashSet<String>();
        for (TaskDataDto tdd : tests) {
            taskIds.addAll(tdd.getIds());
            sessionIds.addAll(tdd.getSessionIds());
        }

        long temp = System.currentTimeMillis();

        // check old data (before jagger 1.2.4 version)
        List<Object[]> metricNames = entityManager.createNativeQuery(
                "select dre.name, selected.id from DiagnosticResultEntity dre join " +
                        " (" +
                        "  select wd.workloaddataID, td.id from " +
                        "    (" +
                        "     select wd.id as workloaddataID, wd.sessionId, wd.taskId from WorkloadData wd where wd.sessionId in (:sessionIds)" +
                        "    ) as wd join   " +
                        "    ( " +
                        "     select td.taskId, td.sessionId, td.id from TaskData td where td.id in (:taskIds)" +
                        "    ) as td on wd.taskId=td.taskId and wd.sessionId=td.sessionId" +
                        " ) as selected on dre.workloadData_id=selected.workloaddataID")
                .setParameter("taskIds", taskIds)
                .setParameter("sessionIds", sessionIds)
                .getResultList();

        if (metricNames.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<MetricNameDto> metrics = new HashSet<MetricNameDto>(metricNames.size());

        log.debug("{} ms spent for fetching {} custom metrics", System.currentTimeMillis() - temp, metricNames.size());

        for (Object[] name : metricNames){
            if (name == null || name[0] == null) continue;
            for (TaskDataDto td : tests) {
                if (td.getIds().contains(((BigInteger)name[1]).longValue())) {
                    MetricNameDto metric = new MetricNameDto();
                    metric.setTest(td);
                    metric.setMetricName((String) name[0]);
                    metric.setOrigin(MetricNameDto.Origin.METRIC);
                    metrics.add(metric);
                    break;
                }
            }
        }

        return metrics;

    }

    private Set<MetricNameDto> getCustomMetricsNamesNewModel(List<TaskDataDto> tests) {
        try {
            Set<Long> taskIds = CommonUtils.getTestsIds(tests);

            List<Object[]> metricDescriptionEntities = fetchUtil.getMetricNames(taskIds);

            if (metricDescriptionEntities.isEmpty()) {
                return Collections.EMPTY_SET;
            }

            Set<MetricNameDto>  metrics = new HashSet<MetricNameDto>(metricDescriptionEntities.size());

            for (Object[] mde : metricDescriptionEntities) {
                for (TaskDataDto td : tests) {
                    if (td.getIds().contains((Long) mde[2])) {
                        metrics.add(new MetricNameDto(td, (String) mde[0], (String) mde[1], MetricNameDto.Origin.METRIC));
                        break;
                    }
                }
            }

            return metrics;
        } catch (PersistenceException e) {
            log.debug("Could not fetch data from MetricSummaryEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.EMPTY_SET;
        }
    }

    private Set<MetricNameDto> getCustomTestGroupMetricsNamesNewModel(List<TaskDataDto> tests) {
        try {
            Set<Long> taskIds = CommonUtils.getTestsIds(tests);

            Multimap<Long, Long> testGroupMap = fetchUtil.getTestsInTestGroup(taskIds);

            List<Object[]> metricDescriptionEntities = fetchUtil.getMetricNames(testGroupMap.keySet());

            metricDescriptionEntities = CommonUtils.filterMonitoring(metricDescriptionEntities, monitoringPlotGroups);

            if (metricDescriptionEntities.isEmpty()) {
                return Collections.EMPTY_SET;
            }

            Set<MetricNameDto>  metrics = new HashSet<MetricNameDto>(metricDescriptionEntities.size());

            // add test-group metric names
            for (Object[] mde : metricDescriptionEntities){
                for (TaskDataDto td : tests){
                    Collection<Long> allTestsInGroup = testGroupMap.get((Long)mde[2]);
                    if (CommonUtils.containsAtLeastOne(td.getIds(), allTestsInGroup)){
                        metrics.add(new MetricNameDto(td, (String)mde[0], (String)mde[1], MetricNameDto.Origin.TEST_GROUP_METRIC));
                    }
                }
            }

            return metrics;
        } catch (PersistenceException e) {
            log.debug("Could not fetch test-group data from MetricSummaryEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.EMPTY_SET;
        }
    }
}
