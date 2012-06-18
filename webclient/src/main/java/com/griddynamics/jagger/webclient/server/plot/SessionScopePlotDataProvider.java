package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/5/12
 */
public interface SessionScopePlotDataProvider {
    List<PlotSeriesDto> getPlotData(String sessionId, String plotName);
}
