package com.griddynamics.jagger.engine.e1.collector;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.SUCCESS_RATE_AGGREGATOR_FAILED_ID;

/** Calculates accumulative number of failed invokes from data collected by @ref SuccessRateCollector<Q,R,E>
 * @author Dmitry Latnikov
 * @n
 * @par Details:
 * @details Aggregator is calculating number of fails in every point for full set of data from beginning of the test @n
 * This means you are getting accumulative value and can see in dynamics how number of fails was changing and influencing success rate. @n
 * Results of calculation are presented in SuccessRateCollector<Q,R,E> docu @n
 * @n
 * This aggregator is used by default when @xlink{metric-success-rate} collector is included in @xlink{test-description,info-collectors} block. @n
 * When necessary you can use different aggregator(s) like in example in SuccessRateCollector<Q,R,E> docu @n
 *
 * @ingroup Main_Aggregators_group */
public class SuccessRateFailsAggregatorProvider implements MetricAggregatorProvider {

    /** Method is called to provide instance of private class: \b SuccessRateFailsAggregator that implements @ref MetricAggregator<C extends Number> and provides necessary calculations */
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
            return SUCCESS_RATE_AGGREGATOR_FAILED_ID;
        }
    }
}