package com.griddynamics.jagger.webclient.server.plot;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 7/12/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomMetricPlotDataProvider implements PlotDataProvider{

    Logger log = LoggerFactory.getLogger(CustomMetricPlotDataProvider.class);

    private LegendProvider legendProvider;
    private EntityManager entityManager;

    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public List<MetricNameDto> getPlotNames(TaskDataDto taskDataDto){
        List<String> plotNames = entityManager.createNativeQuery("select metricDetails.metric from MetricDetails metricDetails " +
                                                                 "where taskData_id in (:ids) " +
                                                                 "group by metricDetails.metric")
                                    .setParameter("ids", taskDataDto.getIds())
                                    .getResultList();
        if (plotNames.isEmpty())
            return Collections.emptyList();

        ArrayList<MetricNameDto> result = new ArrayList<MetricNameDto>(plotNames.size());

        for (String plotName : plotNames){
            if (plotName != null)
                result.add(new MetricNameDto(taskDataDto, plotName));
        }

        return result;
    }

    public Set<MetricNameDto> getPlotNames(List<TaskDataDto> taskDataDtos){

        long temp = System.currentTimeMillis();
        Set<MetricNameDto> result = new HashSet<MetricNameDto>();

        result.addAll(getPlotNamesNewModel(taskDataDtos));

        result.addAll(getPlotNamesOldModel(taskDataDtos));

        log.debug("{} ms spent to fetch custom metrics plots names in count of {}", System.currentTimeMillis() - temp, result.size());

        return result;
    }


    public Set<MetricNameDto> getPlotNamesOldModel(List<TaskDataDto> taskDataDtos){

        Set<Long> testIds = new HashSet<Long>();
        for (TaskDataDto tdd : taskDataDtos) {
            testIds.addAll(tdd.getIds());
        }

        // check old model (before jagger 1.2.4)
        List<Object[]> plotNames = entityManager.createNativeQuery("select metricDetails.metric, metricDetails.taskData_id from MetricDetails metricDetails " +
                "where metricDetails.taskData_id in (:ids) " +
                "group by metricDetails.metric, metricDetails.taskData_id")
                .setParameter("ids", testIds)
                .getResultList();


        if (plotNames.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<MetricNameDto> result = new HashSet<MetricNameDto>(plotNames.size());

        for (Object[] plotName : plotNames){
            if (plotName != null) {
                for (TaskDataDto tdd : taskDataDtos) {
                    if (tdd.getIds().contains(((BigInteger)plotName[1]).longValue())) {
                        MetricNameDto metricNameDto = new MetricNameDto(tdd, (String)plotName[0]);
                        metricNameDto.setOrigin(MetricNameDto.Origin.METRIC);
                        result.add(metricNameDto);
                    }
                }
            }
        }

        return result;
    }


    public Set<MetricNameDto> getPlotNamesNewModel(List<TaskDataDto> taskDataDtos){
        try {
            return MetricNameProvider.getMetricNames(entityManager, taskDataDtos, new MetricDescriptionFetcher() {
                @Override
                public List<Object[]> getTestsMetricDescriptions(Set<Long> ids) {
                    return fetchDescriptions(ids);
                }

                @Override
                public List<Object[]> getTestGroupsMetricDescriptions(Set<Long> ids) {
                    return fetchDescriptions(ids);
                }

                private List<Object[]> fetchDescriptions(Set<Long> ids){
                    return entityManager.createQuery(
                            "select mpe.metricDescription.metricId, mpe.metricDescription.displayName, mpe.metricDescription.taskData.id " +
                                    "from MetricPointEntity as mpe where mpe.metricDescription.taskData.id in (:taskIds) group by mpe.metricDescription.id")
                            .setParameter("taskIds", ids)
                            .getResultList();
                }
            });
        } catch (PersistenceException e) {
            log.debug("Could not fetch metric plot names from MetricPointEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.EMPTY_SET;
        }

    }

    @Override
    public List<PlotSeriesDto> getPlotData(MetricNameDto metricNameDto) {

        String displayName = metricNameDto.getMetricDisplayName();

        long temp = System.currentTimeMillis();

        // check new way
        List<Object[]> metricValues = new ArrayList<Object[]>();

        metricValues.addAll(getPlotDataNewModel(metricNameDto));

        // check old way
        metricValues.addAll(getPlotDataOldModel(metricNameDto));

        log.debug("Fetch metric plots in count of {} in: {}", metricValues.size(), (System.currentTimeMillis() - temp));

        if (metricValues.isEmpty())
            return Collections.emptyList();

        Multimap<Long, Object[]> metrics = ArrayListMultimap.create(metricNameDto.getTaskIds().size(), metricValues.size());
        List<PlotDatasetDto> plots = new ArrayList<PlotDatasetDto>();

        for (Object[] metricDetails : metricValues){
            metrics.put((Long)metricDetails[0], metricDetails);
        }

        for (Long id : metrics.keySet()){
            Collection<Object[]> taskMetrics = metrics.get(id);
            List<PointDto> points = new ArrayList<PointDto>(taskMetrics.size());
            String sessionId = null;
            //TaskData taskData = null;

            for (Object[] metricDetails : taskMetrics){
                //if (taskData == null) taskData = metricDetails.getTaskData();
                if (sessionId == null) sessionId = (String)metricDetails[3];
                points.add(new PointDto((Long)metricDetails[1] / 1000D, Double.parseDouble(metricDetails[2].toString())));
            }

            PlotDatasetDto plotDatasetDto = new PlotDatasetDto(points, legendProvider.generatePlotLegend(sessionId, displayName, true), ColorCodeGenerator.getHexColorCode());
            plots.add(plotDatasetDto);
        }

        PlotSeriesDto plotSeriesDto = new PlotSeriesDto(plots, "Time, sec", "", legendProvider.getPlotHeader(metricNameDto.getTaskIds(), displayName));

        return Arrays.asList(plotSeriesDto);
    }

    /**
     *
     * @param metricNameDto identifier of metric.
     * @return List of objects (taskData.id, time, value, sessionId)
     */
    public List<Object[]> getPlotDataNewModel(MetricNameDto metricNameDto) {
        try {
            return entityManager.createQuery(
                "select mpe.metricDescription.taskData.id, mpe.time, mpe.value, mpe.metricDescription.taskData.sessionId from MetricPointEntity as mpe " +
                        "where mpe.metricDescription.taskData.id in (:taskIds) and mpe.metricDescription.metricId=:metricId")
                .setParameter("taskIds", metricNameDto.getTaskIds())
                .setParameter("metricId", metricNameDto.getMetricName())
                .getResultList();
        } catch (Exception e) {
            log.debug("Could not fetch metric plots from MetricPointEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.EMPTY_LIST;
        }

    }

    /**
     *
     * @param metricNameDto identifier of metric.
     * @return List of objects (taskData.id, time, value, sessionId)
     */
    public List<Object[]> getPlotDataOldModel(MetricNameDto metricNameDto) {
        return entityManager.createQuery(
                "select metrics.taskData.id, metrics.time, metrics.value, metrics.taskData.sessionId from MetricDetails metrics " +
                        "where metrics.metric=:plotName and metrics.taskData.id in (:taskIds)")
                .setParameter("taskIds", metricNameDto.getTaskIds())
                .setParameter("plotName", metricNameDto.getMetricName())
                .getResultList();

    }
}
