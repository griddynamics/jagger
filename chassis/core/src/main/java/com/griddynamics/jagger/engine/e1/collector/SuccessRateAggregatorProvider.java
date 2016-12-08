package com.griddynamics.jagger.engine.e1.collector;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.SUCCESS_RATE_AGGREGATOR_OK_ID;

/** Calculates accumulative success rate from data collected by @ref SuccessRateCollector<Q,R,E>
 * @author Dmitry Latnikov
 * @n
 * @par Details:
 * @details Aggregator is calculating success rate in every point for full set of data from beginning of the test @n
 * This means you are getting accumulative value and can see how particular fails are influencing total success rate. @n
 * Results of calculation are presented in SuccessRateCollector<Q,R,E> docu @n
 * @n
 * This aggregator is used by default when @xlink{metric-success-rate} collector is included in @xlink{test-description,info-collectors} block. @n
 * When necessary you can use different aggregator(s) like in example in SuccessRateCollector<Q,R,E> docu @n
 *
 * @ingroup Main_Aggregators_group */
public class SuccessRateAggregatorProvider implements MetricAggregatorProvider {

    /** Method is called to provide instance of private class: \b SuccessRateAggregator that implements @ref MetricAggregator<C extends Number> and provides necessary calculations */
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
                return 0.0;
            else
                return (double) (passNum) / (double) (failNum + passNum);
        }

        @Override
        public void reset() {
        }

        @Override
        public String getName() {
            return SUCCESS_RATE_AGGREGATOR_OK_ID;
        }
    }
}