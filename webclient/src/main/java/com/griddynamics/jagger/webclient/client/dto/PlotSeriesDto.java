package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class PlotSeriesDto implements Serializable {
    private Collection<PlotDatasetDto> plotSeries = Collections.emptyList();
    private String xAxisLabel;
    private String yAxisLabel;
    private String plotHeader;

    public PlotSeriesDto() {
    }

    public PlotSeriesDto(Collection<PlotDatasetDto> plotSeries, String xAxisLabel, String yAxisLabel, String plotHeader) {
        this.plotSeries = plotSeries;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.plotHeader = plotHeader;
    }

    public Collection<PlotDatasetDto> getPlotSeries() {
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

    @Override
    public String toString() {
        return "PlotSeriesDto{" +
                "plotSeries=" + plotSeries +
                ", xAxisLabel='" + xAxisLabel + '\'' +
                ", yAxisLabel='" + yAxisLabel + '\'' +
                ", plotHeader='" + plotHeader + '\'' +
                '}';
    }
}
