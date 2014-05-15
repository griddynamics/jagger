package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;

import java.util.Set;

/** Class is used to describe result of comparison of metrics to some limit */
public class DecisionPerLimit {
    /** Limit we are comparing to */
    private Limit limit;

    /** Results of comparison for metrics. Several metrics can match to metricName in limit (f.e. cpu utilization from different agents) */
    private Set<DecisionPerMetric> decisionsPerMetric;

    /** Results of comparison for this limit */
    private Decision decisionPerLimit;

    public DecisionPerLimit(Limit limit, Set<DecisionPerMetric> decisionsPerMetric, Decision decisionPerLimit) {
        this.limit = limit;
        this.decisionsPerMetric = decisionsPerMetric;
        this.decisionPerLimit = decisionPerLimit;
    }

    public Limit getLimit() {
        return limit;
    }

    public Set<DecisionPerMetric>  getDecisionsPerMetric() {
        return decisionsPerMetric;
    }

    public Decision getDecisionPerLimit() {
        return decisionPerLimit;
    }

    @Override
    public String toString() {
        return "DecisionPerLimit{" +
                "\n    limit=" + limit +
                "\n    decisionsPerMetric=" + decisionsPerMetric +
                "\n    decisionPerLimit=" + decisionPerLimit +
                '}';
    }

}
