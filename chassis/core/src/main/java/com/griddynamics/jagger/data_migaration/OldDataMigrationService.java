package com.griddynamics.jagger.data_migaration;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.dbapi.entity.*;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import com.griddynamics.jagger.util.statistics.StatisticsCalculator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.END_TIME;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.START_TIME;

/**
 * Created by aantonenko on 9/12/16.
 */

@SuppressWarnings("Duplicates")
@Service
public class OldDataMigrationService extends HibernateDaoSupport {

    private static final Logger log = LoggerFactory.getLogger(OldDataMigrationService.class);

    private final List<Double> timeWindowPercentilesKeys = ImmutableList.of(40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 95.0, 99.0);

    private final StatisticsCalculator globalStatisticsCalculator = new StatisticsCalculator();

    @PersistenceContext
    private EntityManager entityManager;

    public void migrateStatistics() {
        List<MetricPointEntity> newStatistics = new ArrayList<>();

        for (TaskData taskData : getAllTaskData()) {
            collectTimeInvocationStatistics(taskData, newStatistics);
            persistWorkloadProcessDescriptiveStatistics(taskData);
        }
        persistNewStatistics(newStatistics);
    }

    private void collectTimeInvocationStatistics(TaskData taskData, List<MetricPointEntity> newStatistics) {
        List<TimeInvocationStatistics> timeInvocationStatistics = getTimeInvocationStatistics(taskData);
        Map<Double, MetricDescriptionEntity> percentileMap = getPercentileMap(taskData);
        MetricDescriptionEntity throughputDescription = getThroughputDescription(taskData);
        MetricDescriptionEntity latencyDescription = getLatencyDescription(taskData);
        MetricDescriptionEntity latencyStdDevDescription = getLatencyStdDeviationDescription(taskData);

        for (TimeInvocationStatistics tis : timeInvocationStatistics) {
            newStatistics.add(new MetricPointEntity(tis.getTime(), tis.getThroughput(), throughputDescription));
            newStatistics.add(new MetricPointEntity(tis.getTime(), tis.getLatency(), latencyDescription));
            newStatistics.add(new MetricPointEntity(tis.getTime(), tis.getLatencyStdDev(), latencyStdDevDescription));

            for (TimeLatencyPercentile percentile : tis.getPercentiles()) {
                Double key = percentile.getPercentileKey();
                Double value = percentile.getPercentileValue() / 1000D;
                newStatistics.add(new MetricPointEntity(tis.getTime() * 2, value, percentileMap.get(key)));
            }
        }
    }

    private void persistWorkloadProcessDescriptiveStatistics(TaskData taskData) {
        Map<Double, MetricDescriptionEntity> percentileMap = getPercentileMap(taskData);
        List<WorkloadProcessDescriptiveStatistics> workloadProcessDescriptiveStatistics = getWorkloadProcessDescriptiveStatistics(taskData);
        MetricDescriptionEntity throughputDescription = getThroughputDescription(taskData);
        MetricDescriptionEntity latencyDescription = getLatencyDescription(taskData);
        MetricDescriptionEntity latencyStdDevDescription = getLatencyStdDeviationDescription(taskData);

        for (WorkloadProcessDescriptiveStatistics workloadProcessDescriptiveStatistic : workloadProcessDescriptiveStatistics) {
            for (WorkloadProcessLatencyPercentile pp : workloadProcessDescriptiveStatistic.getPercentiles()) {
                persistAggregatedMetricValue(Math.rint(pp.getPercentileValue()) / 1000D, percentileMap.get(pp.getPercentileKey()));
            }

            persistAggregatedMetricValue(Math.rint(globalStatisticsCalculator.getMean()) / 1000D, latencyDescription);
            persistAggregatedMetricValue(Math.rint(globalStatisticsCalculator.getStandardDeviation()) / 1000D, latencyStdDevDescription);


            /*Long startTime = (Long) keyValueStorage.fetchNotNull(taskNamespace, START_TIME);
            Long endTime = (Long) keyValueStorage.fetchNotNull(taskNamespace, END_TIME);

            double duration = (double) (endTime - startTime) / 1000;
            double totalThroughput = Math.rint(totalCount / duration * 100) / 100;*/

            //persistAggregatedMetricValue(Math.rint(totalThroughput * 100) / 100, throughputDescription);
        }
    }

    private MetricDescriptionEntity getLatencyDescription(TaskData taskData) {
        return persistMetricDescription(
                StandardMetricsNamesUtil.LATENCY_ID,
                StandardMetricsNamesUtil.LATENCY_SEC,
                taskData);
    }

    private MetricDescriptionEntity getLatencyStdDeviationDescription(TaskData taskData) {
        return persistMetricDescription(
                    StandardMetricsNamesUtil.LATENCY_STD_DEV_ID,
                    StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC,
                    taskData);
    }

    private MetricDescriptionEntity getThroughputDescription(TaskData taskData) {
        return persistMetricDescription(
                    StandardMetricsNamesUtil.THROUGHPUT_ID,
                    StandardMetricsNamesUtil.THROUGHPUT_TPS,
                    taskData);
    }

    private Map<Double, MetricDescriptionEntity> getPercentileMap(TaskData taskData) {
        Map<Double, MetricDescriptionEntity> percentileMap = new HashMap<>(timeWindowPercentilesKeys.size());
        for (Double percentileKey : timeWindowPercentilesKeys) {
            String metricStr = StandardMetricsNamesUtil.getLatencyMetricName(percentileKey, false);
            percentileMap.put(percentileKey, persistMetricDescription(metricStr, metricStr, taskData));
        }
        return percentileMap;
    }

    private List<WorkloadProcessDescriptiveStatistics> getWorkloadProcessDescriptiveStatistics(TaskData taskData) {
        return entityManager.createQuery("select s from  WorkloadProcessDescriptiveStatistics as s " +
                "where s.taskData.id = :taskId ", WorkloadProcessDescriptiveStatistics.class)
                .setParameter("taskId", taskData.getId())
                .getResultList();
    }

    private List<TimeInvocationStatistics> getTimeInvocationStatistics(TaskData taskData) {
        return entityManager
                .createQuery("select t from TimeInvocationStatistics as t where t.taskData.id = :taskId", TimeInvocationStatistics.class)
                .setParameter("taskId", taskData.getId())
                .getResultList();
    }

    private List<TaskData> getAllTaskData() {
        return entityManager.createQuery("select t from TaskData as t", TaskData.class).getResultList();
    }

    private void persistNewStatistics(final List<MetricPointEntity> newStatistics) {
        log.info("BEGIN: Save to data base");
        getHibernateTemplate().execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                newStatistics.forEach(session::persist);
                session.flush();
                return null;
            }
        });
        log.info("END: Save to data base");
    }

    private MetricDescriptionEntity persistMetricDescription(String metricId, String displayName, TaskData taskData) {
        MetricDescriptionEntity metricDescription = new MetricDescriptionEntity();
        metricDescription.setMetricId(metricId);
        metricDescription.setDisplayName(displayName);
        metricDescription.setTaskData(taskData);
        getHibernateTemplate().persist(metricDescription);
        return metricDescription;
    }

    private void persistAggregatedMetricValue(Number value, MetricDescriptionEntity md) {
        MetricSummaryEntity entity = new MetricSummaryEntity();
        entity.setTotal(value.doubleValue());
        entity.setMetricDescription(md);
        getHibernateTemplate().persist(entity);
    }

}
