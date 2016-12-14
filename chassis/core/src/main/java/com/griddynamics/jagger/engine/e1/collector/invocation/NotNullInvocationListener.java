package com.griddynamics.jagger.engine.e1.collector.invocation;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.SumMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;

/**
 * User: kgribov
 * Date: 2/6/14
 */
public class NotNullInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final String metricName = "not-null-responses";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricName).
                                            displayName("Not null responses").
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
