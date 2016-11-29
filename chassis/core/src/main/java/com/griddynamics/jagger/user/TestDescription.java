package com.griddynamics.jagger.user;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateCollectorProvider;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateFailsAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.scenario.Calibrator;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.invoker.ScenarioFactory;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 8/23/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
@Deprecated
public class TestDescription {

    private List<KernelSideObjectProvider<Validator>> validators;

    private List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> metrics;
    private List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> standardCollectors;
    private List<Provider<InvocationListener<Object, Object, Object>>> listeners;

    private ScenarioFactory<Object, Object, Object> scenarioFactory;
    private Calibrator calibrator = new OneNodeCalibrator();
    private String description = "";
    private String version;
    private String name;

    public List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> getStandardCollectors() {
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

    public List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> metrics) {
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

    public WorkloadTask generatePrototype() {
        WorkloadTask prototype = new WorkloadTask();
        prototype.setCalibrator(calibrator);
        prototype.setDescription(description);
        prototype.setScenarioFactory(scenarioFactory);
        prototype.setName(name);
        prototype.setVersion(version);
        prototype.setValidators(validators);
        prototype.setListeners(listeners);
        prototype.setTestListeners(newArrayList(new CollectThreadsTestListener()));

        List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> allMetrics = new ArrayList<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>>(metrics.size() + standardCollectors.size());
        allMetrics.addAll(standardCollectors);
        allMetrics.addAll(metrics);
        allMetrics.add(getSuccessRateMetric());
        prototype.setCollectors(allMetrics);

        return prototype;
    }

    private SuccessRateCollectorProvider getSuccessRateMetric() {
        MetricDescription metricDescriptions = new MetricDescription("SR")
                .displayName(StandardMetricsNamesUtil.SUCCESS_RATE)
                .plotData(true)
                .showSummary(true)
                .addAggregator(new SuccessRateAggregatorProvider())
                .addAggregator(new SuccessRateFailsAggregatorProvider());
        SuccessRateCollectorProvider successRateCollectorProvider = new SuccessRateCollectorProvider();
        successRateCollectorProvider.setMetricDescription(metricDescriptions);
        successRateCollectorProvider.setName("SR");
        return successRateCollectorProvider;
    }
}
