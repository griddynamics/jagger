package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.DefaultMetric;
import com.griddynamics.jagger.engine.e1.services.Metric;
import com.griddynamics.jagger.engine.e1.services.MetricService;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/30/13
 * Time: 8:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultMetricService implements MetricService {

    protected String sessionId;
    protected String taskId;
    protected NodeContext nodeContext;

    public DefaultMetricService(String sessionId, String taskId, NodeContext context){
        this.sessionId = sessionId;
        this.taskId = taskId;
        this.nodeContext = context;
    }

    private HashMap<String, Metric> metricMap = new HashMap<String, Metric>();

    @Override
    public boolean registerMetric(MetricDescription metricDescription) {
        KeyValueStorage storage = nodeContext.getService(KeyValueStorage.class);

        storage.put(Namespace.of(
                sessionId, taskId, "metricDescription"),
                metricDescription.getId(),
                metricDescription
        );

        DefaultMetric metric = new DefaultMetric();
        metric.init(sessionId, taskId, nodeContext);
        metric.setMetricId(metricDescription.getId());

        metricMap.put(metricDescription.getId(), metric);

        return true;
    }

    @Override
    public Metric getMetric(String id) {
        return metricMap.get(id);
    }

    @Override
    public void flushMetrics() {
        for (Metric metric : metricMap.values()){
            metric.flush();
        }
    }
}
