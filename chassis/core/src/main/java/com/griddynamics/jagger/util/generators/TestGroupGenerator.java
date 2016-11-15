package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.user.test.configurations.JTest;
import com.griddynamics.jagger.user.test.configurations.JTestGroup;

import java.util.ArrayList;

import static com.griddynamics.jagger.util.generators.TestGenerator.generateFromTest;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link Task} entity from user-defined {@link JTestGroup} entity.
 */
class TestGroupGenerator {
    static Task generateFromTestGroup(JTestGroup jTestGroup) {
        CompositeTask compositeTask = new CompositeTask();
        compositeTask.setLeading(new ArrayList<>());
        compositeTask.setAttendant(new ArrayList<>());
        compositeTask.setName(jTestGroup.getId() + "-group");
        for (JTest test : jTestGroup.getTests()) {
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
