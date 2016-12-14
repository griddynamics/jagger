package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.util.entity.ExecutionEntity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;


public class QueryProvider {
    private final String sessions_uri;
    private final String tests_uri;
    private String HDR_CONTENT_TYPE = "Content-Type";
    private String HDR_CONTENT_TYPE_VALUE_APP_JSON = "application/json";
    private Function<String, String> getPropertyValue;
    private String executions_uri;

    public QueryProvider(Function<String, String> getPropertyValue) {
        this.getPropertyValue = getPropertyValue;
        sessions_uri = TestContext.getSessionsUri();
        tests_uri = TestContext.getTestsUri();
        executions_uri = TestContext.getExecutionsUri();
    }

    private String getValue(String key) {
        return getPropertyValue.apply(key);
    }

    public Iterable GET_SessionsList() {
        return () -> Collections.singletonList(new JHttpQuery<String>().get().responseBodyType(SessionEntity[].class).path(sessions_uri)).iterator();
    }

    public Iterable GET_SessionIds() {
        return () -> TestContext
                .getSessions()
                .stream()
                .map(s -> new JHttpQuery<String>()
                        .get().responseBodyType(SessionEntity.class).path(sessions_uri + "/" + s.getId()))
                .iterator();
    }

    public Iterable GET_TestsList() {
        return () -> Collections.singletonList(new JHttpQuery<String>()
                .get().responseBodyType(TestEntity[].class)
                .path(sessions_uri + "/" + getSessionId() + tests_uri))
                .iterator();
    }

    public Iterable GET_TestNames() {
        return () -> TestContext.getTestsBySessionId(getSessionId())
                .stream().map(t -> new JHttpQuery<String>()
                        .get().responseBodyType(TestEntity.class)
                        .path(sessions_uri + "/" + getSessionId() + tests_uri + "/" + t.getName()))
                .iterator();
    }

    public Iterable GET_TestMetrics() {
        return () -> Collections.singletonList(new JHttpQuery<String>()
                .get().responseBodyType(MetricEntity[].class)
                .path(getMetricPath())).iterator();
    }

    public Iterable GET_MetricSummary() {
        return () -> Collections.singletonList(new JHttpQuery<String>()
                .get().responseBodyType(Map.class)
                .path(getMetricPath() + getValue("jaas.rest.sub.tests.metrics_summary")))
                .iterator();
    }

    public Iterable GET_MetricPlotData() {
        return () -> Collections.singletonList(new JHttpQuery<String>()
                .get().responseBodyType(Map.class)
                .path(getMetricPath() + getValue("jaas.rest.sub.tests.metrics_plot_data")))
                .iterator();
    }

    private String getMetricPath() {
        return sessions_uri + "/" + getSessionId() + tests_uri + "/"
                + TestContext.getMetrics().get(getSessionId()).keySet().toArray(new String[]{})[0]
                + getValue("jaas.rest.sub.tests.metrics");
    }

    private String getSessionId() {
        return (TestContext.getTests().keySet().toArray(new String[]{}))[0];
    }

    public Iterable POST_execution() {
        return () -> Collections.singletonList(new JHttpQuery<String>()
                .post()
                .header(HDR_CONTENT_TYPE, HDR_CONTENT_TYPE_VALUE_APP_JSON)
                .body(TestContext.getExecutionConfigPrototype())
                .responseBodyType(ExecutionEntity.class)
                .path(executions_uri))
                .iterator();
    }

    public Iterable GET_ExList() {
        return () -> Collections.singletonList(new JHttpQuery<String>()
                .get().responseBodyType(ExecutionEntity[].class).path(executions_uri))
                .iterator();
    }

    public Iterable GET_ExId() {
        return () -> TestContext.getCreatedExecutionIds().stream()
                .map(id -> new JHttpQuery<String>()
                        .get().responseBodyType(ExecutionEntity.class)
                        .path(executions_uri + "/" + id))
                .iterator();
    }

    public Iterable GET_NonNumeric_ExId() {
        return () -> Stream.of("/abvgdeyka", "/ABVGD")
                .map(q -> new JHttpQuery<String>()
                        .get().responseBodyType(String.class).path(executions_uri + q))
                .iterator();
    }

    public Iterable GET_NonExisting_ExId() {
        return () -> Stream.of(Integer.MAX_VALUE, Integer.MIN_VALUE)
                .map(q -> new JHttpQuery<String>()
                        .get().responseBodyType(String.class).path(executions_uri + "/" + q))
                .iterator();
    }

    public Iterable DELETE_Execution() {
        return () -> TestContext.getCreatedExecutionIds().stream()
                .map(id -> new JHttpQuery<String>()
                        .delete().path(executions_uri + "/" + id))
                .iterator();
    }

    public Iterable GET_Deleted_Ex() {
        return () -> Collections.singletonList(new JHttpQuery<String>()
                .get().path(executions_uri + "/" + TestContext.getFirstRemovedExecution()))
                .iterator();
    }
}
