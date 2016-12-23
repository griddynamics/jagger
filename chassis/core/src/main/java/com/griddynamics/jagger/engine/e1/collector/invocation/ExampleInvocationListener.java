package com.griddynamics.jagger.engine.e1.collector.invocation;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MaxMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.MinMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.PercentileAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;

/** Example of the invocation listener
 * @author Anton Antonenko
 * @n
 * @par Details:
 * @details
 * Collects response time metrics. Max, min, average, percentiles
 *
 * @ingroup Main_Listeners_group */
public class ExampleInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final String metricName = "example-duration-metric";

    @Override
    protected void init() {

        //begin: following section is used for docu generation - example of the metric with multiple aggregators
        getMetricService().createMetric(new MetricDescription(metricName)
                .displayName("Example duration metric, ms")
                .showSummary(true)
                .plotData(true)
                .addAggregator(new MinMetricAggregatorProvider())
                .addAggregator(new MaxMetricAggregatorProvider())
                .addAggregator(new AvgMetricAggregatorProvider())
                .addAggregator(new PercentileAggregatorProvider(40D))
                .addAggregator(new PercentileAggregatorProvider(50D))
                .addAggregator(new PercentileAggregatorProvider(60D))
                .addAggregator(new PercentileAggregatorProvider(70D))
                .addAggregator(new PercentileAggregatorProvider(80D))
                .addAggregator(new PercentileAggregatorProvider(90D))
                .addAggregator(new PercentileAggregatorProvider(95D))
                .addAggregator(new PercentileAggregatorProvider(99D))
        );
        //end: following section is used for docu generation - example of the metric with multiple aggregators
    }

    @Override
    public InvocationListener provide() {
        return new InvocationListener() {
            @Override
            public void onStart(InvocationInfo invocationInfo) { }

            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
                if (invocationInfo.getResult() != null) {
                    getMetricService().saveValue(metricName, invocationInfo.getDuration());
                }
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) { }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) { }
        };
    }
}
