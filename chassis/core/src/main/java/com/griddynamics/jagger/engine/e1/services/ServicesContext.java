package com.griddynamics.jagger.engine.e1.services;


import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.DefaultMetricService;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/30/13
 * Time: 8:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServicesContext {

    private final MetricService metricService;

    public ServicesContext(String sessionId, String taskId, NodeContext context){
        metricService = new DefaultMetricService(sessionId, taskId, context);
    }

    public MetricService getMetricService() {
        return metricService;
    }
}
