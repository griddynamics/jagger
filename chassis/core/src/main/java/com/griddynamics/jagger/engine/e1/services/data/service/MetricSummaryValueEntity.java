package com.griddynamics.jagger.engine.e1.services.data.service;

import com.griddynamics.jagger.util.Decision;

/** Class is a model of summary for some metric
 *
 * @details
 * MetricSummaryValueEntity is used to describe metric summary value
 *
 * @author
 * Latnikov Dmitry
 */
public class MetricSummaryValueEntity {
    /** Summary value for this metric */
    private Double value;
    /** DecisionPerMetric if this metric was compared to some limits to take decision. If not - equal to null */
    private Decision decision = null;

    /** Get decision per metric */
    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    /** Get summary value */
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MetricSummaryValueEntity{" +
                "value=" + value +
                ", decision=" + decision +
                '}';
    }
}
