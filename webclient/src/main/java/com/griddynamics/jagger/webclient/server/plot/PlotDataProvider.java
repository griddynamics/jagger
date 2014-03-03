package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;

import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public interface PlotDataProvider {
    List<PlotSeriesDto> getPlotData(long taskId, MetricNameDto plotName);

    List<PlotSeriesDto> getPlotData(Set<Long> taskId, MetricNameDto plotName);
}
