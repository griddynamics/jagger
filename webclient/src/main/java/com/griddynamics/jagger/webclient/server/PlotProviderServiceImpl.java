package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.plot.CustomMetricPlotDataProvider;
import com.griddynamics.jagger.webclient.server.plot.DataPointCompressingProcessor;
import com.griddynamics.jagger.webclient.server.plot.PlotDataProvider;
import com.griddynamics.jagger.webclient.server.plot.SessionScopePlotDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

import static com.griddynamics.jagger.webclient.client.mvp.NameTokens.AGENT_NAME_SEPARATOR;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl implements PlotProviderService {
    private static final Logger log = LoggerFactory.getLogger(PlotProviderServiceImpl.class);

    private DataPointCompressingProcessor compressingProcessor;
    private Map<String, PlotDataProvider> workloadPlotDataProviders;
    private Map<String, PlotDataProvider> monitoringPlotDataProviders;
    private CustomMetricPlotDataProvider customMetricPlotDataProvider;

    //==========Setters

    @Required
    public void setCompressingProcessor(DataPointCompressingProcessor compressingProcessor) {
        this.compressingProcessor = compressingProcessor;
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

    //===========================
    //===========Contract Methods
    //===========================

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, MetricNameDto plotName) {
        long timestamp = System.currentTimeMillis();
        log.debug("getPlotData was invoked with taskId={} and metricName={}", taskId, plotName);

        PlotDataProvider plotDataProvider = findPlotDataProvider(plotName);

        List<PlotSeriesDto> plotSeriesDto;
        try {
            plotSeriesDto = plotDataProvider.getPlotData(taskId, plotName);
            log.info("getPlotData(): {}", getFormattedLogMessage(plotSeriesDto, "" + taskId, plotName.getMetricName(), System.currentTimeMillis() - timestamp));
        } catch (Exception e) {
            log.error("Error is occurred during plot data loading for taskId=" + taskId + ", metricName=" + plotName, e);
            throw new RuntimeException(e);
        }

        return plotSeriesDto;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(Set<Long> taskIds, MetricNameDto plotName) {
        long timestamp = System.currentTimeMillis();
        log.debug("getPlotData was invoked with taskIds={} and metricName={}", taskIds, plotName);

        PlotDataProvider plotDataProvider = findPlotDataProvider(plotName);

        List<PlotSeriesDto> plotSeriesDtoList;
        try {
            plotSeriesDtoList = plotDataProvider.getPlotData(taskIds, plotName);
            log.info("getPlotData(): {}", getFormattedLogMessage(plotSeriesDtoList, "" + taskIds, plotName.getMetricName(), System.currentTimeMillis() - timestamp));
        } catch (Exception e) {
            log.error("Error is occurred during plot data loading for taskIds=" + taskIds + ", metricName=" + plotName, e);
            throw new RuntimeException(e);
        }

        return plotSeriesDtoList;
    }

    @Override
    public Map<MetricNameDto, List<PlotSeriesDto>> getPlotDatas(Set<MetricNameDto> plots) throws IllegalArgumentException{
        Map<MetricNameDto,List<PlotSeriesDto>> result = new LinkedHashMap<MetricNameDto, List<PlotSeriesDto>>(plots.size());
        // todo : fetch metrics  plots in one query
        for (MetricNameDto plot : plots){
            result.put(plot, getPlotData(plot.getTaskIds(), plot));
        }
        return result;
    }


    @Override
    public Map<SessionPlotNameDto, List<PlotSeriesDto>> getSessionScopePlotData(String sessionId, Collection<SessionPlotNameDto> plotNames) {
        long timestamp = System.currentTimeMillis();
        Map<SessionPlotNameDto, List<PlotSeriesDto>> resultMap = new HashMap<SessionPlotNameDto, List<PlotSeriesDto>>();

        for(SessionPlotNameDto plotName : plotNames) {
            log.debug("getPlotData was invoked with sessionId={} and metricName={}", sessionId, plotName);
            List<PlotSeriesDto> plotSeriesDtoList;

            SessionScopePlotDataProvider plotDataProvider = (SessionScopePlotDataProvider) findPlotDataProvider(plotName);
            if (plotDataProvider == null) {
                log.warn("getPlotData was invoked with unsupported metricName={}", plotName);
                throw new UnsupportedOperationException("Plot type " + plotName + " doesn't supported");
            }


            try {
                plotSeriesDtoList = plotDataProvider.getPlotData(sessionId, plotName.getMetricName());
                log.info("getSessionScopePlotData(): {}", getFormattedLogMessage(plotSeriesDtoList, sessionId, plotName.getMetricName(), System.currentTimeMillis() - timestamp));
                for (PlotSeriesDto plotSeriesDto : plotSeriesDtoList) {
                    for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                        List<PointDto> pointDtoList = compressingProcessor.process(plotDatasetDto.getPlotData());
                        plotDatasetDto.getPlotData().clear();
                        plotDatasetDto.getPlotData().addAll(pointDtoList);
                    }
                }
                log.info("getSessionScopePlotData() after compressing: {}", getFormattedLogMessage(plotSeriesDtoList, sessionId, plotName.getMetricName(), System.currentTimeMillis() - timestamp));
            } catch (Exception e) {
                System.err.println(e);
                log.error("Error is occurred during plot data loading for sessionId=" + sessionId + ", metricName=" + plotName, e);
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

    private PlotDataProvider findPlotDataProvider(MetricName plotName) {
        PlotDataProvider plotDataProvider = workloadPlotDataProviders.get(plotName.getMetricName());
        if (plotDataProvider == null) {
            // any ideas ?
            if (plotName.getMetricName().contains(AGENT_NAME_SEPARATOR)) {
                String temp = plotName.getMetricName().substring(0, plotName.getMetricName().indexOf(AGENT_NAME_SEPARATOR));
                plotDataProvider = monitoringPlotDataProviders.get(temp);
            }
        }
        if (plotDataProvider == null) {
            // we already checked if plot is available on tree creating step
            plotDataProvider = customMetricPlotDataProvider;
        }

        return plotDataProvider;
    }
}
