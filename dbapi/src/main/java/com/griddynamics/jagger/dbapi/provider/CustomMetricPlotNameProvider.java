package com.griddynamics.jagger.dbapi.provider;

import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.dbapi.util.CommonUtils;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import com.griddynamics.jagger.dbapi.util.FetchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: kgribov
 * Date: 7/12/13
 * Time: 1:45 PM
 */
@Component
public class CustomMetricPlotNameProvider {

    private Logger log = LoggerFactory.getLogger(CustomMetricPlotNameProvider.class);

    private FetchUtil fetchUtil;
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setFetchUtil(FetchUtil fetchUtil) {
        this.fetchUtil = fetchUtil;
    }

    @Resource
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public Set<MetricNameDto> getPlotNames(List<TaskDataDto> taskDataDtos) {

        final long temp = System.currentTimeMillis();
        Set<MetricNameDto> result = new HashSet<>();

        result.addAll(getPlotNamesNewModel(taskDataDtos));

        result.addAll(getTestGroupPlotNamesNewModel(taskDataDtos));

        log.debug("{} ms spent to fetch custom metrics plots names in count of {}", System.currentTimeMillis() - temp, result.size());

        return result;
    }

    public Set<MetricNameDto> getPlotNamesNewModel(List<TaskDataDto> taskDataDtos) {

        try {
            Set<Long> testIds = CommonUtils.getTestsIds(taskDataDtos);

            List<Object[]> plotNamesNew = getMetricNames(testIds);

            if (plotNamesNew.isEmpty()) {
                return Collections.emptySet();
            }

            Set<MetricNameDto> result = new HashSet<>(plotNamesNew.size());

            for (Object[] plotName : plotNamesNew) {
                if (plotName != null) {
                    for (TaskDataDto tdd : taskDataDtos) {
                        if (tdd.getIds().contains((Long) plotName[2])) {
                            String metricName = (String) plotName[0];
                            MetricNameDto metricNameDto = new MetricNameDto(tdd, metricName, (String) plotName[1], MetricNameDto.Origin.METRIC);
                            result.add(metricNameDto);
                        }
                    }
                }
            }

            return result;
        } catch (PersistenceException e) {
            log.debug("Could not fetch metric plot names from MetricPointEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.emptySet();
        }

    }

    public Set<MetricNameDto> getTestGroupPlotNamesNewModel(List<TaskDataDto> tests) {

        try {
            Set<Long> testIds = CommonUtils.getTestsIds(tests);

            Multimap<Long, Long> testGroupMap = fetchUtil.getTestGroupIdsByTestIds(testIds);

            List<Object[]> plotNamesNew = getMetricNames(testGroupMap.keySet());

            plotNamesNew = CommonUtils.filterMonitoring(plotNamesNew, monitoringPlotGroups);

            if (plotNamesNew.isEmpty()) {
                return Collections.emptySet();
            }

            Set<MetricNameDto> result = new HashSet<>(plotNamesNew.size());

            for (Object[] mde : plotNamesNew) {
                for (TaskDataDto td : tests) {
                    Collection<Long> allTestsInGroup = testGroupMap.get((Long) mde[2]);
                    if (CommonUtils.containsAtLeastOne(td.getIds(), allTestsInGroup)) {
                        result.add(new MetricNameDto(td, (String) mde[0], (String) mde[1], MetricNameDto.Origin.TEST_GROUP_METRIC));
                        // we should create new MetricNameDto with another origin, because we need it for Session Scope plots
                        result.add(new MetricNameDto(td, (String) mde[0], (String) mde[1], MetricNameDto.Origin.SESSION_SCOPE_TG));
                    }
                }
            }

            return result;
        } catch (PersistenceException e) {
            log.debug("Could not fetch test-group metric plot names from MetricPointEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.emptySet();
        }
    }

    private List<Object[]> getMetricNames(Set<Long> testIds) {
        if (testIds.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager.createQuery(
                "select mpe.metricDescription.metricId, mpe.metricDescription.displayName, mpe.metricDescription.taskData.id " +
                        "from MetricPointEntity as mpe where mpe.metricDescription.taskData.id in (:taskIds) group by mpe.metricDescription.id")
                .setParameter("taskIds", testIds)
                .getResultList();
    }
}
