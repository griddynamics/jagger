package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateCollectorProvider;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateFailsAggregatorProvider;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;
import com.griddynamics.jagger.invoker.SimpleCircularLoadBalancer;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.beans.factory.config.RuntimeBeanReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author asokol
 *         created 11/6/16
 *         Generate {@link WorkloadTask} entity from user-defined {@link JTestDescription} entity.
 */
class TestDescriptionGenerator {

    static WorkloadTask generatePrototype(JTestDescription jTestDescription) {
        WorkloadTask prototype = new WorkloadTask();
        prototype.setCalibrator(new OneNodeCalibrator());
        prototype.setDescription(jTestDescription.getDescription());
        QueryPoolScenarioFactory scenarioFactory = new QueryPoolScenarioFactory();
        scenarioFactory.setQueryProvider(jTestDescription.getQueries());
        scenarioFactory.setEndpointProvider(jTestDescription.getEndpoints());
        scenarioFactory.setInvokerClazz(jTestDescription.getInvokerClass());
        scenarioFactory.setLoadBalancer(new SimpleCircularLoadBalancer() {{
            setPairSupplierFactory(new RoundRobinPairSupplierFactory());
        }});


        prototype.setScenarioFactory(scenarioFactory);
        prototype.setName(jTestDescription.getId());




//
//        List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> allMetrics = new ArrayList<>();
//        allMetrics.add((KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>) new RuntimeBeanReference("durationCollector"));
//        allMetrics.add((KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>) new RuntimeBeanReference("informationCollector"));
//        allMetrics.add(getSuccessRateMetric());
//        prototype.setCollectors(allMetrics);
        prototype.setCollectors(Collections.singletonList(getSuccessRateMetric()));
        prototype.setTestListeners(newArrayList(new CollectThreadsTestListener()));

        return prototype;
    }

    private static SuccessRateCollectorProvider getSuccessRateMetric() {
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
