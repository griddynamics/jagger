package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.util.statistics.StatisticsCalculator;

/** Calculates percentile value on the interval. Percentile must be passed in constructor
 * @author Anton Antonenko
 *
 * @ingroup Main_Aggregators_group */
public class PercentileAggregatorProvider implements MetricAggregatorProvider {
    private Double percentile = 95D;

    public PercentileAggregatorProvider(Double percentile) {
        this.percentile = percentile;
    }

    public PercentileAggregatorProvider() {}

    @Override
    public MetricAggregator provide() {
        return new PercentileAggregator();
    }

    private class PercentileAggregator implements MetricAggregator<Number> {
        private StatisticsCalculator statisticsCalculator = new StatisticsCalculator();

        @Override
        public void append(Number calculated) {
            statisticsCalculator.addValue(calculated.doubleValue());
        }

        @Override
        public Number getAggregated() {
            Double result = statisticsCalculator.getPercentile(percentile);
            return result.isNaN() ? null : result;
        }

        @Override
        public void reset() {
            statisticsCalculator.reset();
        }

        @Override
        public String getName() {
            return percentile + "%";
        }
    }
}
