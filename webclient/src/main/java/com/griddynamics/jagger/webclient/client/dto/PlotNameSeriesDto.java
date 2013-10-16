package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.List;

/**
 * User: amikryukov
 * Date: 10/16/13
 */
public class PlotNameSeriesDto implements Serializable {

    PlotNameDto plotNameDto;
    List<PlotSeriesDto> plotSeriesDto;

    public PlotNameSeriesDto () {}

    public PlotNameSeriesDto (PlotNameDto plotNameDto, List<PlotSeriesDto> plotSeriesDto) {
        this.plotNameDto = plotNameDto;
        this.plotSeriesDto = plotSeriesDto;
    }

    public PlotNameDto getPlotNameDto() {
        return plotNameDto;
    }

    public void setPlotNameDto(PlotNameDto plotNameDto) {
        this.plotNameDto = plotNameDto;
    }

    public List<PlotSeriesDto> getPlotSeriesDto() {
        return plotSeriesDto;
    }

    public void setPlotSeriesDto(List<PlotSeriesDto> plotSeriesDto) {
        this.plotSeriesDto = plotSeriesDto;
    }
}
