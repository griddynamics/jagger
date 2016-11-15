package com.griddynamics.jagger.util.generators;

import static java.util.function.Function.identity;

import com.griddynamics.jagger.engine.e1.aggregator.session.BasicAggregator;
import com.griddynamics.jagger.engine.e1.aggregator.workload.DurationLogProcessor;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

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
    private DurationLogProcessor durationLogProcessor;
    private Map<String, JTestSuite> userJTestSuites = Collections.emptyMap();
    private boolean useBuilders;
    private final String jTestSuiteNameToExecute;
    private Map<String, Configuration> configurations = Collections.emptyMap();
    
    public ConfigurationGenerator(String jTestSuiteNameToExecute) {
        this.jTestSuiteNameToExecute = jTestSuiteNameToExecute;
    }
    
    public Set<String> getUserJTestSuiteNames() {
        if (useBuilders) {
            return new HashSet<>(userJTestSuites.keySet());
        }
        return new HashSet<>(configurations.keySet());
    }
    
    @Autowired(required = false)
    public void setUserJTestSuites(List<JTestSuite> userJTestSuites) {
        this.userJTestSuites = userJTestSuites.stream().collect(Collectors.toMap(JTestSuite::getId, identity()));
    }
    
    @Autowired(required = false)
    public void setConfigurations(Map<String, Configuration> configurations) {
        this.configurations = configurations;
    }
    
    public Configuration generate() {
        if (useBuilders) {
            JTestSuite jTestSuite = userJTestSuites.get(jTestSuiteNameToExecute);
            if (jTestSuite == null) {
                throw new IllegalArgumentException(String.format("No Jagger test suite with name %s", jTestSuiteNameToExecute));
            }
            return generate(jTestSuite);
        }

        Configuration configuration = configurations.get(jTestSuiteNameToExecute);
        if (configuration == null) {
            throw new IllegalArgumentException(String.format("No Jagger configuration with name %s", jTestSuiteNameToExecute));
        }
        return configuration;
    }
    
    /**
     * Generates {@link Configuration} from {@link JTestSuite}.
     *
     * @param jTestSuite user configuration.
     * @return jagger configuration.
     */
    public Configuration generate(JTestSuite jTestSuite) {
        Configuration configuration = new Configuration();
        List<Task> tasks = jTestSuite.getTestGroups().stream().map(TestGroupGenerator::generateFromTestGroup)
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
        teListeners.add(durationLogProcessor);
        
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
    
    public void setDurationLogProcessor(DurationLogProcessor durationLogProcessor) {
        this.durationLogProcessor = durationLogProcessor;
    }
    
    public boolean isUseBuilders() {
        return useBuilders;
    }
    
    public void setUseBuilders(boolean useBuilders) {
        this.useBuilders = useBuilders;
    }
    
    public String getjTestSuiteNameToExecute() { return jTestSuiteNameToExecute; }
}
