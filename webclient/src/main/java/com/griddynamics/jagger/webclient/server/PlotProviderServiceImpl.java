package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.plot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

import static com.griddynamics.jagger.util.AgentUtils.AGENT_NAME_SEPARATOR;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl implements PlotProviderService {
    private static final Logger log = LoggerFactory.getLogger(PlotProviderServiceImpl.class);

    private DataPointCompressingProcessor compressingProcessor;
    private ThroughputPlotDataProvider throughputPlotDataProvider;
    private LatencyPlotDataProvider latencyPlotDataProvider;
    private TimeLatencyPercentilePlotDataProvider timeLatencyPercentilePlotDataProvider;
    private MonitoringPlotDataProvider monitoringPlotDataProvider;
    private CustomMetricPlotDataProvider customMetricPlotDataProvider;

    private LegendProvider legendProvider;

    //==========Setters

    @Required
    public void setCompressingProcessor(DataPointCompressingProcessor compressingProcessor) {
        this.compressingProcessor = compressingProcessor;
    }

    @Required
    public void setCustomMetricPlotDataProvider(CustomMetricPlotDataProvider customMetricPlotDataProvider) {
        this.customMetricPlotDataProvider = customMetricPlotDataProvider;
    }

    @Required
    public void setThroughputPlotDataProvider(ThroughputPlotDataProvider throughputPlotDataProvider) {
        this.throughputPlotDataProvider = throughputPlotDataProvider;
    }

    @Required
    public void setLatencyPlotDataProvider(LatencyPlotDataProvider latencyPlotDataProvider) {
        this.latencyPlotDataProvider = latencyPlotDataProvider;
    }

    @Required
    public void setTimeLatencyPercentilePlotDataProvider(TimeLatencyPercentilePlotDataProvider timeLatencyPercentilePlotDataProvider) {
        this.timeLatencyPercentilePlotDataProvider = timeLatencyPercentilePlotDataProvider;
    }

    @Required
    public void setMonitoringPlotDataProvider(MonitoringPlotDataProvider monitoringPlotDataProvider) {
        this.monitoringPlotDataProvider = monitoringPlotDataProvider;
    }

    @Required
    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    //===========================
    //===========Contract Methods
    //===========================

    @Override
    public Map<MetricNode, PlotSeriesDto> getPlotData(Set<MetricNode> plots) throws IllegalArgumentException{
        PlotDataProvider plotDataProvider;
        Map<MetricNode, PlotSeriesDto> result = new HashMap<MetricNode, PlotSeriesDto>();
        List<PlotSeriesDto> plotSeriesDtoList;

        //todo currently - slow approach. best way to group metrics by origin, get lines and reorder lines to plots

        for (MetricNode metricNode : plots) {

            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();

            for (MetricNameDto metricNameDto : metricNode.getMetricNameDtoList()) {
                plotDataProvider = findPlotDataProvider(metricNameDto);
                if (plotDataProvider != null) {
                    // returns plot
                    plotSeriesDtoList = plotDataProvider.getPlotData(metricNameDto);

                    // we don't need plot, we need lines
                    for (PlotSeriesDto plotSeriesDto : plotSeriesDtoList) {
                        plotDatasetDtoList.addAll(plotSeriesDto.getPlotSeries());
                    }
                }
            }

            // at the moment all MetricNameDtos in MetricNode have same taskIds => it is valid to use first one
            result.put(metricNode, new PlotSeriesDto(plotDatasetDtoList,"Time, sec", "",legendProvider.getPlotHeader(metricNode.getMetricNameDtoList().get(0).getTaskIds(), metricNode.getDisplayName())));

        }

        return result;
    }


    @Override
    public Map<SessionPlotNameDto, List<PlotSeriesDto>> getSessionScopePlotData(String sessionId, Collection<SessionPlotNameDto> plotNames) {
        long timestamp = System.currentTimeMillis();
        Map<SessionPlotNameDto, List<PlotSeriesDto>> resultMap = new HashMap<SessionPlotNameDto, List<PlotSeriesDto>>();

        SessionScopePlotDataProvider plotDataProvider = monitoringPlotDataProvider;

        for(SessionPlotNameDto plotName : plotNames) {
            log.debug("getPlotData was invoked with sessionId={} and metricName={}", sessionId, plotName);
            List<PlotSeriesDto> plotSeriesDtoList;

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

    private PlotDataProvider findPlotDataProvider(MetricNameDto metricNameDto) {
        PlotDataProvider plotDataProvider = null;
        switch (metricNameDto.getOrigin()) {
            case UNKNOWN:
            case STANDARD_METRICS:
            case VALIDATOR:
            case DURATION:

                throw new RuntimeException("Unable to get plot data for metric " + metricNameDto.getMetricName() +
                        " with origin: " + metricNameDto.getOrigin());

            case METRIC:
                plotDataProvider = customMetricPlotDataProvider;
                break;
            case LATENCY:
                plotDataProvider = latencyPlotDataProvider;
                break;
            case THROUGHPUT:
                plotDataProvider = throughputPlotDataProvider;
                break;
            case LATENCY_PERCENTILE:
                plotDataProvider = timeLatencyPercentilePlotDataProvider;
                break;
            case MONITORING:
                plotDataProvider = monitoringPlotDataProvider;
                break;
        }

        return plotDataProvider;
    }
}
