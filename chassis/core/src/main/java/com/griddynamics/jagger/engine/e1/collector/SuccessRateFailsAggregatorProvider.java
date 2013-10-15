package com.griddynamics.jagger.engine.e1.collector;

public class SuccessRateFailsAggregatorProvider implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new SuccessRateFailsAggregator();
    }

    private static class SuccessRateFailsAggregator  implements MetricAggregator<Number>
    {
        long failNum = 0;

        @Override
        public void append(Number calculated)
        {
            if (calculated.intValue() == 0)
                failNum++;
        }

        @Override
        public Double getAggregated() {
            return new Double(failNum);
        }

        @Override
        public void reset() {
        }

        @Override
        public String getName() {
            return "Number of fails";
        }
    }
}