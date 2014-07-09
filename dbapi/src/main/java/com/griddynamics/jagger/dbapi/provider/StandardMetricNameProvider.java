package com.griddynamics.jagger.dbapi.provider;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

public class StandardMetricNameProvider implements MetricNameProvider, MetricPlotNameProvider {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Set<MetricNameDto> getMetricNames(List<TaskDataDto> tests) {
        return null;
    }


    @Override
    public Set<MetricNameDto> getPlotNames(List<TaskDataDto> taskDataDtos) {

        Set<MetricNameDto> result = new HashSet<MetricNameDto>();

        result.addAll(getStandardMetricNames(taskDataDtos));
        result.addAll(getTimeLatencyPercentileNames(taskDataDtos));

        return result;
    }

    private List<MetricNameDto> getTimeLatencyPercentileNames(List<TaskDataDto> taskDataDtos) {

        try {
            Set<Long> taskIds = getTaskIds(taskDataDtos);

            @SuppressWarnings("all")
            List<Object[]> rawDataList =  entityManager.createQuery(
                    "select distinct ps.percentileKey, tis.taskData.id from TimeLatencyPercentile as ps " +
                            "inner join ps.timeInvocationStatistics as tis where tis.taskData.id in (:taskIds)")
                    .setParameter("taskIds", taskIds).getResultList();

            if (rawDataList.isEmpty()) {
                return Collections.emptyList();
            }

            List<MetricNameDto> result = new ArrayList<MetricNameDto>();

            for (Object[] objects : rawDataList) {
                Double percentileKey = (Double) objects[0];
                Long taskDataId = (Long) objects[1];

                for (TaskDataDto taskDataDto : taskDataDtos) {
                    if (taskDataDto.getIds().contains(taskDataId)) {
                        MetricNameDto latencyPercentile = new MetricNameDto(
                                taskDataDto,
                                StandardMetricsNamesUtil.getLatencyMetricName(percentileKey),
                                null,
                                MetricNameDto.Origin.LATENCY_PERCENTILE
                        );
                        latencyPercentile.setMetricNameSynonyms(Arrays.asList(StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE));

                        result.add(latencyPercentile);
                        break;
                    }
                }
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private List<MetricNameDto> getStandardMetricNames(List<TaskDataDto> taskDataDtos) {

        try {
            Set<Long> taskIds = getTaskIds(taskDataDtos);

            // get Throughput and Latency metrics
            List<Long> taskIdsWithTIS = entityManager.createQuery(
                    "select tis.taskData.id from TimeInvocationStatistics as tis " +
                            "where tis.taskData.id in (:taskIds)"
            )
                    .setParameter("taskIds", taskIds)
                    .getResultList();


            if (taskIdsWithTIS.isEmpty()) {
                return Collections.emptyList();
            }

            List<MetricNameDto> result = new ArrayList<MetricNameDto>();

            for (TaskDataDto taskDataDto : taskDataDtos) {
                for (Long l: taskIdsWithTIS) {
                    if (taskDataDto.getIds().contains(l)) {
                        // add throughput latency metricNames
                        MetricNameDto throughput = new MetricNameDto(
                                taskDataDto,
                                StandardMetricsNamesUtil.THROUGHPUT_ID,
                                StandardMetricsNamesUtil.THROUGHPUT_TPS,
                                MetricNameDto.Origin.THROUGHPUT_LATENCY);
                        throughput.setMetricNameSynonyms(Arrays.asList(StandardMetricsNamesUtil.THROUGHPUT));

                        MetricNameDto latency = new MetricNameDto(
                                taskDataDto,
                                StandardMetricsNamesUtil.LATENCY_ID,
                                StandardMetricsNamesUtil.LATENCY_SEC,
                                MetricNameDto.Origin.THROUGHPUT_LATENCY);
                        latency.setMetricNameSynonyms(Arrays.asList(StandardMetricsNamesUtil.LATENCY));

                        MetricNameDto latencyStdDev = new MetricNameDto(
                                taskDataDto,
                                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID,
                                StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC,
                                MetricNameDto.Origin.THROUGHPUT_LATENCY);
                        latency.setMetricNameSynonyms(Arrays.asList(StandardMetricsNamesUtil.LATENCY));


                        result.add(throughput);
                        result.add(latency);
                        result.add(latencyStdDev);
                        break;
                    }
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Set<Long> getTaskIds(Collection<TaskDataDto> taskDataDtos) {
        Set<Long> result = new HashSet<Long>();
        for (TaskDataDto taskDataDto : taskDataDtos) {
            result.addAll(taskDataDto.getIds());
        }
        return result;
    }
}
