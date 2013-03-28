package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.storage.fs.logging.DurationLogEntry;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.ThreadLocalMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.TOTAL_DURATION;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.TOTAL_SQR_DURATION;

/**
 * @author Sergey Pulyaev
 * Date: 23.10.12
 */
public class MultiDurationCollector extends ScenarioCollector {
    public static final String DURATION_MARKER = "MULTI_DURATION";
    private static final Logger log = LoggerFactory.getLogger(MultiDurationCollector.class);

    private double totalDuration = 0;
    private double totalDurationSqr = 0;


    public MultiDurationCollector(String sessionId, String taskId, NodeContext kernelContext) {
        super(sessionId, taskId, kernelContext);
    }

    @Override
    public void onStart(Object query, Object endpoint) {
    }

    @Override
    public void onSuccess(Object query, Object endpoint, Object result, long duration) {
        rememberDurations();
    }

    @Override
    public void onFail(Object query, Object endpoint, InvocationException e) {
    }

    @Override
    public void onError(Object query, Object endpoint, Throwable error) {
    }

    private void rememberDurations() {
        Long endTime = System.currentTimeMillis();

        Map<String, Integer> durations = ThreadLocalMetrics.listLike(ThreadLocalMetrics.METRIC_DURATION_MARKER);
        if(durations != null) {
            for (Map.Entry<String, Integer> duration : durations.entrySet()) {
                double durationSeconds = (double) duration.getValue() / 1000;
                totalDuration = totalDuration + durationSeconds;
                totalDurationSqr = totalDurationSqr + (durationSeconds * durationSeconds);

                LogWriter logWriter = kernelContext.getService(LogWriter.class);
                long startTime = endTime - duration.getValue();
                logWriter.log(sessionId, taskId + "/" + DURATION_MARKER + "/" + duration.getKey(), kernelContext.getId().getIdentifier(),
                        new DurationLogEntry(startTime, duration.getValue()));
            }
        }
    }

    @Override
    public void flush() {
        Namespace namespace = Namespace.of(sessionId, taskId, "DurationMultiCollector", kernelContext.getId().toString());
        kernelContext.getService(KeyValueStorage.class).put(namespace, TOTAL_DURATION, totalDuration);
        log.debug("saved total_duration for namespace {}", namespace);
        kernelContext.getService(KeyValueStorage.class).put(namespace, TOTAL_SQR_DURATION, totalDurationSqr);
        log.debug("saved total_sqr_duration for namespace {}", namespace);
    }

    private static final long serialVersionUID = 8230019294210626531L;
}
