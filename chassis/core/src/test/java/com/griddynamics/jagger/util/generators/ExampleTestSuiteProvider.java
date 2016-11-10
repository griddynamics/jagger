package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.user.test.configurations.JTest;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;
import com.griddynamics.jagger.user.test.configurations.JTestGroup;
import com.griddynamics.jagger.user.test.configurations.JTestSuite;
import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JLoadRps;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Andrey Badaev (andrey.badaev@kohls.com)
 * Date: 10/11/16
 */
public class ExampleTestSuiteProvider {
    public static List<JTestSuite> jTestSuites() {
        JTestDescription description = JTestDescription.builder()
                                                       .withComment("My comment")
                                                       .withId("popa")
                                                       .withEndpointsProvider(new ExampleEndpointsProvider())
                                                       .withQueryProvider(new ExampleQueriesProvider())
                                                       .withInvoker(new ExamplePageVisitorInvoker())
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
                           .withId("popa2")
                           .build();
        
        JTest test2 = JTest.builder()
                           .withJTestDescription(description)
                           .withLoad(load2)
                           .withTermination(termination2)
                           .withId("popa3")
                           .build();
        
        
        JTestGroup testGroup = JTestGroup.builder()
                                         .withId("anti popa")
                                         .withTests(Arrays.asList(test1, test2))
                                         .build();
        
        JTestSuite configuration = JTestSuite.builder("my_first_test")
                                             .withTestGroups(Collections.singletonList(testGroup))
                                             .build();
        
        return Collections.singletonList(configuration);
    }
}
