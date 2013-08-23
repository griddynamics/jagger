package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 8/15/13
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class InformationCollectorProvider<Q, R, E> implements KernelSideObjectProvider<ScenarioCollector<Q, R, E>> {

    private List<ValidatorProvider> providers = Collections.emptyList();

    @Override
    public ScenarioCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        ArrayList<Validator> result = new ArrayList<Validator>(providers.size());
        for (ValidatorProvider provider  : providers){
            result.add(provider.provide(taskId, sessionId, kernelContext));
        }
        return new InformationCollector(sessionId, taskId, kernelContext, result);
    }

    public void setValidators(List<ValidatorProvider> providers){
        this.providers = providers;
    }

    public List<ValidatorProvider> getValidators(){
        return providers;
    }
}
