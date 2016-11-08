package com.griddynamics.jagger.test.jaas.util;

import com.griddynamics.jagger.engine.e1.services.data.service.*;
import com.griddynamics.jagger.test.jaas.util.entity.DbConfigEntity;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Stores and provides access to test context (expected data mainly).
 * Created by ELozovan on 2016-09-28.
 */
@SuppressWarnings("unused")
public class TestContext {
    private static volatile TestContext instance;

    private Set<SessionEntity> sessions = new TreeSet<>();
    private Map<String, Set<TestEntity>> tests = new HashMap<>();
    /**
     * Key:SessionId, Value:[Key:TestName, Value:Set of Metrics]
     */
    private Map<String, Map<String, Set<MetricEntity>>> metrics = new HashMap<>();

    private Map<MetricEntity, MetricSummaryValueEntity> metricSummaries = new HashMap<>();
    private Map<MetricEntity, List<MetricPlotPointEntity>> metricPlotData = new HashMap<>();

    /**
     * To be loaded and used in Read tests for /jaas/dbs resource.
     */
    private Set<DbConfigEntity> dbConfigs = new TreeSet<>();

    /**
     * A prototype, to be generated and used in Create/Update/Delete tests for /jaas/dbs resource.
     */
    private DbConfigEntity dbConfigPrototype;

    /**
     * DB Config Ids (Strings) which were created during test run.
     */
    private List<String> createdDbConfigIds = new ArrayList<>();

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

    /**
     * Returns NULL if no session entity found.
     */
    public static SessionEntity getSession(String id) {
        return get().sessions.stream().filter((s) -> id.equals(s.getId())).findFirst().orElse(null);
    }

    public static void setSessions(Set<SessionEntity> sessions) {
        get().sessions = sessions;
    }

    public static Map<String, Set<TestEntity>> getTests() {
        return get().tests;
    }

    public static Set<TestEntity> getTestsBySessionId(String sessionId) {
        return get().tests.get(sessionId);
    }

    public static TestEntity getTestByName(String sessionId, String testName) {
        return getTestsBySessionId(sessionId).stream().filter((t) -> t.getName().equals(testName)).findFirst().orElse(null);
    }

    public static void setTests(Map<String, Set<TestEntity>> tests) {
        get().tests = tests;
    }

    public static void addTests(String sessionId, Set<TestEntity> sessionTests) {
        get().tests.put(sessionId, sessionTests);
    }

    public static Map<String, Map<String, Set<MetricEntity>>> getMetrics() {
        return get().metrics;
    }

    public static Set<MetricEntity> getMetricsBySessionIdAndTestName(String sessionId, String testName) {
        return get().metrics.get(sessionId).get(testName);
    }

    public static void setMetrics(Map<String, Map<String, Set<MetricEntity>>> metrics) {
        get().metrics = metrics;
    }

    public static void addMetrics(String sessionId, String testName, Set<MetricEntity> metrics) {
        Map<String, Set<MetricEntity>> tmp = new HashMap<>();
        tmp.put(testName, metrics);

        get().metrics.put(sessionId, tmp);
    }

    public static Map<MetricEntity, MetricSummaryValueEntity> getMetricSummaries() {
        return  get().metricSummaries;
    }

    public static void setMetricSummaries(Map<MetricEntity, MetricSummaryValueEntity> metricSummaries) {
        get().metricSummaries = metricSummaries;
    }

    public static Map<MetricEntity, List<MetricPlotPointEntity>> getMetricPlotData() {
        return  get().metricPlotData;
    }

    public static void setMetricPlotData(Map<MetricEntity, List<MetricPlotPointEntity>> metricPlotData) {
        get().metricPlotData = metricPlotData;
    }

    /**
     * Returns set of expected DB configs (for testing of GET /jaas/dbs).
     */
    public static Set<DbConfigEntity> getDbConfigs() {
        return get().dbConfigs;
    }

    /**
     * Returns NULL if no matching DB config entity found.
     */
    public static DbConfigEntity getDbConfig(Long id) {
        return get().dbConfigs.stream().filter((c) -> id.equals(c.getId())).findFirst().orElse(null);
    }

    public static void setDbConfigs(Set<DbConfigEntity> dbConfigs) {
        get().dbConfigs = dbConfigs;
    }

    public static void addDbConfig(DbConfigEntity dbConfig) {
        get().dbConfigs.add(dbConfig);
    }

    public static DbConfigEntity getDbConfigPrototype() {
        if (null == get().dbConfigPrototype) {
            generateDbConfigPrototype();
        }
        return get().dbConfigPrototype;
    }

    private static void setDbConfigPrototype(DbConfigEntity dbConfigToCreate) {
        get().dbConfigPrototype = dbConfigToCreate;
    }

    public static List<String> getCreatedDbConfigIds() {
        return get().createdDbConfigIds;
    }

    public static void setCreatedDbConfigIds(List<String> createdDbConfigIds) {
        get().createdDbConfigIds = createdDbConfigIds;
    }

    public static void addCreatedDbConfigId(String createdDbConfigId) {
        get().createdDbConfigIds.add(createdDbConfigId);
    }

    public static DbConfigEntity provideFakeDbConfig_NoId() {
        DbConfigEntity dbConf = new DbConfigEntity();
        dbConf.setUser("Proto-" + UUID.randomUUID().toString());
        dbConf.setPass("[TO DELETE]" + UUID.randomUUID().toString());
        dbConf.setDesc("Timestamp: " + LocalDateTime.now().toString());
        dbConf.setHibernateDialect(UUID.randomUUID().toString());
        dbConf.setJdbcDriver(UUID.randomUUID().toString());
        dbConf.setUrl("jdbc:" + UUID.randomUUID().toString());

        return dbConf;
    }

    private static void generateDbConfigPrototype() {
        TestContext.setDbConfigPrototype(provideFakeDbConfig_NoId());
    }
}