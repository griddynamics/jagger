package com.griddynamics.jagger.dbapi.provider;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.entity.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kgribov on 4/7/14.
 */
@Component
@Deprecated
public class LatencyMetricNameProvider implements MetricNameProvider {
    private Logger log = LoggerFactory.getLogger(LatencyMetricNameProvider.class);

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Deprecated
    public Set<MetricNameDto> getMetricNames(List<TaskDataDto> tests) {
        Set<MetricNameDto> latencyNames;

        Set<Long> testIds = new HashSet<>();
        for (TaskDataDto tdd : tests) {
            testIds.addAll(tdd.getIds());
        }

        long temp = System.currentTimeMillis();
        List<WorkloadProcessLatencyPercentile> latency = entityManager.createQuery(
                "select s from  WorkloadProcessLatencyPercentile as s where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) ")
                .setParameter("taskIds", testIds)
                .getResultList();


        log.debug("{} ms spent for Latency Percentile fetching (size ={})", System.currentTimeMillis() - temp, latency.size());

        latencyNames = new HashSet<>(latency.size());

        if (!latency.isEmpty()) {
            for (WorkloadProcessLatencyPercentile percentile : latency) {
                for (TaskDataDto tdd : tests) {

                    if (tdd.getIds().contains(percentile.getWorkloadProcessDescriptiveStatistics().getTaskData().getId())) {
                        MetricNameDto dto = new MetricNameDto();
                        String metricName = StandardMetricsNamesUtil.getLatencyMetricName(percentile.getPercentileKey(), true);
                        String metricNameSynonym = StandardMetricsNamesUtil.getLatencyMetricName(percentile.getPercentileKey(), false);
                        dto.setMetricName(metricName);
                        dto.setMetricDisplayName(metricNameSynonym); // done on purpose to have equal display names for old and new model
                        dto.setTest(tdd);
                        dto.setMetricNameSynonyms(Collections.singletonList(metricNameSynonym));
                        dto.setOrigin(MetricNameDto.Origin.LATENCY_PERCENTILE);
                        latencyNames.add(dto);
                        break;
                    }
                }
            }
        }
        return latencyNames;
    }
}
