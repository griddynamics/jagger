package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.aggregator.session.BasicAggregator;
import com.griddynamics.jagger.engine.e1.aggregator.workload.MetricLogProcessor;
import com.griddynamics.jagger.engine.e1.aggregator.workload.ProfilerLogProcessor;
import com.griddynamics.jagger.engine.e1.aggregator.workload.WorkloadAggregator;
import com.griddynamics.jagger.engine.e1.collector.BasicSessionCollector;
import com.griddynamics.jagger.engine.e1.collector.MasterWorkloadCollector;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.SessionExecutionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.user.test.configurations.JTestSuite;
import org.springframework.beans.factory.support.ManagedList;

import java.util.List;
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
    private List<JTestSuite> userConfigurations;

    public void setUserConfigurations(List<JTestSuite> userConfigurations) {
        this.userConfigurations = userConfigurations;
    }

    public Configuration generate() {
        return generate(userConfigurations.iterator().next());
    }

    /**
     * Generates {@link Configuration} from {@code jTestConfiguration}.
     *
     * @param jTestConfiguration user configuration.
     * @return jagger configuration.
     */
    public Configuration generate(JTestSuite jTestConfiguration) {
        Configuration configuration = new Configuration();
        List<Task> tasks = jTestConfiguration.getTestGroups().stream().
                map(TestGroupGenerator::generateFromTestGroup)
                .collect(Collectors.toList());
        configuration.setTasks(tasks);

        ManagedList<SessionExecutionListener> seListeners = new ManagedList<>();
        seListeners.add(basicSessionCollector);
        seListeners.add(basicAggregator);


        ManagedList<DistributionListener> teListeners = new ManagedList<>();
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
