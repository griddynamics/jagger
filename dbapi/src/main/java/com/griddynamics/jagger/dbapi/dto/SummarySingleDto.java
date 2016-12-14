package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * Represent single row in summary table for one metric.
 * Refers to MetricNameDto
 *
 * @author kirilkadurilka
 * @since 04/04/13
 */
public class SummarySingleDto implements Serializable {

    private MetricNameDto metricName;

    private Set<SummaryMetricValueDto> values;

    public Set<SummaryMetricValueDto> getValues() {
        return values;
    }

    public void setValues(Set<SummaryMetricValueDto> values) {
        this.values = values;
    }

    public MetricNameDto getMetricName() {
        return metricName;
    }

    public void setMetricName(MetricNameDto metricName) {
        this.metricName = metricName;
    }
}
