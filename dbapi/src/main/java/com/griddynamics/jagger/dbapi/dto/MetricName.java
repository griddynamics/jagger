package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;
import java.util.List;

/**
 * User: amikryukov
 * Date: 12/17/13
 */
public class MetricName implements Serializable {

    protected String metricName;
    protected String metricDisplayName = null;
    // some of metrics (f.e. Latency, Throughput, Monitoring metrics) had names hardcoded in several places
    // and for sure they were different =>
    // it was possible when same metric had different names (ids) in DB, plots, report, tables, tree view, hyperlinks
    // now we are trying to define metric Id and display name in single place and will use synonyms to be compatible with old versions
    // DON'T use this field for new created metrics
    protected List<String> metricNameSynonyms = null;

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

    public List<String> getMetricNameSynonyms() {
        return metricNameSynonyms;
    }

    public void setMetricNameSynonyms(List<String> metricNameSynonyms) {
        this.metricNameSynonyms = metricNameSynonyms;
    }
}
