package com.griddynamics.jagger.test.jaas;

import com.griddynamics.jagger.engine.e1.collector.DefaultResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.JHttpResponseStatusValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.invoker.v2.DefaultInvokerProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.test.jaas.invoker.ExecutionManipulateInvoker;
import com.griddynamics.jagger.test.jaas.listener.LoadScenarioConfigListener;
import com.griddynamics.jagger.test.jaas.provider.QueryProvider;
import com.griddynamics.jagger.test.jaas.util.JaggerPropertiesProvider;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BadRequest_ResponseContentValidator;
import com.griddynamics.jagger.test.jaas.validator.executions.CreateExecutionResponseValidator;
import com.griddynamics.jagger.test.jaas.validator.executions.ExListResponseValidator;
import com.griddynamics.jagger.test.jaas.validator.executions.ExResponseValidator;
import com.griddynamics.jagger.test.jaas.validator.metrics.MetricsListResponseContentValidator;
import com.griddynamics.jagger.test.jaas.validator.metrics.PlotListResponseContentValidator;
import com.griddynamics.jagger.test.jaas.validator.metrics.SummaryListResponseContentValidator;
import com.griddynamics.jagger.test.jaas.validator.sessions.SessionResponseContentValidator;
import com.griddynamics.jagger.test.jaas.validator.sessions.SessionsListResponseContentValidator;
import com.griddynamics.jagger.test.jaas.validator.tests.TestResponseContentValidator;
import com.griddynamics.jagger.test.jaas.validator.tests.TestsListResponseContentValidator;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Configuration
public class JaasScenario extends JaggerPropertiesProvider {
    private Stream<JParallelTestsGroup> sessionTests(QueryProvider queryProvider) {
        JParallelTestsGroup tg_JaaS_GET_Sessions = JParallelTestsGroup.builder(Id.of("tg_JaaS_GET_Sessions"),
                getTestWithResponseCode200("t_JaaS_GET_Sessions_List_SR_eq._1.0", queryProvider.GET_SessionsList(), new SessionsListResponseContentValidator()),
                getTestWithResponseCode200("t_JaaS_GET_Exact_Session_SR_eq._1.0", queryProvider.GET_SessionIds(), new SessionResponseContentValidator())
        ).build();

        JParallelTestsGroup tg_JaaS_GET_Tests = JParallelTestsGroup.builder(Id.of("tg_JaaS_GET_Tests"),
                getTestWithResponseCode200("t_JaaS_GET_Session_Tests_List_SR_eq._1.0", queryProvider.GET_TestsList(), new TestsListResponseContentValidator()),
                getTestWithResponseCode200("t_JaaS_GET_Exact_Session_Test_SR_eq._1.0", queryProvider.GET_TestNames(), new TestResponseContentValidator())
        ).build();

        JParallelTestsGroup tg_JaaS_GET_Metrics = getSingleTestWithResponseCode200("t_JaaS_GET_Metrics_List_SR_eq._1.0",
                queryProvider.GET_TestMetrics(), new MetricsListResponseContentValidator());

        JParallelTestsGroup tg_JaaS_GET_Metric_Summaries = getSingleTestWithResponseCode200("t_JaaS_GET_Metric_Summary_List_SR_eq",
                queryProvider.GET_MetricSummary(), new SummaryListResponseContentValidator());

        JParallelTestsGroup tg_JaaS_MetricPlotList = getSingleTestWithResponseCode200("t_JaaS_GET_Metric_Plot_List_SR_eq._1.0",
                queryProvider.GET_MetricPlotData(), new PlotListResponseContentValidator());

        return Stream.of(
                tg_JaaS_GET_Sessions,
                tg_JaaS_GET_Tests,
                tg_JaaS_GET_Metrics,
                tg_JaaS_GET_Metric_Summaries,
                tg_JaaS_MetricPlotList
        );
    }

