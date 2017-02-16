package com.griddynamics.jagger.engine.e1.collector;

/** Calculates min value on the interval
 * @author Kirill Gribov
 *
 * @ingroup Main_Aggregators_group */
public class MinMetricAggregatorProvider implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new MinMetricAggregator();
    }

    private static class MinMetricAggregator implements MetricAggregator<Number> {

        private Double value = null;

        @Override
        public void append(Number calculated) {
            if (value == null) {
                value = calculated.doubleValue();
            } else {
                value = Math.min(value, calculated.doubleValue());
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
            return "min";
        }
    }
}
