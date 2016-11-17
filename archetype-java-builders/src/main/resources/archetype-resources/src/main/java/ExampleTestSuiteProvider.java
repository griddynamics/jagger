package com.griddynamics.jagger;

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
import com.griddynamics.jagger.util.generators.ExampleEndpointsProvider;

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
    
        JLoad load = JLoadRps.of(RequestsPerSecond.of(100), MaxLoadThreads.of(100), WarmUpTimeInSeconds.of(60));
    
        JTermination termination = JTerminationIterations.of(IterationsNumber.of(5000), MaxDurationInSeconds.of(60 * 60));
    
        JTest test1 = JTest.builder(Id.of("my_first_test"), description, load, termination).build();
    
        JTestGroup testGroup = JTestGroup.builder(Id.of("my_first_test_group"), test1).build();

        // For JTestSuite which is supposed to be executed during test run its ID must be set to 'chassis.master.session.configuration.bean.name' property's value
        return JTestSuite.builder(Id.of("s_exampleConfiguration"), testGroup).build();
    }
}
