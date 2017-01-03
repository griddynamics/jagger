package com.griddynamics.jagger;

import com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener;
import com.griddynamics.jagger.engine.e1.collector.DefaultResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.ExampleResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.JHttpResponseStatusValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.invocation.ExampleInvocationListener;
import com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.ExampleLoadScenarioListener;
import com.griddynamics.jagger.engine.e1.collector.testgroup.ExampleTestGroupListener;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsBaseline;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.*;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleJLoadScenarioProvider {

    @Bean
    public JLoadScenario exampleJaggerLoadScenario() {

        // begin: following section is used for docu generation - example of the invocation listener
        JTestDefinition jTestDefinition = JTestDefinition
                .builder(Id.of("exampleJaggerTestDefinition"), new ExampleEndpointsProvider())
                // optional
                .withComment("no comments")
                .withQueryProvider(new ExampleQueriesProvider())
                .addValidator(new ExampleResponseValidatorProvider("we are always good"))
                .addValidator(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class))
                .addValidator(JHttpResponseStatusValidatorProvider.of(200, 201, 204))
                .addListener(new NotNullInvocationListener())
                .addListener(new ExampleInvocationListener())
                .build();
        // end: following section is used for docu generation - example of the invocation listener

        JTerminationCriteria jTerminationCriteria = JTerminationCriteriaIterations.of(IterationsNumber.of(1000), MaxDurationInSeconds.of(20));

        JLoadProfile jLoadProfileRps = JLoadProfileRps
                .builder(RequestsPerSecond.of(10))
                .withMaxLoadThreads(10)
                .withWarmUpTimeInMilliseconds(10000)
                .build();

        // begin: following section is used for docu generation - example of the limits

        // For standard metrics use JMetricName.
        // JLimitVsRefValue is used to compare the results with the referenced value.
        // Thresholds are relative values. In the example below, accepted range for the Success rate metric is:
        // 0.99 * 1.0 <= Accepted values <= 1.0 * 1.01
        JLimit successRateLimit = JLimitVsRefValue.builder(JMetricName.PERF_SUCCESS_RATE_OK, RefValue.of(1D))
                                                  .withOnlyWarnings(LowWarnThresh.of(0.99), UpWarnThresh.of(1.01))
                                                  .build();

        // For standard metrics use JMetricName.
        // JLimitVsBaseline is used to compare the results with the baseline.
        // Use 'chassis.engine.e1.reporting.session.comparison.baseline.session.id' to set baseline.
        // Thresholds are relative values. In the example below, accepted range for the Throughput metric is:
        // 0.99 * Reference value from the baseline session <= Accepted values <= Ref value * 1.00001
        JLimit throughputLimit = JLimitVsBaseline.builder(JMetricName.PERF_THROUGHPUT)
                                                 .withOnlyErrors(LowErrThresh.of(0.99), UpErrThresh.of(1.00001))
                                                 .build();

        JLoadTest jLoadTest = JLoadTest
                .builder(Id.of("exampleJaggerLoadTest"), jTestDefinition, jLoadProfileRps, jTerminationCriteria)
                .addListener(new CollectThreadsTestListener())
                .withLimits(successRateLimit, throughputLimit)
                .build();

        // end: following section is used for docu generation - example of the limits

        // begin: following section is used for docu generation - example of the test group listener

        JParallelTestsGroup jParallelTestsGroup = JParallelTestsGroup
                .builder(Id.of("exampleJaggerParallelTestsGroup"), jLoadTest)
                .addListener(new ExampleTestGroupListener())
                .build();

        // end: following section is used for docu generation - example of the test group listener

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
                .addValidator(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class))
                .addValidator(JHttpResponseStatusValidatorProvider.of("(200|201|203)"))
                .build();

        JLoadProfile load = JLoadProfileRps.builder(RequestsPerSecond.of(10)).withMaxLoadThreads(10).withWarmUpTimeInMilliseconds(10000).build();
        JLoadProfile load2 = JLoadProfileRps.builder(RequestsPerSecond.of(20)).withMaxLoadThreads(20).withWarmUpTimeInMilliseconds(20000).build();

        JTerminationCriteria termination = JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(60));
        JTerminationCriteria terminationBackground = JTerminationCriteriaBackground.getInstance();

        JLoadTest test1 = JLoadTest.builder(Id.of("my_first_test"), description, load, termination).build();
        JLoadTest test2 = JLoadTest.builder(Id.of("yet_another_test"), description, load2, terminationBackground).build();

        JParallelTestsGroup testGroup = JParallelTestsGroup.builder(Id.of("my_first_test_group"), test1, test2).build();

        return JLoadScenario.builder(Id.of("myFirstJaggerLoadScenario"), testGroup).build();
    }
}
