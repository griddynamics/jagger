package com.griddynamics.jagger.dbapi.provider;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class StandardMetricNameProvider implements MetricNameProvider {
    private Logger log = LoggerFactory.getLogger(StandardMetricNameProvider.class);
    private List<MetricNameDto> standardMetricNameDtoList;

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Resource
    public void setStandardMetricNameDtoList(List<MetricNameDto> standardMetricNameDtoList) {
        this.standardMetricNameDtoList = standardMetricNameDtoList;
    }

    @Override
    public Set<MetricNameDto> getMetricNames(List<TaskDataDto> tests) {
        Set<MetricNameDto> result;

        Set<Long> taskIds = new HashSet<Long>();
        for (TaskDataDto tdd : tests) {
            taskIds.addAll(tdd.getIds());
        }

        long temp = System.currentTimeMillis();

        List<Object[]> standardMetrics = entityManager.createNativeQuery(
                "select wtd.avgLatency, wtd.failuresCount, wtd.samples, wtd.stdDevLatency, wtd.successRate, wtd.throughput, td.id " +
                "        from WorkloadTaskData as wtd join TaskData as td on td.id in (:ids)" +
                "         and wtd.taskId=td.taskId and wtd.sessionId=td.sessionId")
                .setParameter("ids", taskIds)
                .getResultList();


        log.debug("{} ms spent for Standard metrics names fetching (size ={})", System.currentTimeMillis() - temp, standardMetrics.size());

        if (standardMetrics.isEmpty()) {
            return Collections.emptySet();
        }

        result = new HashSet<MetricNameDto>();

        for (Object[] values : standardMetrics) {
            for (MetricNameDto metricNameDto : standardMetricNameDtoList) {
                // skip creation of metric name dtos when values are not available
                if ((metricNameDto.getMetricName().equals(StandardMetricsNamesUtil.LATENCY_OLD_ID)) &&
                        (values[0] == null)) {
                    continue;
                }
                if ((metricNameDto.getMetricName().equals(StandardMetricsNamesUtil.FAIL_COUNT_OLD_ID)) &&
                        (values[1] == null)) {
                    continue;
                }
                if ((metricNameDto.getMetricName().equals(StandardMetricsNamesUtil.ITERATION_SAMPLES_OLD_ID)) &&
                        (values[2] == null)) {
                    continue;
                }
                if ((metricNameDto.getMetricName().equals(StandardMetricsNamesUtil.LATENCY_STD_DEV_OLD_ID)) &&
                        (values[3] == null)) {
                    continue;
                }
                if ((metricNameDto.getMetricName().equals(StandardMetricsNamesUtil.SUCCESS_RATE_OLD_ID)) &&
                        (values[4] == null)) {
                    continue;
                }
                if ((metricNameDto.getMetricName().equals(StandardMetricsNamesUtil.THROUGHPUT_OLD_ID)) &&
                        (values[5] == null)) {
                    continue;
                }
                for (TaskDataDto test : tests) {
                    if (test.getIds().contains(((BigInteger)values[6]).longValue())) {
                        MetricNameDto metric = new MetricNameDto();
                        metric.setMetricName(metricNameDto.getMetricName());
                        metric.setMetricDisplayName(metricNameDto.getMetricDisplayName());
                        metric.setOrigin(metricNameDto.getOrigin());
                        metric.setTest(test);
                        List<String> synonyms = StandardMetricsNamesUtil.getSynonyms(metricNameDto.getMetricName());
                        if (synonyms != null) {
                            metric.setMetricNameSynonyms(synonyms);
                        }
                        result.add(metric);
                        break;
                    }
                }
            }
        }

        return result;
    }
}
