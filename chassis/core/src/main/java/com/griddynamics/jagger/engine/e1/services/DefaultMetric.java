package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MetricLogEntry;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/30/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultMetric implements Metric {
    public static final String METRIC_MARKER = "METRIC";

    private String sessionId;
    private String taskId;
    private NodeContext context;

    private String metricId;

    @Override
    public String getId() {
        return metricId;
    }

    @Override
    public void save(long timeStamp, Number value) {
        LogWriter logWriter = context.getService(LogWriter.class);
        logWriter.log(sessionId, taskId + File.separatorChar + METRIC_MARKER + File.separatorChar + metricId, context.getId().getIdentifier(),
                new MetricLogEntry(timeStamp, metricId, value));
    }

    @Override
    public void save(Number value) {
        long current = System.currentTimeMillis();
        save(current, value);
    }

    @Override
    public void init(String sessionId, String taskId, NodeContext context) {
        this.sessionId = sessionId;
        this.taskId = taskId;
        this.context = context;
    }

    @Override
    public void flush() {
        LogWriter logWriter = context.getService(LogWriter.class);
        logWriter.flush();
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }
}
