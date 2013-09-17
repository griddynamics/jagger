package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideInitializableObjectProvider;
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

    private List<ValidatorProvider> validatorsProviders = Collections.emptyList();
    private List<KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>>> collectorsProviders = Collections.emptyList();

    @Override
    public ScenarioCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {

        //create validator chain
        ArrayList<Validator> validators = new ArrayList<Validator>(validatorsProviders.size());
        for (ValidatorProvider provider  : validatorsProviders){
            validators.add(provider.provide(taskId, sessionId, kernelContext));
        }

        //create a list of collectors
        ArrayList<ScenarioCollector> collectors = new ArrayList<ScenarioCollector>(collectorsProviders.size());
        for (KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>> provider : collectorsProviders){
            if (provider instanceof KernelSideInitializableObjectProvider){
                ((KernelSideInitializableObjectProvider) provider).init(sessionId, taskId, kernelContext);
            }
            collectors.add(provider.provide(sessionId, taskId, kernelContext));
        }

        return new InformationCollector(sessionId, taskId, kernelContext, validators, collectors);
    }

    public void setValidatorsProviders(List<ValidatorProvider> providers){
        this.validatorsProviders = providers;
    }

    public List<ValidatorProvider> getValidators(){
        return validatorsProviders;
    }

    public List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> getCollectorsProviders() {
        return collectorsProviders;
    }

    public void setCollectorsProviders(List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> collectorsProviders) {
        this.collectorsProviders = collectorsProviders;
    }
}
