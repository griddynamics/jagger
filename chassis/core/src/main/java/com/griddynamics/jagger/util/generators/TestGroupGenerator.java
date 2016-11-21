package com.griddynamics.jagger.util.generators;

import static com.griddynamics.jagger.util.generators.TestGenerator.generateFromTest;

import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;

import java.util.ArrayList;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link Task} entity from user-defined {@link JParallelTestsGroup} entity.
 */
class TestGroupGenerator {
    static Task generateFromTestGroup(JParallelTestsGroup jParallelTestsGroup) {
        CompositeTask compositeTask = new CompositeTask();
        compositeTask.setLeading(new ArrayList<>());
        compositeTask.setAttendant(new ArrayList<>());
        compositeTask.setName(jParallelTestsGroup.getId() + "-group");
        for (JLoadTest test : jParallelTestsGroup.getTests()) {
            WorkloadTask task = generateFromTest(test);
            if (task.getTerminateStrategyConfiguration() instanceof InfiniteTerminationStrategyConfiguration) {
                compositeTask.getAttendant().add(task);
            } else {
                compositeTask.getLeading().add(task);
            }
        }
        return compositeTask;
    }
}
