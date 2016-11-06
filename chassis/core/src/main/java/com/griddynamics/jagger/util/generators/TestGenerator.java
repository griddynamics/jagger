package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.user.test.configurations.JTest;

import static com.griddynamics.jagger.util.generators.TerminationGenerator.generateTermination;
import static com.griddynamics.jagger.util.generators.TestDescriptionGenerator.generatePrototype;
import static com.griddynamics.jagger.util.generators.WorkloadGenerator.generateLoad;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link WorkloadTask} entity from user-defined {@link JTest} entity.
 */
class TestGenerator {
    static WorkloadTask generateFromTest(JTest jTest) {
        WorkloadTask task = generatePrototype(jTest.getTestDescription());
        task.setName(jTest.getId());
        task.setVersion("0");
        TerminateStrategyConfiguration terminateStrategyConfiguration = generateTermination(jTest.getTermination());
        task.setTerminateStrategyConfiguration(terminateStrategyConfiguration);
        WorkloadClockConfiguration workloadClockConfiguration = generateLoad(jTest.getLoad());
        task.setClockConfiguration(workloadClockConfiguration);
        return task;
    }


}
