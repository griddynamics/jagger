package ${package};

import static java.util.Collections.singletonList;

import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.MaxLoadThreads;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.WarmUpTimeInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;

/**
 * Created by Andrey Badaev
 * Date: 10/11/16
 */
public class ExampleJLoadScenarioProvider {
    
    public static JLoadScenario getExampleJaggerLoadScenario() {
        
        JTestDefinition jTestDefinition = JTestDefinition
                .builder(Id.of("exampleJaggerTestDefinition"), new ExampleEndpointsProvider())
                // optional
                .withComment("no comments")
                .withQueryProvider(new ExampleQueriesProvider())
                .withValidators(singletonList(NotNullResponseValidator.class))
                .build();
        
        JLoadProfile jLoadProfileRps = JLoadProfileRps.builder(RequestsPerSecond.of(10)).withMaxLoadThreads(10).withWarmUpTimeInSeconds(10).build();
        
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
                .builder(Id.of("myFirstJaggerTestDefinition"), new ExampleEndpointsProvider())
                // optional
                .withComment("no comments")
                .withQueryProvider(new ExampleQueriesProvider())
                .withValidators(singletonList(NotNullResponseValidator.class))
                .build();
        
        JLoadProfile load = JLoadProfileRps.builder(RequestsPerSecond.of(10)).withMaxLoadThreads(10).withWarmUpTimeInSeconds(10).build();
        JLoadProfile load2 = JLoadProfileRps.builder(RequestsPerSecond.of(20)).withMaxLoadThreads(20).withWarmUpTimeInSeconds(20).build();
        
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
