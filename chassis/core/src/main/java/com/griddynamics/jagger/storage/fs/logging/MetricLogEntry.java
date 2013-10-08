package com.griddynamics.jagger.storage.fs.logging;

/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 18.03.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class MetricLogEntry extends LogEntry<MetricLogEntry> {

    private Double metric;
    private String metricName;

    public MetricLogEntry(long time, String metricName, Double metric) {
        super(time);
        this.metricName=metricName;
        this.metric = metric;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Double getMetric() {
        return this.metric;
    }

    public void setMetric(Double metric) {
        this.metric = metric;
    }

    @Override
    public String toString() {
        return "MetricLogEntry [time=" + this.time + ", metric=" + this.metric + "] metricName=" + this.metricName + "]";
    }
}
