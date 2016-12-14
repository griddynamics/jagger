package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MetricLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/** Implementation of the @ref MetricService
 * @n
 * @par Details:
 * @details  Service gives an ability to create and describe metrics, save metric values.@n
 * Where this service is available you can find in chapter: @ref section_listeners_services @n
 * @n
 * @ingroup Main_Services_group */
public class DefaultMetricService implements MetricService {
    private static final Logger log = LoggerFactory.getLogger(DefaultMetricService.class);


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
        try {
            metricId = URLEncoder.encode(metricId, "UTF-8");
            logWriter.log(sessionId, taskId + File.separatorChar + METRIC_MARKER + File.separatorChar + metricId, context.getId().getIdentifier(),
                    new MetricLogEntry(timeStamp, metricId, value));
        } catch (UnsupportedEncodingException e) {
            log.error("Can't save metric value with id={}", metricId, e);
        }
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
