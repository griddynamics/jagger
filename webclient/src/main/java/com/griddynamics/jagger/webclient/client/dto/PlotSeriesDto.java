package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class PlotSeriesDto implements Serializable {
    private List<PlotDatasetDto> plotSeries = Collections.emptyList();
    private List<MarkingDto> markingSeries = Collections.emptyList();
    private String xAxisLabel;
    private String yAxisLabel;
    private String plotHeader;
    private double yAxisMin;

    public PlotSeriesDto() {
    }

    public PlotSeriesDto(List<PlotDatasetDto> plotSeries, String xAxisLabel, String yAxisLabel, String plotHeader) {
        this.plotSeries = plotSeries;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.plotHeader = plotHeader;
    }

    public PlotSeriesDto(List<PlotDatasetDto> plotSeries, String xAxisLabel, String yAxisLabel, String plotHeader, List<MarkingDto> markingSeries) {
        this.plotSeries = plotSeries;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.plotHeader = plotHeader;
        this.markingSeries = markingSeries;
    }

    public List<PlotDatasetDto> getPlotSeries() {
        return plotSeries;
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public String getPlotHeader() {
        return plotHeader;
    }

    public List<MarkingDto> getMarkingSeries() {
        return markingSeries;
    }

    public double getYAxisMin() {
        return yAxisMin;
    }

    public void setYAxisMin(double yAxisMin) {
        this.yAxisMin = yAxisMin;
    }

    @Override
    public String toString() {
        return "PlotSeriesDto{" +
                "plotSeries=" + plotSeries +
                ", markingSeries=" + markingSeries +
                ", xAxisLabel='" + xAxisLabel + '\'' +
                ", yAxisLabel='" + yAxisLabel + '\'' +
                ", plotHeader='" + plotHeader + '\'' +
                '}';
    }
}
