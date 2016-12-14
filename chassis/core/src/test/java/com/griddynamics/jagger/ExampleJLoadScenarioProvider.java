package com.griddynamics.jagger;

import static java.util.Collections.singletonList;

import com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener;
import com.griddynamics.jagger.engine.e1.collector.testgroup.ExampleTestGroupListener;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.ExampleLoadScenarioListener;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import com.griddynamics.jagger.util.JaggerPropertiesProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * By extending {@link JaggerPropertiesProvider} you get access to all Jagger properties, which you can use
 * for configuration of JLoadScenario.<p>
 * Benefit of this approach is that you can change JLoadScenario configuration by changing properties file and no recompilation is needed.
 */
@Configuration
public class ExampleJLoadScenarioProvider extends JaggerPropertiesProvider {
    
    @Bean
    public JLoadScenario exampleJaggerLoadScenario() {
        
        JTestDefinition jTestDefinition = JTestDefinition
                .builder(Id.of("exampleJaggerTestDefinition"), new ExampleEndpointsProvider())
                // optional
                .withComment("no comments")
                .withQueryProvider(new ExampleQueriesProvider())
                .addValidator(NotNullResponseValidator.class)
                .addListener(new NotNullInvocationListener())
                .build();
        
        // Example of using JaggerPropertiesProvider
        Long iterationsNumber = Long.valueOf(getPropertyValue("example.jagger.load.scenario.termination.iterations"));
        Long maxDurationInSeconds = Long.valueOf(getPropertyValue("example.jagger.load.scenario.termination.max.duration.seconds"));
        JTerminationCriteria jTerminationCriteria = JTerminationCriteriaIterations
                .of(IterationsNumber.of(iterationsNumber), MaxDurationInSeconds.of(maxDurationInSeconds));

        JLoadProfile jLoadProfileRps = JLoadProfileRps
                .builder(RequestsPerSecond.of(10))
                .withMaxLoadThreads(10)
                .withWarmUpTimeInSeconds(10)
                .build();
        
        JLoadTest jLoadTest = JLoadTest
                .builder(Id.of("exampleJaggerLoadTest"), jTestDefinition, jLoadProfileRps, jTerminationCriteria)
                .addListener(new CollectThreadsTestListener())
                .build();
        
        JParallelTestsGroup jParallelTestsGroup = JParallelTestsGroup
                .builder(Id.of("exampleJaggerParallelTestsGroup"), jLoadTest)
                .addListener(new ExampleTestGroupListener())
                .build();
        
        // For JLoadScenario which is supposed to be executed by Jagger its ID must be set to 'jagger.load.scenario.id.to.execute' property's value
        return JLoadScenario.builder(Id.of("exampleJaggerLoadScenario"), jParallelTestsGroup)
                            .addListener(new ExampleLoadScenarioListener())
                            .build();
    }
    
    @Bean
    public JLoadScenario myFirstJaggerLoadScenario() {
        JTestDefinition description = JTestDefinition
                .builder(Id.of("myFirstJaggerTestDefinition"), new ExampleEndpointsProvider())
                // optional
                .withComment("no comments")
                .withQueryProvider(new ExampleQueriesProvider())
                .addValidators(singletonList(NotNullResponseValidator.class))
                .build();
        
        JLoadProfile load = JLoadProfileRps.builder(RequestsPerSecond.of(10)).withMaxLoadThreads(10).withWarmUpTimeInSeconds(10).build();
        JLoadProfile load2 = JLoadProfileRps.builder(RequestsPerSecond.of(20)).withMaxLoadThreads(20).withWarmUpTimeInSeconds(20).build();
        
        JTerminationCriteria termination = JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(60));
        JTerminationCriteria terminationBackground = JTerminationCriteriaBackground.getInstance();
        
        JLoadTest test1 = JLoadTest.builder(Id.of("my_first_test"), description, load, termination).build();
        JLoadTest test2 = JLoadTest.builder(Id.of("yet_another_test"), description, load2, terminationBackground).build();
        
        JParallelTestsGroup testGroup = JParallelTestsGroup.builder(Id.of("my_first_test_group"), test1, test2).build();
        
        return JLoadScenario.builder(Id.of("myFirstJaggerLoadScenario"), testGroup).build();
    }
}
