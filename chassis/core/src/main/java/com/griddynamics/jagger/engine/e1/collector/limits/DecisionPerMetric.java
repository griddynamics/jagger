package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.util.Decision;

/** Class is used to describe result of comparison of particular metric to some limit */
public class DecisionPerMetric {

    private MetricEntity metricEntity;
    private Double metricValue;
    private Double metricRefValue;

    /** Result of comparison of this metric */
    private Decision decisionPerMetric;

    public DecisionPerMetric(MetricEntity metricEntity, Double metricValue, Double metricRefValue, Decision decisionPerMetric) {
        this.metricEntity = metricEntity;
        this.metricValue = metricValue;
        this.metricRefValue = metricRefValue;
        this.decisionPerMetric = decisionPerMetric;
    }

    /** Returns information about metric */
    public MetricEntity getMetricEntity() {
        return metricEntity;
    }

    /** Returns metric summary value */
    public Double getMetricValue() {
        return metricValue;
    }

    /** Returns reference value used for comparison */
    public Double getMetricRefValue() {
        return metricRefValue;
    }

    /** Returns decision for this metric */
    public Decision getDecisionPerMetric() {
        return decisionPerMetric;
    }

    @Override
    public String toString() {
        return "DecisionPerMetric{" +
                "metricId=" + metricEntity.getMetricId() +
                ", metricValue=" + metricValue +
                ", metricRefValue=" + metricRefValue +
                ", decisionPerMetric=" + decisionPerMetric +
                '}';
    }
}