package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.util.Decision;

import java.util.Set;

/** Class is used to describe result of comparison of metrics to some limit */
public class DecisionPerLimit {

    private Limit limit;
    private Set<DecisionPerMetric> decisionsPerMetric;
    private Decision decisionPerLimit;

    public DecisionPerLimit(Limit limit, Set<DecisionPerMetric> decisionsPerMetric, Decision decisionPerLimit) {
        this.limit = limit;
        this.decisionsPerMetric = decisionsPerMetric;
        this.decisionPerLimit = decisionPerLimit;
    }

    /** Returns information about limit */
    public Limit getLimit() {
        return limit;
    }

    /** Returns detailed information about decision per every metric matching this limit */
    public Set<DecisionPerMetric>  getDecisionsPerMetric() {
        return decisionsPerMetric;
    }

    /** Returns decision for this limit */
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
