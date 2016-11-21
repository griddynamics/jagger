package com.griddynamics.jagger;

import static java.util.Collections.singletonList;

import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.aux.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.aux.MaxLoadThreads;
import com.griddynamics.jagger.user.test.configurations.load.aux.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.load.aux.WarmUpTimeInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.aux.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.aux.MaxDurationInSeconds;

/**
 * Created by Andrey Badaev
 * Date: 10/11/16
 */
public class ExampleJLoadScenarioProvider {
    
    public static JLoadScenario getExampleJaggerLoadScenario() {
        
        JTestDefinition jTestDefinition = JTestDefinition
                .builder(Id.of("exampleJaggerTestDefinition"), new com.griddynamics.jagger.ExampleEndpointsProvider())
                // optional
                .withComment("no comments")
                .withQueryProvider(new com.griddynamics.jagger.ExampleQueriesProvider())
                .withValidators(singletonList(NotNullResponseValidator.class))
                .build();
        
        JLoadProfile jLoadProfileRps = JLoadProfileRps
                .of(RequestsPerSecond.of(10), MaxLoadThreads.of(10), WarmUpTimeInSeconds.of(10));
        
        JTerminationCriteria jTerminationCriteria = JTerminationCriteriaIterations.of(IterationsNumber.of(1000), MaxDurationInSeconds.of(20));
        
        JLoadTest jLoadTest = JLoadTest
                .builder(Id.of("exampleJaggerLoadTest"), jTestDefinition, jLoadProfileRps, jTerminationCriteria).build();
        
        JParallelTestsGroup jParallelTestsGroup = JParallelTestsGroup
                .builder(Id.of("exampleJaggerParallelTestsGroup"), jLoadTest).build();
        
        // For JLoadScenario which is supposed to be executed by Jagger its ID must be set to 'jagger.load.scenario.id.to.execute' property's value
        return JLoadScenario.builder(Id.of("exampleJaggerLoadScenario"), jParallelTestsGroup).build();
    }
    
    public static JLoadScenario getFirstJaggerLoadScenario() {
        JTestDefinition description = JTestDefinition
                .builder(Id.of("myFirstJaggerTestDefinition"), new com.griddynamics.jagger.ExampleEndpointsProvider())
                // optional
                .withComment("no comments")
                .withQueryProvider(new com.griddynamics.jagger.ExampleQueriesProvider())
                .withValidators(singletonList(NotNullResponseValidator.class))
                .build();
        
        JLoadProfile load = JLoadProfileRps
                .of(RequestsPerSecond.of(10), MaxLoadThreads.of(10), WarmUpTimeInSeconds.of(10));
        JLoadProfile load2 = JLoadProfileRps
                .of(RequestsPerSecond.of(20), MaxLoadThreads.of(20), WarmUpTimeInSeconds.of(20));
        
        JTerminationCriteria termination = JTerminationCriteriaIterations
                .of(IterationsNumber.of(500), MaxDurationInSeconds.of(60));
        JTerminationCriteria terminationBackground = JTerminationCriteriaBackground.getInstance();
        
        JLoadTest test1 = JLoadTest
                .builder(Id.of("my_first_test"), description, load, termination).build();
        JLoadTest test2 = JLoadTest
                .builder(Id.of("yet_another_test"), description, load2, terminationBackground).build();
        
        JParallelTestsGroup testGroup = JParallelTestsGroup
                .builder(Id.of("my_first_test_group"), test1, test2).build();
        
        return JLoadScenario.builder(Id.of("myFirstJaggerLoadScenario"), testGroup).build();
    }
}
