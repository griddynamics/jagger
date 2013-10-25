package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;


public class SuccessRateCollectorProvider<Q, R, E> extends ContextAware implements KernelSideObjectProvider<ScenarioCollector<Q, R, E>> {

    @Override
    public SuccessRateCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new SuccessRateCollector(sessionId, taskId, kernelContext, getName());
    }
}



