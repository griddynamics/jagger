package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;

import java.util.List;

public class PlotWithContext {

    private HTMLPanel panel;
    private List<PlotSeriesDto> plotSeriesDtoList;
    private double uMinimum;
    private boolean isMetric;

    public PlotWithContext(HTMLPanel panel, List<PlotSeriesDto> plotSeriesDtoList, double uMinimum, boolean metric) {
        this.panel = panel;
        this.plotSeriesDtoList = plotSeriesDtoList;
        this.uMinimum = uMinimum;
        isMetric = metric;
    }

    public HTMLPanel getPanel() {
        return panel;
    }

    public List<PlotSeriesDto> getPlotSeriesDtoList() {
        return plotSeriesDtoList;
    }

    public double getuMinimum() {
        return uMinimum;
    }

    public boolean isMetric() {
        return isMetric;
    }
}
