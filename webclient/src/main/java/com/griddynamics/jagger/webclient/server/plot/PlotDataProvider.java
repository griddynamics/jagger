package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameSeriesDto;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public interface PlotDataProvider {

    PlotNameSeriesDto getPlotData(PlotNameDto plotNameDto);
}
