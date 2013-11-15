package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.collector.MetricDescription;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/30/13
 * Time: 8:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MetricService {
    boolean registerMetric(MetricDescription metricDescription);
    Metric getMetric(String id);
    void flushMetrics();
}
