package com.griddynamics.jagger.invoker.scenario;

import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.CumulativeMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MaxMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MinMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.PercentileAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.StdDevMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateFailsAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.SumMetricAggregatorProvider;

/**
 * Default metric aggregators provider
 */
public class DefaultAggregatorsProvider {
    public static final AvgMetricAggregatorProvider AVG_AGGREGATOR = new AvgMetricAggregatorProvider();
    public static final MinMetricAggregatorProvider MIN_AGGREGATOR = new MinMetricAggregatorProvider();
    public static final MaxMetricAggregatorProvider MAX_AGGREGATOR = new MaxMetricAggregatorProvider();
    public static final CumulativeMetricAggregatorProvider CUMULATIVE_AGGREGATOR = new CumulativeMetricAggregatorProvider();
    public static final StdDevMetricAggregatorProvider STD_DEV_AGGREGATOR = new StdDevMetricAggregatorProvider();
    public static final SumMetricAggregatorProvider SUM_AGGREGATOR = new SumMetricAggregatorProvider();
    public static final SuccessRateAggregatorProvider SUCCESS_AGGREGATOR = new SuccessRateAggregatorProvider();
    public static final SuccessRateFailsAggregatorProvider FAILS_AGGREGATOR = new SuccessRateFailsAggregatorProvider();

    public static PercentileAggregatorProvider PERCENTILE_AGGREGATOR(Double precentile) {
        return new PercentileAggregatorProvider(precentile);
    }

}
