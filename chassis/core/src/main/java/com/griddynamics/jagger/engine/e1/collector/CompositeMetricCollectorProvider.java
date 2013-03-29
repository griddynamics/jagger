package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;

import java.util.LinkedList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 22.03.13
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class CompositeMetricCollectorProvider<Q, R, E>  implements KernelSideObjectProvider<ScenarioCollector<Q, R, E>> {

    private List<KernelSideObjectProvider<ScenarioCollector<Q, R, E>>> collectorProviders;


    public List<KernelSideObjectProvider<ScenarioCollector<Q, R, E>>> getCollectorProviders() {
        return collectorProviders;
    }

    public void setCollectorProviders(List<KernelSideObjectProvider<ScenarioCollector<Q, R, E>>> collectorProviders)  {
        this.collectorProviders = collectorProviders;
    }

    @Override
    public ScenarioCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        List<ScenarioCollector<Q, R, E>> collectors=new LinkedList<ScenarioCollector<Q, R, E>>();
        for(KernelSideObjectProvider<ScenarioCollector<Q, R, E>> provider : collectorProviders){
            collectors.add(provider.provide(sessionId, taskId, kernelContext));
        }
        return new CompositeMetricCollector<Q, R, E>(
                sessionId,
                taskId,
                kernelContext,
                collectors);
    }
}
