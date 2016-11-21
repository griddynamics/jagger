package com.griddynamics.jagger.util.generators;

import static java.util.Collections.singletonList;

import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.user.test.configurations.Id;
import com.griddynamics.jagger.user.test.configurations.JTest;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;
import com.griddynamics.jagger.user.test.configurations.JTestGroup;
import com.griddynamics.jagger.user.test.configurations.JTestSuite;
import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JLoadRps;
import com.griddynamics.jagger.user.test.configurations.load.MaxLoadThreads;
import com.griddynamics.jagger.user.test.configurations.load.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.load.WarmUpTimeInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;
import com.griddynamics.jagger.user.test.configurations.termination.MaxDurationInSeconds;

/**
 * Created by Andrey Badaev
 * Date: 10/11/16
 */
public class ExampleTestSuiteProvider {
    public static JTestSuite jTestSuite() {
        JTestDescription description = JTestDescription
                .builder(Id.of("my_first_jagger_test_description"), new ExampleEndpointsProvider())
                // optional
                .withComment("no_comments")
                .withQueryProvider(new ExampleQueriesProvider())
                .withValidators(singletonList(NotNullResponseValidator.class))
                .build();
    
        JLoad load = JLoadRps.of(RequestsPerSecond.of(500), MaxLoadThreads.of(50), WarmUpTimeInSeconds.of(60));
        JLoad load2 = JLoadRps.of(RequestsPerSecond.of(100), MaxLoadThreads.of(10), WarmUpTimeInSeconds.of(24));
    
        JTermination termination = JTerminationIterations.of(IterationsNumber.of(100), MaxDurationInSeconds.of(500));
        JTermination termination2 = JTerminationIterations.of(IterationsNumber.of(100), MaxDurationInSeconds.of(100));
    
        JTest test1 = JTest.builder(Id.of("my_first_test"), description, load, termination).build();
        JTest test2 = JTest.builder(Id.of("yet_another_test"), description, load2, termination2).build();
    
        JTestGroup testGroup = JTestGroup.builder(Id.of("my_first_test_group"), test1, test2).build();
    
        return JTestSuite.builder(Id.of("my_first_test_suite"), testGroup).build();
    }
}
