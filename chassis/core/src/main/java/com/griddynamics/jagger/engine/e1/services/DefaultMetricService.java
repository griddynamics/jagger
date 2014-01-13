package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MetricLogEntry;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/30/13
 * Time: 8:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultMetricService implements MetricService {
    public static final String METRIC_MARKER = "METRIC";

    protected String sessionId;
    protected String taskId;
    protected NodeContext context;

    public DefaultMetricService(String sessionId, String taskId, NodeContext context){
        this.sessionId = sessionId;
        this.taskId = taskId;
        this.context = context;
    }

    @Override
    public void createMetric(MetricDescription metricDescription) {
        KeyValueStorage storage = context.getService(KeyValueStorage.class);

        storage.put(Namespace.of(sessionId, taskId, "metricDescription"),
                    metricDescription.getMetricId(),
                    metricDescription
        );
    }

    @Override
    public void saveValue(String metricId, Number value) {
        long current = System.currentTimeMillis();
        saveValue(metricId, value, current);
    }

    @Override
    public void saveValue(String metricId, Number value, long timeStamp) {
        LogWriter logWriter = context.getService(LogWriter.class);
        logWriter.log(sessionId, taskId + File.separatorChar + METRIC_MARKER + File.separatorChar + metricId, context.getId().getIdentifier(),
                new MetricLogEntry(timeStamp, metricId, value));
    }

    @Override
    public void flush() {
        LogWriter logWriter = context.getService(LogWriter.class);
        logWriter.flush();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
