package ${package}.user.scenario.example;

import com.griddynamics.jagger.invoker.scenario.JHttpUserScenarioInvocationListener;
import com.griddynamics.jagger.invoker.scenario.JHttpUserScenarioInvokerProvider;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowErrThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpErrThresh;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;
import com.griddynamics.jagger.user.test.configurations.loadbalancer.JLoadBalancer;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.griddynamics.jagger.user.test.configurations.loadbalancer.JLoadBalancer.DefaultLoadBalancer.ROUND_ROBIN;
import static ${package}.user.scenario.example.UserScenarioEndpointsProvider.SCENARIO_ID;
import static ${package}.user.scenario.example.UserScenarioEndpointsProvider.STEP_1_ID;
import static ${package}.user.scenario.example.UserScenarioEndpointsProvider.STEP_2_ID;

//begin: following section is used for docu generation - User scenario execution

/**
 * Example of user scenario load scenario
 * We are creating load test (load scenario) with the multiple user scnearios (sequence of actions) to execute
 */
@Configuration
public class UserScenarioJLoadScenarioProvider {

    @Bean
    public JLoadScenario exampleSimpleJaggerLoadScenarioUS() {

        //begin: following section is used for docu generation - Load balancer setup

        JTestDefinition jTestDefinition =
                JTestDefinition.builder(Id.of("td_user_scenario_example"), new UserScenarioEndpointsProvider())
                        .withInvoker(new JHttpUserScenarioInvokerProvider())
                        // Exclusive access - single scenario is executed only by one virtual user at a time. No parallel execution
                        // Random seed - different virtual users execute scenarios in different order
                        .withLoadBalancer(JLoadBalancer.builder(ROUND_ROBIN)
                                .withExclusiveAccess()
                                .withRandomSeed(1234)
                                .build())
                        .addListener(JHttpUserScenarioInvocationListener.builder()
                                .withLatencyAvgStddevAggregators()
                                .withLatencyMinMaxAggregators()
                                .withLatencyPercentileAggregators(50D, 95D, 99D)
                                .build())
                        .build();

        //end: following section is used for docu generation - Load balancer setup

        JLoadProfile jLoadProfileInvocations =
                JLoadProfileInvocation.builder(InvocationCount.of(100), ThreadCount.of(2))
                        .build();

        JTerminationCriteria jTerminationCriteria =
                JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(50));

        // We are setting acceptance criteria for particular metric of the selected step in the scenario
        JLimit avgLatencyLimit =
                JLimitVsRefValue.builder(SCENARIO_ID, STEP_1_ID, StandardMetricsNamesUtil.LATENCY_AVG_AGG_ID, RefValue.of(1.2))
                        .withOnlyErrors(LowErrThresh.of(0.25), UpErrThresh.of(2.0))
                        .build();
        JLimit stdDevLatencyLimit =
                JLimitVsRefValue.builder(SCENARIO_ID, STEP_1_ID, StandardMetricsNamesUtil.LATENCY_STD_DEV_AGG_ID, RefValue.of(0.5))
                        .withOnlyErrors(LowErrThresh.of(0.5), UpErrThresh.of(1.5))
                        .build();
        JLimit maxLatencyLimit =
                JLimitVsRefValue.builder(SCENARIO_ID, STEP_2_ID, StandardMetricsNamesUtil.LATENCY_MAX_AGG_ID, RefValue.of(2.0))
                        .withOnlyErrors(LowErrThresh.of(0.5), UpErrThresh.of(1.5))
                        .build();
        JLimit minDevLatencyLimit =
                JLimitVsRefValue.builder(SCENARIO_ID, STEP_2_ID, StandardMetricsNamesUtil.LATENCY_MIN_AGG_ID, RefValue.of(0.2))
                        .withOnlyErrors(LowErrThresh.of(0.5), UpErrThresh.of(1.5))
                        .build();
        JLimit percentile99LatencyLimit =
                JLimitVsRefValue.builder(SCENARIO_ID, STEP_2_ID, JMetricName.PERF_LATENCY_PERCENTILE(99D), RefValue.of(2.0))
                        .withOnlyErrors(LowErrThresh.of(0.5), UpErrThresh.of(1.5))
                        .build();
        JLimit successRateLimit =
                JLimitVsRefValue.builder(SCENARIO_ID, STEP_1_ID, JMetricName.PERF_SUCCESS_RATE_OK, RefValue.of(1.0))
                        .withOnlyErrors(LowErrThresh.of(0.99), UpErrThresh.of(1.01))
                        .build();
        JLimit errorsLimit =
                JLimitVsRefValue.builder(SCENARIO_ID, STEP_2_ID, JMetricName.PERF_SUCCESS_RATE_FAILS, RefValue.of(0.0))
                        .withOnlyErrors(LowErrThresh.of(0.99), UpErrThresh.of(1.01))
                        .build();

        JLoadTest jLoadTest =
                JLoadTest.builder(Id.of("lt_user_scenario_example"), jTestDefinition, jLoadProfileInvocations, jTerminationCriteria)
                        .withLimits(avgLatencyLimit, stdDevLatencyLimit, minDevLatencyLimit, maxLatencyLimit, percentile99LatencyLimit, successRateLimit, errorsLimit)
                        .build();

        JParallelTestsGroup jParallelTestsGroup =
                JParallelTestsGroup.builder(Id.of("ptg_user_scenario_example"), jLoadTest)
                        .build();

        // To launch your load scenario, set 'jagger.load.scenario.id.to.execute' property's value equal to the load scenario id
        // You can do it via system properties or in the 'environment.properties' file
        return JLoadScenario.builder(Id.of("ls_user_scenario_example"), jParallelTestsGroup)
                .build();
    }
}

//end: following section is used for docu generation - User scenario execution


