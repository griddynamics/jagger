package com.griddynamics.jagger.test.javabuilders.utils;

import com.griddynamics.jagger.engine.e1.collector.MetricAggregator;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;

/**
 * return difference between first and last logged value
 */
public class FirstLastDifferenceAggregatorProvider implements MetricAggregatorProvider {
    @Override
    public MetricAggregator provide() {
        return new MetricAggregator() {
            private Double first = null;
            private Double last = null;

            @Override
            public void append(Number calculated) {
                Double val = calculated.doubleValue();
                if(first==null){
                    first = val;
                    last =  val;
                }else if (last < val) {
                    last = val;
                }
            }

            @Override
            public Number getAggregated() {
                return last-first;
            }

            @Override
            public void reset() {
                last = null;
                first = null;
            }

            @Override
            public String getName() {
                return "first_last";
            }
        };
    }
}

