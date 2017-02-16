package com.griddynamics.jagger.engine.e1.collector;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** Class to describe metric
 * @author Gribov Kirill
 * @details
 * Example of metric setup and creation with use of @ref com.griddynamics.jagger.engine.e1.services.DefaultMetricService "metric service"
 * @include  ExampleInvocationListener.java
 * @n
 */
public class MetricDescription implements Serializable{

    protected String id;
    protected String displayName;
    protected boolean showSummary = true;
    protected boolean plotData;
    protected Map<MetricAggregatorProvider, MetricAggregatorSettings> aggregatorsWithSettings = Maps.newHashMap();

    /** Constructor
     * @param metricId - main ID of the metric. Metric will be stored under this ID in DB */
    public MetricDescription(String metricId) {
        this.id = metricId;
    }

    /** Getter for metric ID
     * @return Metric ID*/
    public String getMetricId() {
        return this.id;
    }
    /** Setter for metric ID
     * @param metricId - main ID of the metric. Metric will be stored under this ID in DB */
    public void setMetricId(String metricId){
        this.id = metricId;
    }

    /** Getter for metric display name
     * @return display name */
    public String getDisplayName() {
        return displayName;
    }
    /** Setter for metric display name
     * @param displayName - display name of the metric. This name will be displayed in WebUI and PDF report */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /** Getter for metric aggregators
     * @return list of aggregators assigned to this metric */
    public List<MetricAggregatorProvider> getAggregators() {
        return Lists.newArrayList(aggregatorsWithSettings.keySet());
    }
    /** Setter for metric aggregators
     * @param aggregators - list of aggregators that will be applied to this metric during result processing. @n
     *                      If list will be empty Jagger will use default aggregator (summary).@n
     *                      You can use Jagger built in aggregators @ref Main_Aggregators_group or custom aggregators */
    public void setAggregators(List<MetricAggregatorProvider> aggregators) {
        aggregatorsWithSettings.clear();
        for (MetricAggregatorProvider aggregator : aggregators) {
            aggregatorsWithSettings.put(aggregator, MetricAggregatorSettings.EMPTY_SETTINGS);
        }
    }

    /** Getter for metric aggregators with settings
     * @return Aggregators settings map */
    public Map<MetricAggregatorProvider, MetricAggregatorSettings> getAggregatorsWithSettings() {
        return aggregatorsWithSettings;
    }
    
    /** Setter for metric aggregators with settings
     * @param aggregatorsWithSettings - map of aggregators with settings that will be applied to this metric during result processing. @n
     *                      What additional settings can be applied you can find here @ref MetricAggregatorSettings @n
     *                      If map will be empty Jagger will use default aggregator (summary) without additional settings.@n
     *                      You can use Jagger built in aggregators @ref Main_Aggregators_group or custom aggregators */
    public void setAggregatorsWithSettings(Map<MetricAggregatorProvider, MetricAggregatorSettings> aggregatorsWithSettings) {
        this.aggregatorsWithSettings = aggregatorsWithSettings;
    }

    /** Getter for metric "show summary" boolean parameter
     * @return true if necessary to save summary value to DB and show it in report and WebUI */
    public boolean getShowSummary() {
        return showSummary;
    }
    /** Setter for metric "show summary" boolean parameter
     * @param showSummary - set true if you want to save summary value to DB and show it in report and WebUI */
    public void setShowSummary(boolean showSummary) {
        this.showSummary = showSummary;
    }

    /** Getter for metric "plot data" boolean parameter
     * @return true if necessary to save detailed results (metric vs time) to DB and show it in report and WebUI */
    public boolean getPlotData() {
        return plotData;
    }
    /** Setter for metric "plot data" boolean parameter
     * @param plotData - set true if you want to save detailed results (metric vs time) to DB and show it in report and WebUI */
    public void setPlotData(boolean plotData) {
        this.plotData = plotData;
    }

    /** Setter for metric "show summary" boolean parameter
     * @param showSummary - set true if you want to save summary value to DB and show it in report and WebUI
     * @return this MetricDescription */
    public MetricDescription showSummary(boolean showSummary){
        this.showSummary = showSummary;
        return this;
    }
    /** Setter for metric "plot data" boolean parameter
     * @param plotData - set true if you want to save detailed results (metric vs time) to DB and show it in report and WebUI
     * @return this MetricDescription */
    public MetricDescription plotData(boolean plotData){
        this.plotData = plotData;
        return this;
    }
    /** Setter for metric display name
     * @param displayName - display name of the metric. This name will be displayed in WebUI and PDF report
     * @return this MetricDescription */
    public MetricDescription displayName(String displayName){
        this.displayName = displayName;
        return this;
    }

    /** Append new aggregator to list of metric aggregator
     * @param aggregator - aggregators that will be applied to this metric during result processing. @n
     *                     You can use Jagger built in aggregators @ref Main_Aggregators_group or custom aggregator
     * @return this MetricDescription */
    public MetricDescription addAggregator(MetricAggregatorProvider aggregator){
        this.aggregatorsWithSettings.put(aggregator, MetricAggregatorSettings.EMPTY_SETTINGS);
        return this;
    }

    /** Append new aggregator to list of metric aggregator with settings.
     * @param aggregator - aggregators that will be applied to this metric during result processing. @n
     *                     You can use Jagger built in aggregators @ref Main_Aggregators_group or custom aggregator
     * @param settings - settings of aggregator.
     * @return this MetricDescription */
    public MetricDescription addAggregator(MetricAggregatorProvider aggregator, MetricAggregatorSettings settings){
        this.aggregatorsWithSettings.put(aggregator, settings);
        return this;
    }
}
