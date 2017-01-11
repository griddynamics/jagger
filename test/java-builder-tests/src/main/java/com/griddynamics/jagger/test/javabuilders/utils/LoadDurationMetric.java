package com.griddynamics.jagger.test.javabuilders.utils;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;

/**
 * Calculate load duration instead of test duration in JMetricName.PERF_DURATION
 */
public class LoadDurationMetric extends ServicesAware implements Provider<InvocationListener> {
    public static String NAME = "loadDuration";

    @Override
    protected void init() {
        MetricDescription metricDescription = new MetricDescription(NAME)
                .displayName(NAME)
                .plotData(false)
                .showSummary(true)
                .addAggregator(new FirstLastDifferenceAggregatorProvider());
        getMetricService().createMetric(metricDescription);
    }

    @Override
    public InvocationListener provide() {
        return new InvocationListener() {
            private void append(){
                getMetricService().saveValue(NAME, System.currentTimeMillis()/1000.0);
            }

            @Override
            public void onStart(InvocationInfo invocationInfo) {
                append();
            }

            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
               append();
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) {
                append();
            }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) {
                append();
            }
        };
    }

}
