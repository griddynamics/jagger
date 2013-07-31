package com.griddynamics.jagger.webclient.client;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/19/12
 */
public abstract class PlotsServingBase {

    protected String generateTaskScopePlotId(PlotNameDto plotNameDto) {
        return "" + plotNameDto.getTaskId() + "#task-scope-plot-" + plotNameDto.getPlotName().toLowerCase().replaceAll("\\s+", "-");
    }

    protected String generateCrossSessionsTaskScopePlotId(PlotNameDto plotNameDto) {
        String ids = plotNameDto.getTaskIds().toString().replaceAll("\\s+", "");
        return "" + ids + "#cross-sessions-task-scope-plot-" + plotNameDto.getPlotName().toLowerCase().replaceAll("\\s+", "-");
    }

    protected boolean isTaskScopePlotId(String domId) {
        return domId.contains("#task-scope-plot-");
    }

    protected boolean isCrossSessionsTaskScopePlotId(String domId) {
        return domId.contains("#cross-sessions-task-scope-plot-");
    }

    protected boolean isMetricPlotId(String domId) {
        return domId.contains("#metric-scope-plot-");
    }

    protected String generateMetricPlotId(MetricNameDto plotNameDto) {
        return "" + plotNameDto.getTests().getTaskName() + "#metric-scope-plot-" + plotNameDto.getName().toLowerCase().replaceAll("\\s+", "-");
    }

    protected boolean isSessionScopePlotId(String domId) {
        return domId.contains("#session-scope-plot-");
    }

    protected String generateSessionScopePlotId(String sessionId, String plotName) {
        return sessionId + "#session-scope-plot-" + plotName.toLowerCase().replaceAll("\\s+", "-");
    }

    protected String extractEntityIdFromDomId(String plotId) {
        return plotId.substring(0, plotId.indexOf("#"));
    }

}
