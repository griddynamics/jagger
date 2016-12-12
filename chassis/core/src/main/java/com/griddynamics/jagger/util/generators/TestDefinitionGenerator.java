package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.collector.DurationCollector;
import com.griddynamics.jagger.engine.e1.collector.InformationCollector;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateCollectorProvider;
import com.griddynamics.jagger.engine.e1.collector.SuccessRateFailsAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.ValidatorProvider;
import com.griddynamics.jagger.engine.e1.scenario.ReflectionProvider;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;
import com.griddynamics.jagger.invoker.SimpleCircularLoadBalancer;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import org.springframework.beans.factory.support.ManagedList;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.SUCCESS_RATE;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.SUCCESS_RATE_ID;
import java.util.List;

/**
 *
 * @author asokol
 *         created 11/6/16
 *         Generate {@link WorkloadTask} entity from user-defined {@link JTestDefinition} entity.
 */
class TestDefinitionGenerator {

    public static WorkloadTask generatePrototype(JTestDefinition jTestDefinition) {

        WorkloadTask prototype = new WorkloadTask();
        prototype.setDescription(jTestDefinition.getDescription());
        QueryPoolScenarioFactory scenarioFactory = new QueryPoolScenarioFactory();
        scenarioFactory.setQueryProvider(jTestDefinition.getQueries());
        scenarioFactory.setEndpointProvider(jTestDefinition.getEndpoints());
        scenarioFactory.setInvokerClazz(jTestDefinition.getInvoker());
        scenarioFactory.setLoadBalancer(new SimpleCircularLoadBalancer() {{
            setPairSupplierFactory(new RoundRobinPairSupplierFactory());
        }});
        prototype.setScenarioFactory(scenarioFactory);

        prototype.setName(jTestDefinition.getId());

        ManagedList collectors = new ManagedList();
        collectors.add(getSuccessRateMetric());
        collectors.add(ReflectionProvider.ofClass(DurationCollector.class));
        collectors.add(ReflectionProvider.ofClass(InformationCollector.class));
        prototype.setCollectors(collectors);

        ManagedList validators = new ManagedList();
        for (Class<? extends ResponseValidator> clazz: jTestDefinition.getValidators()) {
            ValidatorProvider validatorProvider = new ValidatorProvider();
            validatorProvider.setValidator(ReflectionProvider.ofClass(clazz));
            validators.add(validatorProvider);
        }
        prototype.setValidators(validators);

        prototype.setListeners((List) jTestDefinition.getListeners());


        return prototype;
    }

    private static SuccessRateCollectorProvider getSuccessRateMetric() {
        MetricDescription metricDescriptions = new MetricDescription(SUCCESS_RATE_ID)
                .displayName(SUCCESS_RATE)
                .plotData(true)
                .showSummary(true)
                .addAggregator(new SuccessRateAggregatorProvider())
                .addAggregator(new SuccessRateFailsAggregatorProvider());
        SuccessRateCollectorProvider successRateCollectorProvider = new SuccessRateCollectorProvider();
        successRateCollectorProvider.setMetricDescription(metricDescriptions);
        successRateCollectorProvider.setName(SUCCESS_RATE_ID);
        return successRateCollectorProvider;
    }
}
