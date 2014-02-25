package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * User: amikryukov
 * Date: 12/17/13
 */
public class MetricName implements Serializable {

    protected String metricName;
    protected String metricDisplayName = null;

    public MetricName() {}
    public MetricName(String metricName) {
        this.metricName = metricName;
    }
    public MetricName(String metricName, String metricDisplayName) {
        this.metricName = metricName;
        this.metricDisplayName = metricDisplayName;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricDisplayName() {
        return metricDisplayName == null ? metricName : metricDisplayName;
    }

    public void setMetricDisplayName(String metricDisplayName) {
        this.metricDisplayName = metricDisplayName;
    }
}
