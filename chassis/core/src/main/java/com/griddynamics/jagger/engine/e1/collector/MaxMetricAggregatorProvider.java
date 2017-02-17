package com.griddynamics.jagger.engine.e1.collector;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.MAX_AGGREGATOR_ID;

/** Calculates max value on the interval
 * @author Kirill Gribov
 *
 * @ingroup Main_Aggregators_group */
public class MaxMetricAggregatorProvider implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new MaxMetricAggregator();
    }

    private static class MaxMetricAggregator implements MetricAggregator<Number> {

        private Double value = null;

        @Override
        public void append(Number calculated) {
            if (value == null) {
                value = calculated.doubleValue();
            } else {
                value = Math.max(value, calculated.doubleValue());
            }
        }

        @Override
        public Double getAggregated() {
            return value;
        }

        @Override
        public void reset() {
            value = null;
        }

        @Override
        public String getName() {
            return MAX_AGGREGATOR_ID;
        }
    }
}