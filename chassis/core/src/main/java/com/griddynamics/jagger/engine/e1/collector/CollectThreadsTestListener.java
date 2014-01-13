package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfo;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/2/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectThreadsTestListener extends ServicesAware implements Provider<TestListener> {

    private String metricId = "Jagger.Threads";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricId)
                                                .plotData(true)
                                                .showSummary(true)
                                                .addAggregator(new AvgMetricAggregatorProvider()));
    }

    @Override
    public TestListener provide() {
        return new TestListener() {
            @Override
            public void onStart(TestInfo testInfo) {}

            @Override
            public void onStop(TestInfo testInfo) {}

            @Override
            public void onRun(TestInfo status) {
                getMetricService().saveValue(metricId, status.getThreads());
            }
        };
    }
}
