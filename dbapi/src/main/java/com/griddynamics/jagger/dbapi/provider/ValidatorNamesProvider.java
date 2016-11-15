package com.griddynamics.jagger.dbapi.provider;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ValidatorNamesProvider implements MetricNameProvider {

    private Logger log = LoggerFactory.getLogger(ValidatorNamesProvider.class);

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Set<MetricNameDto> getMetricNames(List<TaskDataDto> tests) {
        Set<Long> taskIds = new HashSet<>();
        Set<String> sessionIds = new HashSet<>();
        for (TaskDataDto tdd : tests) {
            taskIds.addAll(tdd.getIds());
            sessionIds.addAll(tdd.getSessionIds());
        }

        long temp = System.currentTimeMillis();

        Set<MetricNameDto> validators = getValidatorsNamesNewModel(tests);
        if (validators == null) { // some exception occured

            List<Object[]> validatorNames = entityManager.createNativeQuery(
                    "SELECT v.validator, selected.taskdataID FROM ValidationResultEntity v JOIN " +
                            "(" +
                            "  SELECT wd.workloaddataID, td.taskdataID FROM " +
                            "    ( " +
                            "      SELECT wd.id AS workloaddataID, wd.taskId, wd.sessionId FROM WorkloadData wd WHERE wd.sessionId IN (:sessionIds)" +
                            "    ) AS wd JOIN   " +
                            "    ( " +
                            "      SELECT td.id AS taskdataID, td.taskId, td.sessionId FROM TaskData td WHERE td.id IN (:taskIds)" +
                            "    ) AS td ON wd.taskId=td.taskId AND wd.sessionId=td.sessionId" +
                            ") AS selected ON v.workloadData_id=selected.workloaddataID")
                    .setParameter("taskIds", taskIds)
                    .setParameter("sessionIds", sessionIds)
                    .getResultList();
            log.debug("{} ms spent for fetching {} validators", System.currentTimeMillis() - temp, validatorNames.size());

            validators = new HashSet<>(validatorNames.size());

            for (Object[] name : validatorNames) {
                if (name == null || name[0] == null) continue;
                for (TaskDataDto td : tests) {
                    if (td.getIds().contains(((BigInteger) name[1]).longValue())) {
                        MetricNameDto metric = new MetricNameDto();
                        metric.setTest(td);
                        metric.setMetricName((String) name[0]);
                        metric.setOrigin(MetricNameDto.Origin.VALIDATOR);
                        validators.add(metric);
                        break;
                    }
                }
            }
        }

        return validators;
    }


    public Set<MetricNameDto> getValidatorsNamesNewModel(List<TaskDataDto> tests) {
        try {
            Set<Long> taskIds = new HashSet<>();
            Set<String> sessionIds = new HashSet<>();
            for (TaskDataDto tdd : tests) {
                taskIds.addAll(tdd.getIds());
                sessionIds.addAll(tdd.getSessionIds());
            }

            long temp = System.currentTimeMillis();

            List<Object[]> validatorNames = entityManager.createNativeQuery(
                    "SELECT v.validator, selected.taskdataID, v.displayName FROM ValidationResultEntity v JOIN " +
                            "(" +
                            "  SELECT wd.workloaddataID, td.taskdataID FROM " +
                            "    ( " +
                            "      SELECT wd.id AS workloaddataID, wd.taskId, wd.sessionId FROM WorkloadData wd WHERE wd.sessionId IN (:sessionIds)" +
                            "    ) AS wd JOIN   " +
                            "    ( " +
                            "      SELECT td.id AS taskdataID, td.taskId, td.sessionId FROM TaskData td WHERE td.id IN (:taskIds)" +
                            "    ) AS td ON wd.taskId=td.taskId AND wd.sessionId=td.sessionId" +
                            ") AS selected ON v.workloadData_id=selected.workloaddataID")
                    .setParameter("taskIds", taskIds)
                    .setParameter("sessionIds", sessionIds)
                    .getResultList();
            log.debug("{} ms spent for fetching {} validators", System.currentTimeMillis() - temp, validatorNames.size());

            if (validatorNames.isEmpty()) {
                return Collections.emptySet();
            }
            Set<MetricNameDto> validators = new HashSet<>(validatorNames.size());

            for (Object[] name : validatorNames) {
                if (name == null || name[0] == null) continue;
                for (TaskDataDto td : tests) {
                    if (td.getIds().contains(((BigInteger) name[1]).longValue())) {
                        MetricNameDto metric = new MetricNameDto();
                        metric.setTest(td);
                        metric.setMetricName((String) name[0]);
                        metric.setMetricDisplayName((String) name[2]);
                        metric.setOrigin(MetricNameDto.Origin.VALIDATOR);
                        validators.add(metric);
                        break;
                    }
                }
            }

            return validators;
        } catch (PersistenceException e) {
            log.debug("Could not fetch validators names from new model of ValidationResultEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return null;
        }
    }

}
