package com.griddynamics.jagger.dbapi.util;

import com.griddynamics.jagger.dbapi.model.NameTokens;
import org.springframework.stereotype.Component;

/**
 * Class contains methods to create legends for metric`s curves and plot headers for plots.
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
@Component
public class LegendProvider {

    private static final String SESSION_PREFIX = "#";
    private static final String LEGEND_DESCRIPTION_SEPARATOR = ": ";

    public LegendProvider() {
    }

    /**
     * Creates legend for curve(single metric).
     *
     * @param sessionId session id
     * @param description description of metric (displayName)
     * @param addSessionPrefix defines whether adds session prefix or not.@n
     *                         For example trends do not need session prefix
     * @return legend for single metric
     */
    public String generatePlotLegend(String sessionId, String description, boolean addSessionPrefix) {
        if (!addSessionPrefix) {
            return description;
        }

        return SESSION_PREFIX + sessionId + LEGEND_DESCRIPTION_SEPARATOR + description;
    }

    /**
     * Creates plot header for the plot
     *
     * @param taskName name of task
     * @param plotName displayName of PlotNode for witch plot plot-header should be created
     * @return plot header of the plot
     */
    public String generatePlotHeader(String taskName, String plotName) {

        return taskName + ", " + plotName;
    }


    /**
     * Creates plot header for the session scope plot
     *
     * @param plotName displayName of PlotNode for witch plot plot-header should be created
     * @return plot header of the session scope plot
     */
    public String generateSessionScopePlotHeader(String plotName) {

        return NameTokens.SESSION_SCOPE_PLOTS + ", " + plotName;
    }
    

    /**
     * Get metric description(displayName) from legend
     *
     * @param legend legend string
     * @return metric description(displayName)
     */
    public static String parseMetricName(String legend) {
        if (legend.startsWith(SESSION_PREFIX)) {
            return legend.substring(legend.indexOf(LEGEND_DESCRIPTION_SEPARATOR) + LEGEND_DESCRIPTION_SEPARATOR.length());
        }
        return legend;
    }

    /**
     * Get session id from legend
     *
     * @param legend legend string
     * @return session id
     */
    public static String parseSessionId(String legend) {
        if (legend.startsWith(SESSION_PREFIX)) {
            return legend.substring(legend.indexOf(SESSION_PREFIX), legend.indexOf(LEGEND_DESCRIPTION_SEPARATOR));
        }
        return null;
    }
}