    private Stream<JParallelTestsGroup> executionsTests(QueryProvider queryProvider) {
        JParallelTestsGroup tg_JaaS_POST_execution = JParallelTestsGroup.builder(Id.of("tg_JaaS_POST_execution"),
                getExecutionLoadTest("t_JaaS_POST_execution", queryProvider.POST_execution(),
                        Arrays.asList(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class),
                                JHttpResponseStatusValidatorProvider.of(201), new CreateExecutionResponseValidator()),
                        JLoadProfileUserGroups.builder(JLoadProfileUsers.builder(NumberOfUsers.of(1)).build()).build(),
                        JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(20)))
        ).build();

        JParallelTestsGroup tg_JaaS_GET_executions = JParallelTestsGroup.builder(Id.of("tg_JaaS_GET_executions"),
                getTestWithResponseCode200("t_JaaS_GET_Executions_List", queryProvider.GET_ExList(), new ExListResponseValidator()),
                getTestWithResponseCode200("t_JaaS_GET_Exact_Execution", queryProvider.GET_ExId(), new ExResponseValidator()),
                getStandardTest("t_JaaS_GET_BadRequest", queryProvider.GET_NonNumeric_ExId(),
                        Arrays.asList(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class),
                                JHttpResponseStatusValidatorProvider.of(400), new BadRequest_ResponseContentValidator())),
                getStandardTest("t_JaaS_GET_NotFound", queryProvider.GET_NonExisting_ExId(),
                        Arrays.asList(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class),
                                JHttpResponseStatusValidatorProvider.of(404)))
        ).build();

        JParallelTestsGroup tg_JaaS_DELETE_Executions = JParallelTestsGroup.builder(Id.of("tg_JaaS_DELETE_Executions"),
                getExecutionLoadTest("t_JaaS_DELETE_Executions", queryProvider.DELETE_Execution(),
                        Arrays.asList(JHttpResponseStatusValidatorProvider.of(204), DefaultResponseValidatorProvider.of(NotNullResponseValidator.class)),
                        JLoadProfileUserGroups.builder(JLoadProfileUsers.builder(NumberOfUsers.of(1)).build()).build(),
                        JTerminationCriteriaIterations.of(IterationsNumber.of(300), MaxDurationInSeconds.of(15)))
        ).build();

        JParallelTestsGroup tg_JaaS_GET_deleted_execution = JParallelTestsGroup.builder(Id.of("tg_JaaS_GET_deleted_execution"),
                getStandardTest("t_JaaS_GET_deleted_execution", queryProvider.GET_Deleted_Ex(),
                        Arrays.asList(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class), JHttpResponseStatusValidatorProvider.of(404)))
        ).build();


        return Stream.of(
                tg_JaaS_POST_execution,
                tg_JaaS_GET_executions,
                tg_JaaS_DELETE_Executions,
                tg_JaaS_GET_deleted_execution
        );
    }

    @Bean
    public JLoadScenario getJaasTestScenario() {
        TestContext.initUri(this);

        QueryProvider queryProvider = new QueryProvider(this::getPropertyValue);
        return JLoadScenario.builder(Id.of("ts_JaaSTestSuit"), Stream.concat(
                sessionTests(queryProvider),
                executionsTests(queryProvider)).collect(Collectors.toList()))
                .withLatencyPercentiles(Collections.singletonList(99d))
                .addListener(new LoadScenarioConfigListener())
                .build();
    }


    private JParallelTestsGroup getSingleTestWithResponseCode200(String id, Iterable queryProvider, ResponseValidatorProvider validator) {
        return JParallelTestsGroup.builder(Id.of("g" + id), getTestWithResponseCode200(id, queryProvider, validator)).build();
    }

    private JLoadTest getTestWithResponseCode200(String id, Iterable queryProvider, ResponseValidatorProvider validator) {
        return getStandardTest(id, queryProvider,
                Arrays.asList(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class),
                        JHttpResponseStatusValidatorProvider.of(200), validator));
    }

    private JLoadTest getStandardTest(String id, Iterable queryProvider, List<ResponseValidatorProvider> validators) {
        JLoadProfile standardGroupLoad = JLoadProfileUserGroups
                .builder(JLoadProfileUsers.builder(NumberOfUsers.of(2))
                        .withLifeTimeInSeconds(Long.valueOf(getPropertyValue("jaas.std.tst.life")))
                        .build())
                .build();
        JTerminationCriteria standardTermination = JTerminationCriteriaIterations
                .of(IterationsNumber.of(Long.valueOf(getPropertyValue("jaas.std.tst.iterations"))),
                        MaxDurationInSeconds.of(Long.valueOf(getPropertyValue("jaas.std.tst.max_duration"))));

        return getCommonLoadTest(id, queryProvider, validators, standardGroupLoad, standardTermination);
    }

    private JLoadTest getCommonLoadTest(String id,
                                        Iterable queryProvider,
                                        List<ResponseValidatorProvider> validators,
                                        JLoadProfile standardGroupLoad,
                                        JTerminationCriteria standardTermination) {
        JTestDefinition definition = JTestDefinition.builder(Id.of(id + "def"),
                Collections.singletonList(new JHttpEndpoint(TestContext.getEndpointUri())))
                .withQueryProvider(queryProvider)
                .addValidators(validators)
                .build();

        return JLoadTest.builder(Id.of(id), definition, standardGroupLoad, standardTermination)
                .withLimits(JLimitVsRefValue.builder("successRate-Success rate", RefValue.of(1.0)).build())
                .build();
    }

    private JLoadTest getExecutionLoadTest(String id, Iterable queryProvider, List<ResponseValidatorProvider> validators, JLoadProfile standardGroupLoad, JTerminationCriteria standardTermination) {
        JTestDefinition definition = JTestDefinition.builder(Id.of(id + "def"),
                Collections.singletonList(new JHttpEndpoint(TestContext.getEndpointUri())))
                .withQueryProvider(queryProvider)
                .withInvoker(DefaultInvokerProvider.of(ExecutionManipulateInvoker.class))
                .addValidators(validators)
                .build();

        return JLoadTest.builder(Id.of(id), definition, standardGroupLoad, standardTermination)
                .withLimits(JLimitVsRefValue.builder("successRate-Success rate", RefValue.of(1.0)).build())
                .build();
    }


}
