package com.griddynamics.jagger.test.jagger2;

import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.user.test.configurations.JTest;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;
import com.griddynamics.jagger.user.test.configurations.JTestGroup;
import com.griddynamics.jagger.user.test.configurations.JTestSuite;
import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JLoadRps;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;

import java.util.Collections;


public class TestSuiteProvider {
    public static JTestSuite jTestSuites() {
        JTestDescription sleep_service_5ms_simple_http_query = JTestDescription.builder()
                .withId("sleep-service-5ms-simple-http-query-provider")
                .withComment("sleep-service-5ms-simple-http-query-provider")
                .withEndpointsProvider(EndpointsProvider.getEndpoints())
                .withQueryProvider(new QueriesProvider("/sleep/5"))
                .withInvoker(DefaultHttpInvoker.class)
//                .withValidators(NotNullResponseValidator.class, metric.TrueValidator.class)
//                .addListeners(new NotNullInvocationListener())
//                .addMetrics("metric-success-rate", "metric-not-null-response")
//                .withQueryDistributor("query-distributor-round-robin")
                .build();


        JLoad load = JLoadRps.builder()
                .withMaxLoadThreads(1)
                .withRequestPerSecond(100)
                .withWarmUpTimeInSeconds(0)
                .build();

        JTermination termination = JTerminationIterations.builder()
                .withIterationsCount(1000)
                .withMaxDurationInSeconds(500)
                .build();

        JTest test1 = JTest.builder()
                .withJTestDescription(sleep_service_5ms_simple_http_query)
                .withLoad(load)
                .withTermination(termination)
                .withId("my_first_test")
                .build();

        JTestGroup testGroup = JTestGroup.builder()
                .withId("my_first_test_group")
                .withTests(Collections.singletonList(test1))
                .build();

        return JTestSuite.builder()
                .withId("s_exampleConfiguration")
                .withTestGroups(Collections.singletonList(testGroup))
                .build();
    }
}
