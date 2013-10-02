#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.provider;

import com.griddynamics.jagger.collector.SuccessRateCollector;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;

import java.util.Arrays;
import java.util.List;

public class ProviderOfSuccessRateCollector<Q, R, E> extends MetricCollectorProvider<Q, R, E> {

    @Override
    public void init(java.lang.String sessionId, java.lang.String taskId, NodeContext kernelContext) {
        KeyValueStorage storage = kernelContext.getService(KeyValueStorage.class);
        storage.put(Namespace.of(
                sessionId, taskId, "metricAggregatorProviders"),
                "successRate",
                aggregators
        );
    }

    @Override
    public SuccessRateCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new SuccessRateCollector(sessionId, taskId, kernelContext);
    }

    @Override
    public void setAggregators(List<MetricDescriptionEntry> aggregators) {
    }

    @Override
    public List<MetricDescriptionEntry> getAggregators() {
        return aggregators;
    }

    private List<MetricDescriptionEntry> aggregators = Arrays.asList(
            new MetricDescriptionEntry(new ProviderOfSuccessRateAggregator(), true),
            new MetricDescriptionEntry(new ProviderOfFailCountAggregator(), true));

}
