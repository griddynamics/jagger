package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import com.griddynamics.jagger.webclient.server.DefaultWorkloadParameters;
import com.griddynamics.jagger.webclient.server.LegendProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class ThroughputPlotDataProvider implements PlotDataProvider {
    private LegendProvider legendProvider;
    private EntityManager entityManager;

    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
        checkArgument(taskId > 0, "taskId is not valid; it's lesser or equal 0");
        checkNotNull(plotName, "plotName is null");

        List<Object[]> rawData = findAllTimeInvocationStatisticsByTaskData(taskId);

        if (rawData == null) {
            return Collections.emptyList();
        }

        TaskData taskData = entityManager.find(TaskData.class, taskId);
        Set<PlotDatasetDto> plotSeries = new HashSet<PlotDatasetDto>();
        plotSeries.add(assemble(rawData, taskData.getSessionId(), false));

        PlotSeriesDto plotSeriesDto = new PlotSeriesDto(plotSeries, "Time, sec", "", legendProvider.generatePlotHeader(taskData, plotName));

        return Collections.singletonList(plotSeriesDto);
    }

    @Override
    public List<PlotSeriesDto> getPlotData(Set<Long> taskIds, String plotName) {
        checkNotNull(taskIds, "taskIds is null");
        checkArgument(!taskIds.isEmpty(), "taskIds is empty");
        checkNotNull(plotName, "plotName is null");

        List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>(taskIds.size());
        for (long taskId : taskIds) {
            List<Object[]> rawData = findAllTimeInvocationStatisticsByTaskData(taskId);

            if (rawData == null) {
                continue;
            }

            TaskData taskData = entityManager.find(TaskData.class, taskId);
            plotDatasetDtoList.add(assemble(rawData, taskData.getSessionId(), true));
        }

        return Collections.singletonList(new PlotSeriesDto(plotDatasetDtoList, "Time, sec", "", legendProvider.getPlotHeader(taskIds, plotName)));
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> findAllTimeInvocationStatisticsByTaskData(long taskId) {
        return entityManager.createQuery(
                "select tis.time, tis.throughput from TimeInvocationStatistics as tis where tis.taskData.id=:taskId")
                .setParameter("taskId", taskId).getResultList();
    }

    private PlotDatasetDto assemble(List<Object[]> rawData, String sessionId, boolean addSessionPrefix) {
        List<PointDto> pointDtoList = DataProcessingUtil.convertFromRawDataToPointDto(rawData, 0, 1);

        String legend = legendProvider.generatePlotLegend(sessionId, DefaultWorkloadParameters.THROUGHPUT.getDescription(), addSessionPrefix);
        return new PlotDatasetDto(pointDtoList, legend, ColorCodeGenerator.getHexColorCode());
    }
}
