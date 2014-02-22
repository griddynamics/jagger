package com.griddynamics.jagger.user;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.scenario.*;
import com.griddynamics.jagger.invoker.ScenarioFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 8/23/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestDescription {

    private List<KernelSideObjectProvider<Validator>> validators;

    private List<KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>>> metrics;
    private List<KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>>> standardCollectors;
    private List<Provider<InvocationListener<Object, Object, Object>>> listeners;

    private ScenarioFactory<Object, Object, Object> scenarioFactory;
    private Calibrator calibrator = new OneNodeCalibrator();
    private String description = "";
    private String version;
    private String name;

    public List<KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>>> getStandardCollectors() {
        return standardCollectors;
    }

    public void setStandardCollectors(List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> standardCollectors) {
        this.standardCollectors = standardCollectors;
    }

    public Calibrator getCalibrator() {
        return calibrator;
    }

    public void setCalibrator(Calibrator calibrator) {
        this.calibrator = calibrator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<KernelSideObjectProvider<Validator>> getValidators() {
        return validators;
    }

    public void setValidators(List<KernelSideObjectProvider<Validator>> validators) {
        this.validators = validators;
    }

    public List<KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>>> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>>> metrics) {
        this.metrics = metrics;
    }

    public ScenarioFactory<Object, Object, Object> getScenarioFactory() {
        return scenarioFactory;
    }

    public void setScenarioFactory(ScenarioFactory<Object, Object, Object> scenarioFactory) {
        this.scenarioFactory = scenarioFactory;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Provider<InvocationListener<Object, Object, Object>>> getListeners() {
        return listeners;
    }

    public void setListeners(List<Provider<InvocationListener<Object, Object, Object>>> listeners) {
        this.listeners = listeners;
    }

    public WorkloadTask generatePrototype(){
        WorkloadTask prototype = new WorkloadTask();
        prototype.setCalibrator(calibrator);
        prototype.setDescription(description);
        prototype.setScenarioFactory(scenarioFactory);
        prototype.setName(name);
        prototype.setVersion(version);
        prototype.setValidators(validators);
        prototype.setListeners(listeners);

        List<KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>>> allMetrics = new ArrayList<KernelSideObjectProvider<ScenarioCollector<Object,Object,Object>>>(metrics.size()+ standardCollectors.size());
        allMetrics.addAll(standardCollectors);
        allMetrics.addAll(metrics);

        prototype.setCollectors(allMetrics);

        return prototype;
    }
}
