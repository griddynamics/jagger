#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.provider;

import com.griddynamics.jagger.engine.e1.collector.MetricAggregator;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;

public class ProviderOfFailCountAggregator implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new  MetricAggregator<Number>() {
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
                return "aggFails";
            }
        };
    }
}