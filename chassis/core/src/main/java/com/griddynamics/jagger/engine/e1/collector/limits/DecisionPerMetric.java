package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;

/** Class is used to describe result of comparison of particular metric to some limit */
public class DecisionPerMetric {
    /** Metric */
    private MetricEntity metricEntity;

    /** Metric summary value */
    private Double metricValue;

    /** Reference value used for comparison */
    private Double metricRefValue;

    /** Result of comparison of this metric */
    private Decision decisionPerMetric;

    public DecisionPerMetric(MetricEntity metricEntity, Double metricValue, Double metricRefValue, Decision decisionPerMetric) {
        this.metricEntity = metricEntity;
        this.metricValue = metricValue;
        this.metricRefValue = metricRefValue;
        this.decisionPerMetric = decisionPerMetric;
    }

    public MetricEntity getMetricEntity() {
        return metricEntity;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public Double getMetricRefValue() {
        return metricRefValue;
    }

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