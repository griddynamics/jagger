package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.plot.CustomMetricPlotDataProvider;
import com.griddynamics.jagger.webclient.server.plot.DataPointCompressingProcessor;
import com.griddynamics.jagger.webclient.server.plot.PlotDataProvider;
import com.griddynamics.jagger.webclient.server.plot.SessionScopePlotDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl implements PlotProviderService {
    private static final Logger log = LoggerFactory.getLogger(PlotProviderServiceImpl.class);

    private EntityManager entityManager;

    private DataPointCompressingProcessor compressingProcessor;
    private Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups;
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;
    private Map<String, PlotDataProvider> workloadPlotDataProviders;
    private Map<String, PlotDataProvider> monitoringPlotDataProviders;
    private CustomMetricPlotDataProvider customMetricPlotDataProvider;

    //==========Setters

    @Required
    public void setCompressingProcessor(DataPointCompressingProcessor compressingProcessor) {
        this.compressingProcessor = compressingProcessor;
    }

    @Required
    public void setWorkloadPlotGroups(Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups) {
        this.workloadPlotGroups = workloadPlotGroups;
    }

    @Required
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    @Required
    public void setWorkloadPlotDataProviders(Map<String, PlotDataProvider> workloadPlotDataProviders) {
        this.workloadPlotDataProviders = workloadPlotDataProviders;
    }

    @Required
    public void setMonitoringPlotDataProviders(Map<String, PlotDataProvider> monitoringPlotDataProviders) {
        this.monitoringPlotDataProviders = monitoringPlotDataProviders;
    }

    @Required
    public void setCustomMetricPlotDataProvider(CustomMetricPlotDataProvider customMetricPlotDataProvider) {
        this.customMetricPlotDataProvider = customMetricPlotDataProvider;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    //===========================
    //===========Contract Methods
    //===========================

    @Override
    public Set<PlotNameDto> getTaskScopePlotList(Set<String> sessionIds, TaskDataDto taskDataDto) {
        Set<PlotNameDto> plotNameDtoSet = new LinkedHashSet<PlotNameDto>();
        try {
            if (isWorkloadStatisticsAvailable(sessionIds, taskDataDto)) {
                for (Map.Entry<GroupKey, DefaultWorkloadParameters[]> monitoringPlot : workloadPlotGroups.entrySet()) {
                    plotNameDtoSet.add(new PlotNameDto(taskDataDto, monitoringPlot.getKey().getUpperName()));
                }
            }

            for (String sessionId : sessionIds) {
                if (isMonitoringStatisticsAvailable(sessionId)) {
                    for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : monitoringPlotGroups.entrySet()) {
                        plotNameDtoSet.add(new PlotNameDto(taskDataDto, monitoringPlot.getKey().getUpperName()));
                    }
                }
            }

            List<PlotNameDto> customMetrics = customMetricPlotDataProvider.getPlotNames(taskDataDto);
            for (PlotNameDto plotNameDto : customMetrics){
                plotNameDtoSet.add(plotNameDto);
            }

            log.debug("For sessions {} are available these plots: {}", sessionIds, plotNameDtoSet);
        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds + ", task name " + taskDataDto.getTaskName(), e);
            throw new RuntimeException(e);
        }

        return plotNameDtoSet;
    }

    @Override
    public Set<String> getSessionScopePlotList(String sessionId) {
        Set<String> plotNameDtoSet = null;
        try {
            if (!isMonitoringStatisticsAvailable(sessionId)) {
                return Collections.emptySet();
            }

            plotNameDtoSet = new LinkedHashSet<String>();

            for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : monitoringPlotGroups.entrySet()) {
                plotNameDtoSet.add(monitoringPlot.getKey().getUpperName());
            }
        } catch (Exception e) {
            log.error("Error was occurred during session scope plots data getting for session ID " + sessionId, e);
            throw new RuntimeException(e);
        }

        return plotNameDtoSet;
    }

    @Override
    public PlotNameSeriesDto getPlotData(PlotNameDto plotName) {
        long timestamp = System.currentTimeMillis();
        log.debug("getPlotData was invoked with taskIds={} and plotName={}", plotName.getTaskIds(), plotName.getPlotName());

        PlotDataProvider plotDataProvider = findPlotDataProvider(plotName.getPlotName());

        PlotNameSeriesDto plotNameSeriesDto = null;
        try {
            plotNameSeriesDto = plotDataProvider.getPlotData(plotName);
            log.info("getPlotData(): {}", getFormattedLogMessage(plotNameSeriesDto.getPlotSeriesDto(), "" + plotName.getTaskIds(), plotName.getPlotName(), System.currentTimeMillis() - timestamp));
        } catch (Exception e) {
            log.error("Error is occurred during plot data loading for taskIds=" + plotName.getTaskIds() + ", plotName=" + plotName, e);
            throw new RuntimeException(e);
        }

        return plotNameSeriesDto;
    }

    @Override
    public List<PlotNameSeriesDto> getPlotDatas(Set<PlotNameDto> plots) throws IllegalArgumentException{
        List<PlotNameSeriesDto>  result = new ArrayList<PlotNameSeriesDto> (plots.size());
        for (PlotNameDto plot : plots){
            result.add(getPlotData(plot));
        }
        return result;
    }


    @Override
    public Map<String,List<PlotSeriesDto>> getSessionScopePlotData(String sessionId, Collection<String> plotNames) {
        long timestamp = System.currentTimeMillis();
        Map<String, List<PlotSeriesDto>> resultMap = new HashMap<String, List<PlotSeriesDto>>();

        for(String plotName : plotNames) {
            log.debug("getPlotData was invoked with sessionId={} and plotName={}", sessionId, plotName);
            List<PlotSeriesDto> plotSeriesDtoList;

            SessionScopePlotDataProvider plotDataProvider = (SessionScopePlotDataProvider) monitoringPlotDataProviders.get(plotName);
            if (plotDataProvider == null) {
                log.warn("getPlotData was invoked with unsupported plotName={}", plotName);
                throw new UnsupportedOperationException("Plot type " + plotName + " doesn't supported");
            }


            try {
                plotSeriesDtoList = plotDataProvider.getPlotData(sessionId, plotName);
                log.info("getSessionScopePlotData(): {}", getFormattedLogMessage(plotSeriesDtoList, sessionId, plotName, System.currentTimeMillis() - timestamp));
                for (PlotSeriesDto plotSeriesDto : plotSeriesDtoList) {
                    for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                        List<PointDto> pointDtoList = compressingProcessor.process(plotDatasetDto.getPlotData());
                        plotDatasetDto.getPlotData().clear();
                        plotDatasetDto.getPlotData().addAll(pointDtoList);
                    }
                }
                log.info("getSessionScopePlotData() after compressing: {}", getFormattedLogMessage(plotSeriesDtoList, sessionId, plotName, System.currentTimeMillis() - timestamp));
            } catch (Exception e) {
                log.error("Error is occurred during plot data loading for sessionId=" + sessionId + ", plotName=" + plotName, e);
                throw new RuntimeException(e);
            }
            resultMap.put(plotName, plotSeriesDtoList);
        }

        return resultMap;
    }

    //===========================
    //==========Auxiliary Methods
    //===========================

    private String getFormattedLogMessage(List<PlotSeriesDto> plotSeriesDto, String id, String plotName, long millis) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("For id=")
                .append(id)
                .append(", plot name=\"")
                .append(plotName)
                .append("\" ")
                .append(plotSeriesDto.size())
                .append(" plots were found: ");
        for (PlotSeriesDto dto : plotSeriesDto) {
            logBuilder.append("\n* \"").append(dto.getPlotHeader()).append("\" {");

            int summaryPointsCount = 0;
            for (PlotDatasetDto plotDatasetDto : dto.getPlotSeries()) {
                summaryPointsCount += plotDatasetDto.getPlotData().size();
                logBuilder.append("\"")
                        .append(plotDatasetDto.getLegend())
                        .append("\" [")
                        .append(plotDatasetDto.getPlotData().size())
                        .append(" fetched data points], ");
            }
            logBuilder.append("} //Summary: ").append(summaryPointsCount).append(" points;");
            logBuilder.append("\nExecuted for ").append(millis).append(" ms");
        }

        return logBuilder.toString();
    }

    private boolean isMonitoringStatisticsAvailable(String sessionId) {
        long timestamp = System.currentTimeMillis();
        long monitoringStatisticsCount = (Long) entityManager.createQuery("select count(ms.id) from MonitoringStatistics as ms where ms.sessionId=:sessionId")
                .setParameter("sessionId", sessionId)
                .getSingleResult();

        if (monitoringStatisticsCount == 0) {
            log.info("For session {} monitoring statistics were not found in DB for {} ms", sessionId, System.currentTimeMillis() - timestamp);
            return false;
        }

        return true;
    }

    private boolean isWorkloadStatisticsAvailable(long taskId) {
        long timestamp = System.currentTimeMillis();
        long workloadStatisticsCount = (Long) entityManager.createQuery("select count(tis.id) from TimeInvocationStatistics as tis where tis.taskData.id=:taskId")
                .setParameter("taskId", taskId)
                .getSingleResult();

        if (workloadStatisticsCount == 0) {
            log.info("For task ID {} workload statistics were not found in DB for {} ms", taskId, System.currentTimeMillis() - timestamp);
            return false;
        }

        return true;
    }

    private boolean isWorkloadStatisticsAvailable(Set<String> sessionIds, TaskDataDto tests) {
        long timestamp = System.currentTimeMillis();
        long workloadStatisticsCount = (Long) entityManager.createQuery("select count(tis.id) from TimeInvocationStatistics as tis where tis.taskData.sessionId in (:sessionIds) and tis.taskData.id in (:tests)")
                .setParameter("tests", tests.getIds())
                .setParameter("sessionIds", sessionIds)
                .getSingleResult();

        if (workloadStatisticsCount < tests.getIds().size()) {
            log.info("For task ID {} workload statistics were not found in DB for {} ms", tests.getTaskName(), System.currentTimeMillis() - timestamp);
            return false;
        }

        return true;
    }

    private PlotDataProvider findPlotDataProvider(String plotName) {
        PlotDataProvider plotDataProvider = workloadPlotDataProviders.get(plotName);
        if (plotDataProvider == null) {
            plotDataProvider = monitoringPlotDataProviders.get(plotName);
        }
        if (plotDataProvider == null) {
            if (customMetricPlotDataProvider.isAvailable(plotName)){
                plotDataProvider = customMetricPlotDataProvider;
            }
        }
        if (plotDataProvider == null){
            log.warn("getPlotData was invoked with unsupported plotName={}", plotName);
            throw new UnsupportedOperationException("Can not find data for plot \"" + plotName +
                    "\". \nProbably it is link problem");
        }

        return plotDataProvider;
    }
}
