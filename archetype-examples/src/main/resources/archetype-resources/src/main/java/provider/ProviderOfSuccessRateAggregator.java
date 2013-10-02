#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.provider;

import com.griddynamics.jagger.engine.e1.collector.MetricAggregator;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;

public class ProviderOfSuccessRateAggregator implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new MetricAggregator() {

            int passNum = 0;
            int failNum = 0;

            @Override
            public void append(Integer calculated)
            {
                if (calculated!=0)
                    failNum++;
                else
                    passNum++;
            }

            @Override
            public Integer getAggregated() {
                // September 2013 - metrics can store only long values => store in 0.01% instead of %
                return ((failNum + passNum) == 0) ? 0 : (int)((double) ((passNum) * 10000 / (double) (failNum + passNum)));
            }

            @Override
            public void reset() {
            }

            @Override
            public String getName() {
                return "aggSR, 0.01%";
            }

        };
    }
}