package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.PlotsServingBase;
import com.griddynamics.jagger.webclient.client.components.ControlTree;
import com.griddynamics.jagger.webclient.client.components.SessionComparisonPanel;
import com.griddynamics.jagger.webclient.client.trends.Trends;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public abstract class TreeAwareHandler<T> extends PlotsServingBase implements CheckChangeEvent.CheckChangeHandler<T> {

    protected ControlTree<String> tree;
    protected Trends.MetricFetcher metricFetcher;
    protected SessionComparisonPanel sessionComparisonPanel;
    protected Trends.TestPlotFetcher testPlotFetcher;
    protected Trends.SessionScopePlotFetcher sessionScopePlotFetcher;
    protected Trends.TestInfoFetcher testInfoFetcher;

    public void setTree(ControlTree<String> tree) {
        this.tree = tree;
    }

    public void setMetricFetcher(Trends.MetricFetcher metricFetcher) {
        this.metricFetcher = metricFetcher;
    }

    public void setSessionComparisonPanel(SessionComparisonPanel sessionComparisonPanel) {
        this.sessionComparisonPanel = sessionComparisonPanel;
    }

    public void setTestPlotFetcher(Trends.TestPlotFetcher testPlotFetcher) {
        this.testPlotFetcher = testPlotFetcher;
    }

    public void setSessionScopePlotFetcher(Trends.SessionScopePlotFetcher sessionScopePlotFetcher) {
        this.sessionScopePlotFetcher = sessionScopePlotFetcher;
    }

    public void setTestInfoFetcher(Trends.TestInfoFetcher testInfoFetcher) {
        this.testInfoFetcher = testInfoFetcher;
    }
}
