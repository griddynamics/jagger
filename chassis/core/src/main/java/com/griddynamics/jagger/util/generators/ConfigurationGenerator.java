package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.engine.e1.scenario.RpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.user.test.configurations.JTest;
import com.griddynamics.jagger.user.test.configurations.JTestConfiguration;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;
import com.griddynamics.jagger.user.test.configurations.JTestGroup;
import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JLoadRps;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Generates {@link Configuration} entity
 * from {@link JTestConfiguration} object.
 */
public class ConfigurationGenerator {

    /**
     * Generates {@link Configuration} from {@code jTestConfiguration}.
     *
     * @param jTestConfiguration user configuration.
     * @return jagger configuration.
     */
    public static Configuration generate(JTestConfiguration jTestConfiguration) {
        Configuration configuration = new Configuration();
        List<Task> tasks = jTestConfiguration.getTestGroups().stream().
                map(ConfigurationGenerator::generateFromTestGroup)
                .collect(Collectors.toList());
        configuration.setTasks(tasks);

        return configuration;
    }

    private static WorkloadTask generatePrototype(JTestDescription jTestDescription) {
        WorkloadTask prototype = new WorkloadTask();
        prototype.setCalibrator(new OneNodeCalibrator());
        prototype.setDescription(jTestDescription.getDescription());
        QueryPoolScenarioFactory scenarioFactory = new QueryPoolScenarioFactory();
        scenarioFactory.setQueryProvider(jTestDescription.getQueries());
        scenarioFactory.setEndpointProvider(jTestDescription.getEndpoints());
        //set Invoker
        prototype.setScenarioFactory(scenarioFactory);
        prototype.setName(jTestDescription.getId());
        return prototype;
    }

    private static WorkloadTask generateFromTest(JTest jTest) {
        WorkloadTask task = generatePrototype(jTest.getTestDescription());
        task.setName(jTest.getId());
        // there should be a number
        // task.setNumber();
        task.setVersion("0");
        TerminateStrategyConfiguration terminateStrategyConfiguration = generateTermination(jTest.getTermination());
        task.setTerminateStrategyConfiguration(terminateStrategyConfiguration);
        WorkloadClockConfiguration workloadClockConfiguration = generateLoad(jTest.getLoad());
        task.setClockConfiguration(workloadClockConfiguration);
        return task;
    }

    private static Task generateFromTestGroup(JTestGroup jTestGroup) {
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

    private static WorkloadClockConfiguration generateLoad(JLoad jLoad) {
        WorkloadClockConfiguration clockConfiguration = null;
        if (jLoad instanceof JLoadRps) {
            clockConfiguration = new RpsClockConfiguration();
            ((RpsClockConfiguration) clockConfiguration).setValue(((JLoadRps) jLoad).getRequestsPerSecond());
            ((RpsClockConfiguration) clockConfiguration).setWarmUpTime(((JLoadRps) jLoad).getWarmUpTimeInSeconds());
            ((RpsClockConfiguration) clockConfiguration).setMaxThreadNumber((int) ((JLoadRps) jLoad).getMaxLoadThreads());
        }
        return clockConfiguration;
    }

    private static TerminateStrategyConfiguration generateTermination(JTermination jTermination) {
        TerminateStrategyConfiguration termination = null;
        if (jTermination instanceof JTerminationIterations) {
            termination = new IterationsOrDurationStrategyConfiguration();
            String duration = parseDuration(((JTerminationIterations) jTermination).getMaxDurationInSeconds());
            ((IterationsOrDurationStrategyConfiguration) termination).setDuration(duration);
            ((IterationsOrDurationStrategyConfiguration) termination).setIterations(
                    (int) ((JTerminationIterations) jTermination).getIterationCount());
            ((IterationsOrDurationStrategyConfiguration) termination).setShutdown(new AtomicBoolean(false));


        }
        return termination;
    }

    private static String parseDuration(long durationInSecond) {
        return "2y";
    }
}
