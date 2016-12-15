package com.griddynamics.jagger;

import com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener;
import com.griddynamics.jagger.engine.e1.collector.DefaultResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.ExampleResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
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
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowErrThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowWarnThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpErrThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpWarnThresh;
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

import static java.util.Collections.singletonList;

@Configuration
public class ExampleJLoadScenarioProvider {

    @Bean
    public JLoadScenario exampleJaggerLoadScenario() {

        JTestDefinition jTestDefinition = JTestDefinition
                .builder(Id.of("exampleJaggerTestDefinition"), new ExampleEndpointsProvider())
                // optional
                .withComment("no comments")
                .withQueryProvider(new ExampleQueriesProvider())
                .addValidator(new ExampleResponseValidatorProvider("we are always good"))
                .addValidator(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class))
                .addListener(new NotNullInvocationListener())
                .build();

        JTerminationCriteria jTerminationCriteria = JTerminationCriteriaIterations.of(IterationsNumber.of(1000), MaxDurationInSeconds.of(20));

        JLoadProfile jLoadProfileRps = JLoadProfileRps
                .builder(RequestsPerSecond.of(10))
                .withMaxLoadThreads(10)
                .withWarmUpTimeInSeconds(10)
                .build();

        // For standard metrics use JMetricName.
        // JLimitVsRefValue is used to compare the results with the referenced value.
        JLimit successrateLimit = JLimitVsRefValue.builder(JMetricName.SUCCESS_RATE_OK, RefValue.of(10D))
                                                  // the threshold is relative.
                                                  .withOnlyWarnings(LowWarnThresh.of(0.1), UpWarnThresh.of(1.5))
                                                  .build();

        // For standard metrics use JMetricName.
        // JLimitVsBaseline is used to compare the results with the baseline.
        // Use 'chassis.engine.e1.reporting.session.comparison.baseline.session.id' to set baseline.
        JLimit throughputLimit = JLimitVsBaseline.builder(JMetricName.THROUGHPUT)
                                                 // the threshold is relative.
                                                 .withOnlyErrors(LowErrThresh.of(0.99), UpErrThresh.of(1.00001))
                                                 .build();

        JLoadTest jLoadTest = JLoadTest
                .builder(Id.of("exampleJaggerLoadTest"), jTestDefinition, jLoadProfileRps, jTerminationCriteria)
                .addListener(new CollectThreadsTestListener())
                .withLimits(successrateLimit, throughputLimit)
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
                .addValidators(singletonList(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class)))
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