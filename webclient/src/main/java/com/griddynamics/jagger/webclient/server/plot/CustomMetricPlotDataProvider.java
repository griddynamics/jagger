package com.griddynamics.jagger.webclient.server.plot;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.CollectorDescription;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.MetricDetails;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.LegendProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    private LegendProvider legendProvider;
    private EntityManager entityManager;

    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public boolean isAvailable(String plotName) {

        // check new model
        Number count = (Long)entityManager.createQuery("select count(id) FROM MetricDetails where collectorDescription.name=:plotName")
                .setParameter("plotName", plotName)
                .getSingleResult();

        if (count.intValue() == 0) {
            // check old model
            count = (BigInteger)entityManager.createNativeQuery("select count(id) FROM MetricDetails where metric=:plotName")
                            .setParameter("plotName", plotName)
                            .getSingleResult();

        }

        if (count.intValue() > 0)
            return true;

        return false;
    }

    public List<PlotNameDto> getPlotNames(TaskDataDto taskDataDto){

        List<PlotNameDto> result;

        // check new model
        List<CollectorDescription> collectorDescriptions = entityManager.createQuery("select d.collectorDescription from DiagnosticResultEntity d where d.collectorDescription.taskData.id in (:ids)")
                .setParameter("ids", taskDataDto.getIds()).getResultList();

        if (!collectorDescriptions.isEmpty()) {
            result = new ArrayList<PlotNameDto>(collectorDescriptions.size());

            for (CollectorDescription collectorDescription : collectorDescriptions){

                Long count = (Long)entityManager.createQuery("select count(id) FROM MetricDetails where collectorDescription=:description")
                        .setParameter("description", collectorDescription)
                        .getSingleResult();

                if (count > 0)
                    result.add(new PlotNameDto(taskDataDto, collectorDescription.getName(), collectorDescription.getDisplayName()));
            }
        } else {

            // check old model
            List<String> plotNames = entityManager.createNativeQuery("select metricDetails.metric from MetricDetails metricDetails " +
                                                                     "where taskData_id in (:ids) " +
                                                                     "group by metricDetails.metric")
                                        .setParameter("ids", taskDataDto.getIds())
                                        .getResultList();
            if (plotNames.isEmpty())
                return Collections.emptyList();

            result = new ArrayList<PlotNameDto>(plotNames.size());

            for (String plotName : plotNames){
                if (plotName != null)
                result.add(new PlotNameDto(taskDataDto, plotName));
            }
        }

        return result;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
        return getPlotData(new HashSet<Long>(Arrays.asList(taskId)), plotName);
    }

    @Override
    public List<PlotSeriesDto> getPlotData(Set<Long> taskId, String plotName) {

        // check new model
        List<MetricDetails> metricValues = entityManager.createQuery("select metrics from MetricDetails metrics " +
                "where metrics.collectorDescription.name=:plotName and metrics.collectorDescription.taskData.id in (:taskIds)",
                MetricDetails.class)
                .setParameter("taskIds", taskId)
                .setParameter("plotName", plotName)
                .getResultList();


        // check old model
        metricValues.addAll(
                entityManager.createNativeQuery("select * from MetricDetails metrics " +
                                                                       "where metrics.metric=:plotName and metrics.taskData_id in (:taskIds)",
                                                                        MetricDetails.class)
                                        .setParameter("taskIds", taskId)
                                        .setParameter("plotName", plotName)
                                        .getResultList()
        );

        if (metricValues.isEmpty())
            return Collections.emptyList();

        String displayName = metricValues.get(0).getDisplay();

        System.out.println("displayName = " + displayName);

        Multimap<Long, MetricDetails> metrics = ArrayListMultimap.create(taskId.size(), metricValues.size());
        List<PlotDatasetDto> plots = new ArrayList<PlotDatasetDto>();

        for (MetricDetails metricDetails : metricValues){
            if (metricDetails.getCollectorDescription() == null)
                metrics.put(metricDetails.getTaskData().getId(), metricDetails);
            else {
                metrics.put(metricDetails.getCollectorDescription().getTaskData().getId(), metricDetails);
            }
        }

        for (Long id : metrics.keySet()){
            Collection<MetricDetails> taskMetrics = metrics.get(id);
            List<PointDto> points = new ArrayList<PointDto>(taskMetrics.size());
            TaskData taskData = null;

            for (MetricDetails metricDetails : taskMetrics){
                if (taskData == null) {
                    if (metricDetails.getCollectorDescription() == null)
                        taskData = metricDetails.getTaskData();
                    else
                        taskData = metricDetails.getCollectorDescription().getTaskData();
                }
                points.add(new PointDto(metricDetails.getTime() / 1000D, metricDetails.getValue()));
            }

            PlotDatasetDto plotDatasetDto = new PlotDatasetDto(points, legendProvider.generatePlotLegend(taskData.getSessionId(), displayName, true), ColorCodeGenerator.getHexColorCode());
            plots.add(plotDatasetDto);
        }

        PlotSeriesDto plotSeriesDto = new PlotSeriesDto(plots, "Time, sec", "", legendProvider.getPlotHeader(taskId, displayName));

        return Arrays.asList(plotSeriesDto);
    }
}
