package com.griddynamics.jagger.test.jaas.util;

import com.griddynamics.jagger.engine.e1.services.data.service.*;
import com.griddynamics.jagger.test.jaas.util.entity.ExecutionEntity;

import java.util.*;

/**
 * Stores and provides access to test context (expected data mainly).
 * Created by ELozovan on 2016-09-28.
 */
public class TestContext {
    private static volatile TestContext instance;
    private static String sessionsUri;
    private static String testsUri;
    private static String executionsUri;
    private static String endpointUri;

    private Set<SessionEntity> sessions = new TreeSet<>();
    private Map<String, Set<TestEntity>> tests = new HashMap<>();
    /**
     * Key:SessionId, Value:[Key:TestName, Value:Set of Metrics]
     */
    private Map<String, Map<String, Set<MetricEntity>>> metrics = new HashMap<>();

    private Map<MetricEntity, MetricSummaryValueEntity> metricSummaries = new HashMap<>();
    private Map<MetricEntity, List<MetricPlotPointEntity>> metricPlotData = new HashMap<>();


    /**
     * Execution Ids which were created during test run.
     */
    private Set<Long> createdExecutionsIds = new HashSet<>();
    private Long firstRemovedExecution;

    private TestContext() {
    }

    public static TestContext get() {
        TestContext localInstance = instance;
        if (localInstance == null) {
            synchronized (TestContext.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TestContext();
                }
            }
        }
        return localInstance;
    }

    public static Set<SessionEntity> getSessions() {
        return get().sessions;
    }

    public static void setSessions(Set<SessionEntity> sessions) {
        get().sessions = sessions;
    }

    /**
     * Returns NULL if no session entity found.
     */
    public static SessionEntity getSession(String id) {
        return get().sessions.stream().filter((s) -> id.equals(s.getId())).findFirst().orElse(null);
    }

    public static Map<String, Set<TestEntity>> getTests() {
        return get().tests;
    }

    public static void setTests(Map<String, Set<TestEntity>> tests) {
        get().tests = tests;
    }

    public static Set<TestEntity> getTestsBySessionId(String sessionId) {
        return get().tests.get(sessionId);
    }

    public static TestEntity getTestByName(String sessionId, String testName) {
        return getTestsBySessionId(sessionId).stream().filter((t) -> t.getName().equals(testName)).findFirst().orElse(null);
    }

    public static void addTests(String sessionId, Set<TestEntity> sessionTests) {
        get().tests.put(sessionId, sessionTests);
    }

    public static Map<String, Map<String, Set<MetricEntity>>> getMetrics() {
        return get().metrics;
    }

    public static void setMetrics(Map<String, Map<String, Set<MetricEntity>>> metrics) {
        get().metrics = metrics;
    }

    public static Set<MetricEntity> getMetricsBySessionIdAndTestName(String sessionId, String testName) {
        return get().metrics.get(sessionId).get(testName);
    }

    public static void addMetrics(String sessionId, String testName, Set<MetricEntity> metrics) {
        Map<String, Set<MetricEntity>> tmp = new HashMap<>();
        tmp.put(testName, metrics);

        get().metrics.put(sessionId, tmp);
    }

    public static Map<MetricEntity, MetricSummaryValueEntity> getMetricSummaries() {
        return get().metricSummaries;
    }

    public static void setMetricSummaries(Map<MetricEntity, MetricSummaryValueEntity> metricSummaries) {
        get().metricSummaries = metricSummaries;
    }

    public static Map<MetricEntity, List<MetricPlotPointEntity>> getMetricPlotData() {
        return get().metricPlotData;
    }

    public static void setMetricPlotData(Map<MetricEntity, List<MetricPlotPointEntity>> metricPlotData) {
        get().metricPlotData = metricPlotData;
    }


    public static Set<Long> getCreatedExecutionIds() {
        return get().createdExecutionsIds;
    }

    public static void addCreatedExecutionId(Long createdExecutionId) {
        get().createdExecutionsIds.add(createdExecutionId);
    }

    public static String getExecutionConfigPrototype() {
        return ExecutionEntity.getDefault().toJson();
    }

    public static Long getFirstRemovedExecution() {
        return get().firstRemovedExecution;
    }

    public static void setFirstRemovedExecution(Long firstRemovedExecution) {
        if (get().firstRemovedExecution == null) {
            get().firstRemovedExecution = firstRemovedExecution;
        }
    }

    public static void initUri(JaggerPropertiesProvider provider) {
        sessionsUri = provider.getPropertyValue("jaas.rest.base.sessions");
        testsUri = provider.getPropertyValue("jaas.rest.sub.sessions.tests");
        executionsUri = provider.getPropertyValue("jaas.rest.executions");
        endpointUri = provider.getPropertyValue("jaas.endpoint");
    }

    public static String getSessionsUri() {
        return sessionsUri;
    }

    public static String getTestsUri() {
        return testsUri;
    }

    public static String getExecutionsUri() {
        return executionsUri;
    }

    public static String getEndpointUri() {
        return endpointUri;
    }
}