package com.griddynamics.jagger.util;

import com.griddynamics.jagger.engine.e1.aggregator.session.BasicAggregator;
import com.griddynamics.jagger.engine.e1.aggregator.workload.MetricLogProcessor;
import com.griddynamics.jagger.engine.e1.aggregator.workload.ProfilerLogProcessor;
import com.griddynamics.jagger.engine.e1.aggregator.workload.WorkloadAggregator;
import com.griddynamics.jagger.engine.e1.collector.BasicSessionCollector;
import com.griddynamics.jagger.engine.e1.collector.MasterWorkloadCollector;
import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.engine.e1.scenario.RpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;
import com.griddynamics.jagger.invoker.SimpleCircularLoadBalancer;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.user.test.configurations.JTest;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;
import com.griddynamics.jagger.user.test.configurations.JTestGroup;
import com.griddynamics.jagger.user.test.configurations.JTestSuite;
import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JLoadRps;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationDuration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;
import org.springframework.beans.factory.support.ManagedList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Generates {@link Configuration} entity
 * from {@link JTestSuite} object.
 */
public class ConfigurationGenerator {

    private BasicSessionCollector basicSessionCollector;
    private MasterWorkloadCollector e1MasterCollector;
    private BasicAggregator basicAggregator;
    private WorkloadAggregator e1ScenarioAggregator;
    private MetricLogProcessor metricLogProcessor;
    private ProfilerLogProcessor profilerLogProcessor;


    /**
     * Generates {@link Configuration} from {@code jTestConfiguration}.
     *
     * @param jTestConfiguration user configuration.
     * @return jagger configuration.
     */
    public Configuration generate(JTestSuite jTestConfiguration) {
        Configuration configuration = new Configuration();
        List<Task> tasks = jTestConfiguration.getTestGroups().stream().
                map(this::generateFromTestGroup)
                .collect(Collectors.toList());
        configuration.setTasks(tasks);

        ManagedList seListeners = new ManagedList();
        seListeners.add(basicSessionCollector);
        seListeners.add(basicAggregator);


        ManagedList teListeners = new ManagedList();
        teListeners.add(basicSessionCollector);
        teListeners.add(basicAggregator);
        teListeners.add(e1MasterCollector);
        teListeners.add(e1ScenarioAggregator);
        teListeners.add(metricLogProcessor);
        teListeners.add(profilerLogProcessor);


        configuration.setSessionExecutionListeners(seListeners);
        configuration.setTaskExecutionListeners(teListeners);

        return configuration;
    }

    private WorkloadTask generatePrototype(JTestDescription jTestDescription) {
        WorkloadTask prototype = new WorkloadTask();
        // this is default value in xml-implementation.
        prototype.setCalibrator(new OneNodeCalibrator());
        // perhaps it is the time to change the name of this field
        prototype.setDescription(jTestDescription.getDescription());
        // As we discuss, now  this is default scenario factory
        QueryPoolScenarioFactory scenarioFactory = new QueryPoolScenarioFactory();
        // So, maybe I can rid of the others
        scenarioFactory.setQueryProvider(jTestDescription.getQueries());
        scenarioFactory.setEndpointProvider(jTestDescription.getEndpoints());
        scenarioFactory.setInvokerClazz(jTestDescription.getInvokerClass());
        scenarioFactory.setLoadBalancer(new SimpleCircularLoadBalancer() {{
            setPairSupplierFactory(new RoundRobinPairSupplierFactory());
        }});


        //set Invoker ?? I don't remember what did I want to say by this comment
        prototype.setScenarioFactory(scenarioFactory);
        // The {@link TestDescription} has a field name we've changed it to id. Is it ok?
        prototype.setName(jTestDescription.getId());

        // what about listeners, collectors, validators and metrics?
        return prototype;
    }

    private WorkloadTask generateFromTest(JTest jTest) {
        WorkloadTask task = generatePrototype(jTest.getTestDescription());
        task.setName(jTest.getId());
        // there should be a number
        // so what is number?
        // task.setNumber();
        task.setVersion("0");
        // there also might be a start delay
        // what is start delay?
        // limits?
        // listeners
        TerminateStrategyConfiguration terminateStrategyConfiguration = generateTermination(jTest.getTermination());
        task.setTerminateStrategyConfiguration(terminateStrategyConfiguration);
        WorkloadClockConfiguration workloadClockConfiguration = generateLoad(jTest.getLoad());
        task.setClockConfiguration(workloadClockConfiguration);
        return task;
    }

    private Task generateFromTestGroup(JTestGroup jTestGroup) {
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

    private WorkloadClockConfiguration generateLoad(JLoad jLoad) {
        WorkloadClockConfiguration clockConfiguration = null;
        if (jLoad instanceof JLoadRps) {
            clockConfiguration = new RpsClockConfiguration();
            ((RpsClockConfiguration) clockConfiguration).setValue(((JLoadRps) jLoad).getRequestsPerSecond());
            ((RpsClockConfiguration) clockConfiguration).setWarmUpTime(((JLoadRps) jLoad).getWarmUpTimeInSeconds());
            ((RpsClockConfiguration) clockConfiguration).setMaxThreadNumber((int) ((JLoadRps) jLoad).getMaxLoadThreads());
        }
        return clockConfiguration;
    }

    private TerminateStrategyConfiguration generateTermination(JTermination jTermination) {
        TerminateStrategyConfiguration termination = null;
        if (jTermination instanceof JTerminationIterations) {
            termination = new IterationsOrDurationStrategyConfiguration();
            String duration = parseDuration(((JTerminationIterations) jTermination).getMaxDurationInSeconds());
            ((IterationsOrDurationStrategyConfiguration) termination).setDuration(duration);
            ((IterationsOrDurationStrategyConfiguration) termination).setIterations(
                    (int) ((JTerminationIterations) jTermination).getIterationCount());
            ((IterationsOrDurationStrategyConfiguration) termination).setShutdown(new AtomicBoolean(false));
        } else if (jTermination instanceof JTerminationDuration) {
            termination = new IterationsOrDurationStrategyConfiguration();
            String duration = parseDuration(((JTerminationDuration) jTermination).getDurationInSeconds());
            ((IterationsOrDurationStrategyConfiguration) termination).setDuration(duration);
            ((IterationsOrDurationStrategyConfiguration) termination).setShutdown(new AtomicBoolean(false));
        } else if (jTermination instanceof JTerminationBackground) {
            termination = new InfiniteTerminationStrategyConfiguration();
        }
        return termination;
    }

    private String parseDuration(long durationInSecond) {
        return durationInSecond + "s";
    }


    public void setBasicSessionCollector(BasicSessionCollector basicSessionCollector) {
        this.basicSessionCollector = basicSessionCollector;
    }

    public void setE1MasterCollector(MasterWorkloadCollector e1MasterCollector) {
        this.e1MasterCollector = e1MasterCollector;
    }

    public void setBasicAggregator(BasicAggregator basicAggregator) {
        this.basicAggregator = basicAggregator;
    }

    public void setE1ScenarioAggregator(WorkloadAggregator e1ScenarioAggregator) {
        this.e1ScenarioAggregator = e1ScenarioAggregator;
    }

    public void setMetricLogProcessor(MetricLogProcessor metricLogProcessor) {
        this.metricLogProcessor = metricLogProcessor;
    }

    public void setProfilerLogProcessor(ProfilerLogProcessor profilerLogProcessor) {
        this.profilerLogProcessor = profilerLogProcessor;
    }
}
