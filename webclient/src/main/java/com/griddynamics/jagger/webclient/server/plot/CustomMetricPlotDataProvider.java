package com.griddynamics.jagger.webclient.server.plot;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
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
        BigInteger count = (BigInteger)entityManager.createNativeQuery("select count(id) FROM MetricDetails where metric=:plotName")
                            .setParameter("plotName", plotName)
                            .getSingleResult();

        if (count.intValue() > 0)
            return true;

        return false;
    }

    public List<PlotNameDto> getPlotNames(TaskDataDto taskDataDto){
        List<MetricDetails> metricDetails = entityManager.createNativeQuery("select * from MetricDetails metricDetails " +
                                                                 "where taskData_id in (:ids) " +
                                                                 "group by metricDetails.metric", MetricDetails.class)
                                    .setParameter("ids", taskDataDto.getIds())
                                    .getResultList();
        if (metricDetails.isEmpty())
            return Collections.emptyList();

        ArrayList<PlotNameDto> result = new ArrayList<PlotNameDto>(metricDetails.size());

        for (MetricDetails metricDetail : metricDetails){
            result.add(new PlotNameDto(taskDataDto, metricDetail.getMetric(), metricDetail.getDisplayName()));
        }

        return result;
    }

    @Override
    public PlotNameSeriesDto getPlotData(PlotNameDto plotNameDto) {

        String plotName = plotNameDto.getPlotName();
        String displayName = plotNameDto.getDisplayName();
        Set<Long> taskIds = plotNameDto.getTaskIds();

        List<MetricDetails> metricValues = entityManager.createNativeQuery("select * from MetricDetails metrics " +
                                                                           "where metrics.metric=:plotName and metrics.taskData_id in (:taskIds)",
                                                                            MetricDetails.class)
                                            .setParameter("taskIds", taskIds)
                                            .setParameter("plotName", plotName)
                                            .getResultList();

        if (metricValues.isEmpty()) {
            return new PlotNameSeriesDto(plotNameDto, Collections.EMPTY_LIST);
        }

        // displayName == plotName means that there is no displayName actually.
        // so try to get it - may be there is no displayName because plotNameDto came from link
        if (displayName == plotName) {
            displayName = metricValues.iterator().next().getDisplayName();
            plotNameDto.setDisplayName(displayName);
        }

        Multimap<Long, MetricDetails> metrics = ArrayListMultimap.create(taskIds.size(), metricValues.size());
        List<PlotDatasetDto> plots = new ArrayList<PlotDatasetDto>();

        for (MetricDetails metricDetails : metricValues){
            metrics.put(metricDetails.getTaskData().getId(), metricDetails);
        }

        for (Long id : metrics.keySet()){
            Collection<MetricDetails> taskMetrics = metrics.get(id);
            List<PointDto> points = new ArrayList<PointDto>(taskMetrics.size());
            TaskData taskData = null;

            for (MetricDetails metricDetails : taskMetrics){
                if (taskData == null) taskData = metricDetails.getTaskData();
                points.add(new PointDto(metricDetails.getTime() / 1000D, (double)metricDetails.getValue()));
            }

            PlotDatasetDto plotDatasetDto = new PlotDatasetDto(points, legendProvider.generatePlotLegend(taskData.getSessionId(), displayName, true), ColorCodeGenerator.getHexColorCode());
            plots.add(plotDatasetDto);
        }

        PlotSeriesDto plotSeriesDto = new PlotSeriesDto(plots, "Time, sec", "", legendProvider.getPlotHeader(taskIds, displayName));

        return new PlotNameSeriesDto(plotNameDto, Arrays.asList(plotSeriesDto));
    }
}
