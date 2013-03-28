package com.griddynamics.jagger.engine.e1.aggregator.workload.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Sergey Pulyaev
 * Date: 24.10.12
 */
@Entity
public class DurationMetricLatencyPercentile extends Percentile {
    @ManyToOne
    @JoinColumn(name="statistics_id")
    private DurationMetricStatistics durationMetricStatistic;

    public DurationMetricLatencyPercentile() {}

    public DurationMetricLatencyPercentile(double percentileKey, double percentileValue) {
        super(percentileKey, percentileValue);
    }

    public DurationMetricStatistics getDurationMetricStatistic() {
        return durationMetricStatistic;
    }

    public void setDurationMetricStatistic(DurationMetricStatistics durationMetricStatistic) {
        this.durationMetricStatistic = durationMetricStatistic;
    }
}
