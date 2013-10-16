package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import com.griddynamics.jagger.webclient.server.DefaultWorkloadParameters;
import com.griddynamics.jagger.webclient.server.LegendProvider;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class TimeLatencyPercentilePlotDataProvider implements PlotDataProvider {
    private LegendProvider legendProvider;
    private EntityManager entityManager;

    @Required
    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public PlotNameSeriesDto getPlotData(PlotNameDto plotNameDto) {

        Set<Long> taskIds = plotNameDto.getTaskIds();
        String plotName = plotNameDto.getPlotName();

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

            plotDatasetDtoList.addAll(assemble(rawData, taskData.getSessionId(), true));
        }

        return new PlotNameSeriesDto(
                plotNameDto,
                Collections.singletonList(
                        new PlotSeriesDto(plotDatasetDtoList,
                                "Time, sec", "", legendProvider.getPlotHeader(taskIds, plotName)
                        )
                )
        );
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> findAllTimeInvocationStatisticsByTaskData(long taskId) {
        return entityManager.createQuery(
                "select tis.time, ps.percentileKey, ps.percentileValue from TimeLatencyPercentile as ps inner join ps.timeInvocationStatistics as tis where tis.taskData.id=:taskId")
                .setParameter("taskId", taskId).getResultList();
    }

    private List<PlotDatasetDto> assemble(List<Object[]> rawData, String sessionId, boolean addSessionPrefix) {
        List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();
        Map<String, List<PointDto>> percentiles = new TreeMap<String, List<PointDto>>();
        double previousPercentileValue = 0.0;
        for (Object[] raw : rawData) {
            if (percentiles.get(raw[1].toString()) == null) {
                percentiles.put(raw[1].toString(), new ArrayList<PointDto>(rawData.size()));
            }
            List<PointDto> list = percentiles.get(raw[1].toString());

            double x = DataProcessingUtil.round((Long) raw[0] / 1000.0D);
            double y = DataProcessingUtil.round(((Double) raw[2] - previousPercentileValue) / 1000);
            list.add(new PointDto(x, y));

            previousPercentileValue = y;
        }

        for (Map.Entry<String, List<PointDto>> entry : percentiles.entrySet()) {
            DefaultWorkloadParameters parameter = DefaultWorkloadParameters.fromDescription(entry.getKey());
            String description = (parameter == null ? entry.getKey() : parameter.getDescription());
            String legend = legendProvider.generatePlotLegend(sessionId, description, addSessionPrefix);
            plotDatasetDtoList.add(new PlotDatasetDto(entry.getValue(), legend, ColorCodeGenerator.getHexColorCode()));
        }

        return plotDatasetDtoList;
    }
}
