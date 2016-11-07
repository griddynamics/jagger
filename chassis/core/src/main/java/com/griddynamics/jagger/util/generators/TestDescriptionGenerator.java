package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;
import com.griddynamics.jagger.invoker.SimpleCircularLoadBalancer;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;

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
        return prototype;
    }
}
