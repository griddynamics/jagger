package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadExecutionStatus;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MetricLogEntry;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/10/13
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class CalculatorStatusCollector extends CalculatorContextAware<WorkloadExecutionStatus> implements WorkloadStatusCollector {

    public static final String METRIC_MARKER = "METRIC";

    @Override
    public void collect(WorkloadExecutionStatus status) {
        LogWriter logWriter = nodeContext.getService(LogWriter.class);
        long startTime = System.currentTimeMillis();
        logWriter.log(sessionId, taskId + File.separatorChar + METRIC_MARKER + File.separatorChar + name, nodeContext.getId().getIdentifier(),
                new MetricLogEntry(startTime, name,  getMetricCalculator().calculate(status).doubleValue()));
    }

    @Override
    public void flush() {
        nodeContext.getService(LogWriter.class).flush();
    }
}
