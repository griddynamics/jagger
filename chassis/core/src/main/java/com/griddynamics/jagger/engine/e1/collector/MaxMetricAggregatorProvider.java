package com.griddynamics.jagger.engine.e1.collector;

// begin: following section is used for docu generation - custom aggregator source
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
            return "max";
        }
    }
}
// end: following section is used for docu generation - custom aggregator source