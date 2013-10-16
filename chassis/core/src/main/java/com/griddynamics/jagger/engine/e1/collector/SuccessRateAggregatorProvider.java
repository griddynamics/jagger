package com.griddynamics.jagger.engine.e1.collector;

public class SuccessRateAggregatorProvider implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide()
    {
        return new SuccessRateAggregator();
    }

    private static class SuccessRateAggregator  implements MetricAggregator<Number>
    {
        private long passNum = 0;
        private long failNum = 0;

        @Override
        public void append(Number calculated)
        {
            if (calculated.intValue() != 0)
                passNum++;
            else
                failNum++;

        }

        @Override
        public Double getAggregated() {
            if ((failNum + passNum) == 0)
                return new Double(0.0);
            else
                return new Double((double) (passNum) / (double) (failNum + passNum));
        }

        @Override
        public void reset() {
        }

        @Override
        public String getName() {
            return "Success rate";
        }
    }
}