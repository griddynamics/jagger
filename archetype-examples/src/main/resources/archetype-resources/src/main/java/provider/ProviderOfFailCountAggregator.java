#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.provider;

import com.griddynamics.jagger.engine.e1.collector.MetricAggregator;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;

public class ProviderOfFailCountAggregator implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new MetricAggregator() {

            int failNum = 0;

            @Override
            public void append(Integer calculated)
            {
                if (calculated!=0)
                    failNum++;
            }

            @Override
            public Integer getAggregated() {
                return failNum;
            }

            @Override
            public void reset() {}

            @Override
            public String getName() {
                return "aggFails";
            }

        };
    }
}