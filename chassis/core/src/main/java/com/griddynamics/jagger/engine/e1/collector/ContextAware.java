package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.NodeSideInitializable;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/10/13
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ContextAware implements NodeSideInitializable, Serializable{
    protected String sessionId;
    protected String taskId;
    protected NodeContext nodeContext;

    protected String name;
    protected MetricDescription metricDescription;

    @Override
    public void init(String sessionId, String taskId, NodeContext nodeContext) {
        this.sessionId = sessionId;
        this.taskId = taskId;
        this.nodeContext = nodeContext;

        KeyValueStorage storage = nodeContext.getService(KeyValueStorage.class);
        storage.put(Namespace.of(
                sessionId, taskId, "metricDescription"),
                metricDescription.getMetricId(),
                metricDescription
        );
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMetricDescription(MetricDescription metricDescriptions) {
        this.metricDescription = metricDescriptions;
    }

    public MetricDescription getMetricDescriptions() {
        return metricDescription;
    }
}
