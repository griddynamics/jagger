package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 04.04.13
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
public class MetricDto implements Serializable {

    private MetricNameDto metricName;

    private Set<MetricValueDto> values;

    public Set<MetricValueDto> getValues() {
        return values;
    }

    public void setValues(Set<MetricValueDto> values) {
        this.values = values;
    }

    public MetricNameDto getMetricName() {
        return metricName;
    }

    public void setMetricName(MetricNameDto metricName) {
        this.metricName = metricName;
    }
}
