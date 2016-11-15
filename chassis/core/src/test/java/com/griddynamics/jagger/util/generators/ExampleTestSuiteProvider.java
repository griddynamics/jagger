package com.griddynamics.jagger.util.generators;

import static java.util.Collections.singletonList;

import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.user.test.configurations.JTest;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;
import com.griddynamics.jagger.user.test.configurations.JTestGroup;
import com.griddynamics.jagger.user.test.configurations.JTestSuite;
import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JLoadRps;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;

import java.util.Arrays;

/**
 *
 * Created by Andrey Badaev
 * Date: 10/11/16
 */
public class ExampleTestSuiteProvider {
    public static JTestSuite jTestSuite() {
        JTestDescription description = JTestDescription.builder()
                                                       .withId("my_first_jagger_test_description")
                                                       .withComment("no_comments")
                                                       .withEndpointsProvider(new ExampleEndpointsProvider())
                                                       .withQueryProvider(new ExampleQueriesProvider())
                                                       .withValidators(singletonList(NotNullResponseValidator.class))
                                                       .build();
        
        JLoad load = JLoadRps.builder()
                             .withMaxLoadThreads(50)
                             .withRequestPerSecond(100)
                             .withWarmUpTimeInSeconds(24)
                             .build();
        JLoad load2 = JLoadRps.builder()
                              .withMaxLoadThreads(500)
                              .withRequestPerSecond(500)
                              .withWarmUpTimeInSeconds(42)
                              .build();
        
        JTermination termination = JTerminationIterations.builder()
                                                         .withIterationsCount(100)
                                                         .withMaxDurationInSeconds(500)
                                                         .build();
        
        JTermination termination2 = JTerminationIterations.builder()
                                                          .withIterationsCount(100)
                                                          .withMaxDurationInSeconds(100)
                                                          .build();
        
        JTest test1 = JTest.builder()
                           .withJTestDescription(description)
                           .withLoad(load)
                           .withTermination(termination)
                           .withId("my_first_test")
                           .build();
        
        JTest test2 = JTest.builder()
                           .withId("yet_another_test")
                           .withJTestDescription(description)
                           .withLoad(load2)
                           .withTermination(termination2)
                           .build();
        
        
        JTestGroup testGroup = JTestGroup.builder()
                                         .withId("my_first_test_group")
                                         .withTests(Arrays.asList(test1, test2))
                                         .build();
    
        
        JTestSuite jTestSuite = JTestSuite.builder()
                                             .withId("my_first_test_suite")
                                             .withTestGroups(singletonList(testGroup))
                                             .build();
        
        return jTestSuite;
    }
}
