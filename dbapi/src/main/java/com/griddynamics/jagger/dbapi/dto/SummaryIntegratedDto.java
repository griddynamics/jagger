package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Represents rows in summary table and trend plot built with this rows
 * Refers to MetricNode
 */
public class SummaryIntegratedDto implements Serializable {

    // trends plot
    private PlotIntegratedDto plotIntegratedDto;

    // all metrics suit for one metricNode
    private List<SummarySingleDto> summarySingleDtoList;

    public PlotIntegratedDto getPlotIntegratedDto() {
        return plotIntegratedDto;
    }

    public void setPlotIntegratedDto(PlotIntegratedDto plotSeriesDto) {
        this.plotIntegratedDto = plotSeriesDto;
    }

    public List<SummarySingleDto> getSummarySingleDtoList() {
        return summarySingleDtoList;
    }

    public void setSummarySingleDtoList(List<SummarySingleDto> summarySingleDtoList) {
        this.summarySingleDtoList = summarySingleDtoList;
    }
}
