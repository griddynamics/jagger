package com.griddynamics.jagger.engine.e1.collector.invocation;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.SumMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;

/** Example of the invocation listener
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * Will collect number of not null responses from the SUT
 *
 * @ingroup Main_Listeners_group */
/* begin: following section is used for docu generation - example of the invocation listener with metric service */
public class NotNullInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final String metricName = "not-null-responses";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricName).
                                            displayName("Number of not null responses").
                                            showSummary(true).
                                            plotData(false).
                                            addAggregator(new SumMetricAggregatorProvider()));
    }

    @Override
    public InvocationListener provide() {
        return new InvocationListener() {
            @Override
            public void onStart(InvocationInfo invocationInfo) {
            }

            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
                if (invocationInfo.getResult() != null){
                    getMetricService().saveValue(metricName, 1);
                }
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) {
            }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) {
            }
        };
    }
}
/* end: following section is used for docu generation - example of the invocation listener with metric service */
