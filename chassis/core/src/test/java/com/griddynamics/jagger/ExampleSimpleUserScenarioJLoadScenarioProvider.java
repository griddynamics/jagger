package com.griddynamics.jagger;

import com.griddynamics.jagger.invoker.scenario.JHttpUserScenarioInvocationListener;
import com.griddynamics.jagger.invoker.scenario.JHttpUserScenarioInvokerProvider;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowErrThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpErrThresh;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.generateMetricId;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.generateScenarioStepId;

/**
 * Example of user scenario load scenario
 */
@Configuration
public class ExampleSimpleUserScenarioJLoadScenarioProvider {

    @Bean
    public JLoadScenario exampleSimpleJaggerLoadScenarioUS() {

        JTestDefinition jTestDefinition = JTestDefinition.builder(Id.of("td_example"), new ExampleUserScenarioProvider())
                .withInvoker(new JHttpUserScenarioInvokerProvider())
                .addListener(new JHttpUserScenarioInvocationListener())
                .build();

        JLoadProfile jLoadProfileInvocations = JLoadProfileInvocation.builder(InvocationCount.of(100), ThreadCount.of(2)).build();

        JTerminationCriteria jTerminationCriteria = JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(50));

        //TODO: JFG-1123
        String stepId = generateScenarioStepId(ExampleUserScenarioProvider.SCENARIO_ID, ExampleUserScenarioProvider.STEP_1_ID, 1);
        String metricId = generateMetricId(stepId, StandardMetricsNamesUtil.LATENCY_ID);
        JLimit firstStepLimit = JLimitVsRefValue.builder(metricId + "-avg", RefValue.of(1.5))
                .withOnlyErrors(LowErrThresh.of(0.8), UpErrThresh.of(1.2))
                .build();

        JLoadTest jLoadTest = JLoadTest.builder(Id.of("lt_example"), jTestDefinition, jLoadProfileInvocations, jTerminationCriteria)
                .withLimits(firstStepLimit)
                .build();

        JParallelTestsGroup jParallelTestsGroup = JParallelTestsGroup.builder(Id.of("ptg_example"), jLoadTest).build();

        return JLoadScenario.builder(Id.of("ls_example_scenario"), jParallelTestsGroup).build();
    }
}

