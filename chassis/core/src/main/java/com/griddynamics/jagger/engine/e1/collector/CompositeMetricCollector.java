package com.griddynamics.jagger.engine.e1.collector;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Scenario;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 22.03.13
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */

public class CompositeMetricCollector<Q, R, E> extends ScenarioCollector<Q, R, E> {

    private final List<ScenarioCollector<Q, R, E>> collectors;
    

    public CompositeMetricCollector(String sessionId, String taskId, NodeContext kernelContext, List<ScenarioCollector<Q, R, E>> collectors) {
        super(sessionId, taskId, kernelContext);
        this.collectors = ImmutableList.copyOf(collectors);
    }


    @Override
    public void flush() {
        for(ScenarioCollector collector: collectors){
            collector.flush();
        }
    }

    @Override
    public void onStart(Q query, E endpoint) {
        for(ScenarioCollector collector: collectors){
            collector.onStart(query, endpoint);
        }
    }

    @Override
    public void onSuccess(Q query, E endpoint, R result, long duration) {
        for(ScenarioCollector collector: collectors){
            collector.onSuccess(query, endpoint, result, duration);
        }
    }

    @Override
    public void onFail(Q query, E endpoint, InvocationException e) {
        for(ScenarioCollector collector: collectors){
            collector.onFail(query, endpoint, e);
        }
    }

    @Override
    public void onError(Q query, E endpoint, Throwable error) {
        for(ScenarioCollector collector: collectors){
            collector.onError(query, endpoint, error);
        }
    }
}
