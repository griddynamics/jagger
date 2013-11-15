package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/30/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricDescription implements Serializable{

    protected String id;
    protected String displayName;
    protected boolean showSummary = true;
    protected boolean showPlotData;
    protected List<MetricAggregatorProvider> aggregators;

    public MetricDescription(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAggregators(List<MetricAggregatorProvider> aggregators) {
        this.aggregators = aggregators;
    }

    public List<MetricAggregatorProvider> getAggregators() {
        return aggregators;
    }

    public boolean getShowSummary() {
        return showSummary;
    }

    public void setShowSummary(boolean showSummary) {
        this.showSummary = showSummary;
    }

    public boolean getShowPlotData() {
        return showPlotData;
    }

    public void setShowPlotData(boolean showPlot) {
        this.showPlotData = showPlot;
    }
}
