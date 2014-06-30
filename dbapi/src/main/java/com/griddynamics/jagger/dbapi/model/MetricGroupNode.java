package com.griddynamics.jagger.dbapi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MetricGroupNode<M extends MetricNode> extends AbstractIdentifyNode {

    private List<M> metrics = null;
    private List<MetricGroupNode<M>> metricGroupNodeList = null;

    public MetricGroupNode(MetricGroupNode<M> that) {
        super(that);
        this.metrics = that.getMetricsWithoutChildren();
        this.metricGroupNodeList = that.getMetricGroupNodeList();
    }
    public MetricGroupNode(String displayName) {
        this.displayName = displayName;
    }
    public MetricGroupNode() {}

    public void setMetricGroupNodeList(List<MetricGroupNode<M>> metricGroupNodeList) {
        this.metricGroupNodeList = metricGroupNodeList;
    }

    public void setMetrics(List<M> metrics) {
        this.metrics = sortByDisplayName(metrics);
    }

    public List<M> getMetricsWithoutChildren() {
        return metrics;
    }
    public List<MetricGroupNode<M>> getMetricGroupNodeList() {
        return metricGroupNodeList;
    }

    // all metrics, including children groups
    public List<M> getMetrics() {
        List<M> allMetrics = new ArrayList<M>();

        if (metrics != null) {
            allMetrics.addAll(metrics);
        }

        if (metricGroupNodeList != null) {
            for (MetricGroupNode<M> metricGroupNode : metricGroupNodeList) {
                List<M> metricNodeList = metricGroupNode.getMetrics();
                if (metricNodeList != null) {
                    allMetrics.addAll(metricNodeList);
                }
            }
        }

        return allMetrics;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        ArrayList<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        if (metricGroupNodeList != null) {
            result.addAll(metricGroupNodeList);
        }
        if (metrics != null) {
            result.addAll(metrics);
        }
        return result;
    }

    private List<M> sortByDisplayName(List<M> metricNodeList) {
        Collections.sort(metricNodeList, new Comparator<M>() {
            @Override
            public int compare(M o1, M o2) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getDisplayName(), o2.getDisplayName());
                return (res != 0) ? res : o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        return metricNodeList;
    }
}
