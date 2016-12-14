#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.MetricCollector;
import com.griddynamics.jagger.engine.e1.collector.SimpleMetricCalculator;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MetricLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ExampleSuccessRateCollector<Q, R, E> extends MetricCollector<Q, R, E> {
    private static final Logger log = LoggerFactory.getLogger(ExampleSuccessRateCollector.class);

    private long startTime = 0;

    public ExampleSuccessRateCollector(String sessionId, String taskId, NodeContext kernelContext) {
        super(sessionId, taskId, kernelContext,new SimpleMetricCalculator(),"exampleSuccessRate");
    }

    @Override
    public void flush() {
    }

    @Override
    public void onStart(Object query, Object endpoint) {
        // Remember invoke start time
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onSuccess(Object query, Object endpoint, Object result, long duration) {
        // Count pass result
        remember(1);
    }

    @Override
    public void onFail(Object query, Object endpoint, InvocationException e) {
        // Count fail result
        remember(0);
    }

    @Override
    public void onError(Object query, Object endpoint, Throwable error) {
        // Count fail result
        remember(0);
    }

    private void remember(long result) {
        // Log result to Kernel storage
        String METRIC_MARKER = "METRIC";
        LogWriter logWriter = kernelContext.getService(LogWriter.class);
        logWriter.log(sessionId, taskId + File.separatorChar + METRIC_MARKER + File.separatorChar + "exampleSuccessRate", kernelContext.getId().getIdentifier(),
                new MetricLogEntry(startTime, "exampleSuccessRate", result));
    }

}