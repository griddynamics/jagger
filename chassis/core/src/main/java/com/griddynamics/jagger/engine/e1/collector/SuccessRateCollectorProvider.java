package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;

import java.util.Arrays;
import java.util.List;

public class SuccessRateCollectorProvider<Q, R, E> extends MetricCollectorProvider<Q, R, E> {

    @Override
    public SuccessRateCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new SuccessRateCollector(sessionId, taskId, kernelContext, getName());
    }
}



