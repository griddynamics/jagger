#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.provider;

import ${package}.collector.ExampleSuccessRateCollector;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;

import java.util.Arrays;
import java.util.List;

public class ProviderOfExampleSuccessRateCollector<Q, R, E> extends MetricCollectorProvider<Q, R, E> {

    @Override
    public void init(java.lang.String sessionId, java.lang.String taskId, NodeContext kernelContext) {
        KeyValueStorage storage = kernelContext.getService(KeyValueStorage.class);
        storage.put(Namespace.of(
                sessionId, taskId, "metricDescription"),
                "successRate",
                aggregators
        );
    }

    @Override
    public ExampleSuccessRateCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new ExampleSuccessRateCollector(sessionId, taskId, kernelContext);
    }

    @Override
    public void setMetricDescription(MetricDescription description) {
    }

    @Override
    public List<MetricDescription> getMetricDescription() {
        return new MetricDescription("successRate")
                .showPlotData(true)
                .showSummary(true)
                .addAggregator(new ProviderOfExampleSuccessRateAggregator())
                .addAggregator(new ProviderOfFailCountAggregator());
    }
}
