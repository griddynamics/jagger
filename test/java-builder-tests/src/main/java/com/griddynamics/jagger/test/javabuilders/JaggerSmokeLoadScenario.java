package com.griddynamics.jagger.test.javabuilders;

import com.griddynamics.jagger.test.javabuilders.load.InvocationsLoadTests;
import com.griddynamics.jagger.test.javabuilders.load.RpsLoadTests;
import com.griddynamics.jagger.test.javabuilders.load.UserGroupsLoadTests;
import com.griddynamics.jagger.test.javabuilders.smoke_components.DummyTestListenerProvider;
import com.griddynamics.jagger.test.javabuilders.smoke_components.TestDefinitionVariations;
import com.griddynamics.jagger.test.javabuilders.smoke_components.TestLoadVariations;
import com.griddynamics.jagger.test.javabuilders.utils.JaggerPropertiesProvider;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaDuration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.DurationInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class JaggerSmokeLoadScenario extends JaggerPropertiesProvider {

    private TestDefinitionVariations definitions;
    private TestLoadVariations loads;
    private JTerminationCriteria terminate10Sec;

    /**
     * Check smoke_components with
     *  - definition has all fields with default values
     *  - rps load with one request per second and all fields with default values
     *  - termination by duration after 10 sec
     */
    private JLoadTest allDefaultsRps(){
        return JLoadTest.builder(Id.of("all definition default, all rps default, termination by duration"),
                definitions.allDefaults(),
                loads.oneRPSWithAllDefaults(),
                terminate10Sec)
                .withLimits(allSuccess())
                .build();
    }

    /**
     * Check smoke_components with
     *  - definition where all fields are specified
     *  - rps load with 100 requests per sec with max thread = 10 and warm up = 2 sec (all fields are specified)
     *  - termination after 2 iteration
     */
    private JLoadTest allFieldsRps(){
        return JLoadTest.builder(Id.of("all definition fields, all rps fields, termination by iteration"),
                definitions.allFields(),
                loads.rpsAllFields(),
                JTerminationCriteriaIterations.of(IterationsNumber.of(2), MaxDurationInSeconds.of(60)))
                .withLimits(allSuccess())
                .build();
    }

    /**
     * Check smoke_components with
     *  - several queries in definition
     *  - rps load with 10 requests and 5 sec warm up
     *  - and termination by limit of duration
     */
    private JLoadTest queriesRotationWithWarmUp(){
        return JLoadTest.builder(Id.of("Queries rotation with warm up and termination by max duration"),
                definitions.listOfQueries(),
                loads.rpsFiveSecWarmUp(),
                JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(10)))
                .withLimits(allSuccess())
                .build();
    }

    /**
     * Check smoke_components with
     *  - definition with one query
     *  - rps load with 1 requests and 1 thread max
     */
    private JLoadTest oneQueryOneThread(){
        return JLoadTest.builder(Id.of("1 query, 1 thread"),
                definitions.singleQuery(),
                loads.rpsOneThreadMax(),
                terminate10Sec)
                .build();
    }

    /**
     * Check smoke_components with
     *  - definition where only comment is specified (other fields are default)
     *  - rps load with 1 requests and 0 sec warm up
     */
    private JLoadTest zeroWarmUp(){
        return JLoadTest.builder(Id.of("zero warm up, definition with comment"),
                definitions.withComment(),
                loads.rpsWith0WarmUp(),
                terminate10Sec)
                .withLimits(allSuccess())
                .build();
    }

    /**
     * Check smoke_components with
     *  - definition with single validator
     *  - group load with one group with one user with all fields with default values
     *  - termination after one iteration
     */
    private JLoadTest groupLoadAllDefaultAndValidator(){
        return JLoadTest.builder(Id.of("Single group with one user and all defaults, single validator, one iteration"),
                definitions.singleValidator(),
                loads.singleGroupAllDefaults(),
                JTerminationCriteriaIterations.of(IterationsNumber.of(1), MaxDurationInSeconds.of(30)))
                .withLimits(allSuccess())
                .build();
    }

    /**
     * Check smoke_components with
     *  - definition with a few validators
     *  - group load with several user groups with different set of parameters
     */
    private JLoadTest listOfValidatorsAndGroups(){
        return JLoadTest.builder(Id.of("User groups load"),
                definitions.listOfValidators(),
                loads.severalGroupWithUsersVariations(),
                JTerminationCriteriaDuration.of(DurationInSeconds.of(30)))
                .withLimits(allSuccess())
                .build();
    }

    /**
     * test with cpu load to test background termination, will be used to test metrics
     */
    private JLoadTest cpuLoadWithBackgroundTermination(){
        return JLoadTest.builder(Id.of("cpu load"),
                definitions.load_cpu_service_10000000(),
                loads.oneRPSWithAllDefaults(),
                JTerminationCriteriaBackground.getInstance())
                .withLimits(allSuccess())
                .build();
    }

    /**
     * test with memory load, will be used to test metrics
     */
    private JLoadTest allocateMemoryDuring10Sec(){
        return JLoadTest.builder(Id.of("allocate memory"),
                definitions.allocate_memory_service_1000000x200(),
                loads.oneRPSWithAllDefaults(),
                terminate10Sec)
                .withLimits(allSuccess())
                .addListener(new DummyTestListenerProvider())
                .build();
    }

    /**
     * TODO remove workaround when JFG-1082 will be fixed
     */
    private JParallelTestsGroup testGroup(String id, JLoadTest... tests){
        return JParallelTestsGroup.builder(Id.of(id), Stream.of(tests).map(t->
                JLoadTest.builder(Id.of(id+"_"+t.getId()), t.getTestDescription(), t.getLoad(), t.getTermination())
                        .addListeners(t.getListeners())
                        .withLimits(t.getLimits())
                        .build())
                .collect(Collectors.toList())).build();
    }

    private JParallelTestsGroup getInvocationsLoadTests(){
        InvocationsLoadTests tests = new InvocationsLoadTests(this);
        return testGroup("InvocationsLoadTests",
                tests.testInvocationsInOneThread(),
                tests.testInvocationsSeveralThreads(),
                tests.testInvocationsTerminatedByDurationAfterInvocationsReached(),
                tests.testInvocationsTerminatedByDurationBeforeInvocationsReached(),
                tests.testInvocationsTerminatedByIterations(),
                tests.testInvocationsWithDelayBetweenInvocations(),
                tests.testInvocationsWithPeriodLoadingMaxDuration(),
                tests.testInvocationsWithPeriodLoadingMaxIterations(),
                tests.testInvocationsWithSmallerLoadingPeriod()
        );
    }

    private JParallelTestsGroup getRpsLoadTests(){
        RpsLoadTests tests = new RpsLoadTests(this);

        return testGroup("RpsLoad",
                tests.testRpsLoadLimitThreads(),
                tests.testRpsLoadWithDurationTermination(),
                tests.testRpsLoadWithIterationTermination(),
                tests.testRpsLoadWithWarmUp(),
                tests.testRpsBalancingPulse(),
                tests.testRpsBalancingRnd());
    }

    private JParallelTestsGroup getGroupLoadTests(){
        UserGroupsLoadTests tests = new UserGroupsLoadTests(this);

        return testGroup("GroupLoad",
                tests.oneUserOneGroup(),
                tests.severalUsersOneGroup(),
                tests.userGroupWithDelay(),
                tests.userGroupWithLifeTimeGreaterTestDuration(),
                // tests.userGroupWithLifeTimeLessTestDuration(), TODO uncomment when JFG-1094 will be fixed
                tests.userGroupWithSlewRate(),
                tests.userGroupWithSlewRateTerminated(),
                tests.userGroupDelayBetweenInvocations(),
                tests.fewUserGroups(),
                tests.userGroupsComplexLoad()
        );

    }

    @Bean
    public JLoadScenario getJaggerTestScenario(){
        definitions = new TestDefinitionVariations(this);
        loads = new TestLoadVariations();
        terminate10Sec = JTerminationCriteriaDuration.of(DurationInSeconds.of(10));

        JParallelTestsGroup singleTest = JParallelTestsGroup.builder(Id.of("single test"), allDefaultsRps()).build();
        JParallelTestsGroup backgroundTermination = JParallelTestsGroup.builder(Id.of("background termination"),
                cpuLoadWithBackgroundTermination(), allocateMemoryDuring10Sec()).build();
        JParallelTestsGroup severalTests = JParallelTestsGroup.builder(Id.of("several tests"), Arrays.asList(
                zeroWarmUp(),
                groupLoadAllDefaultAndValidator(),
                queriesRotationWithWarmUp(),
                oneQueryOneThread(),
                listOfValidatorsAndGroups(),
                allFieldsRps()
                )
        ).build();

        return JLoadScenario.builder(Id.of("JaggerSmokeTests"),
                singleTest,
                backgroundTermination,
                severalTests,
                getInvocationsLoadTests(),
                getRpsLoadTests(),
                getGroupLoadTests()).build();
    }

    /**
     * For loads tests debugging porpoises
     */
    @Bean
    public JLoadScenario getJaggerLoadTestScenario() {
        return JLoadScenario.builder(Id.of("LoadsTests"),
                getInvocationsLoadTests(),
                getRpsLoadTests(),
                getGroupLoadTests()
        ).build();
    }

    private JLimit allSuccess(){
        return JLimitVsRefValue.builder(JMetricName.PERF_SUCCESS_RATE_OK, RefValue.of(1.0)).build();
    }

}
