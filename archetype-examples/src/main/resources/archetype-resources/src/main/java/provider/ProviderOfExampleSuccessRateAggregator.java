#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.provider;

import com.griddynamics.jagger.engine.e1.collector.MetricAggregator;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;

public class ProviderOfExampleSuccessRateAggregator implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new  MetricAggregator<Number>() {
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
                return "aggSR";
            }
        };
    }
}