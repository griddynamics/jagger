package com.griddynamics.jagger.test.jaas.util;

import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;

import java.util.*;

/**
 * Stores and provides access to test context (expected data mainly).
 * Created by ELozovan on 2016-09-28.
 */
public class TestContext {
    private static volatile TestContext instance;

    private Set<SessionEntity> sessions = new TreeSet<>();
    private Map<String,Set<TestEntity>> tests = new HashMap<>();

    private TestContext() {}

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
     *
     */
    public static SessionEntity getSession(String id) {
        return get().sessions.stream().filter((s)->id.equals(s.getId())).findFirst().orElse(null);
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
        return getTestsBySessionId(sessionId).stream().filter((t)->t.getName().equals(testName)).findFirst().orElse(null);
    }

    public static void setTests(Map<String, Set<TestEntity>> tests) {
        get().tests = tests;
    }

    public static void addTests(String sessionId, Set<TestEntity> sessionTests) {
        get().tests.put(sessionId, sessionTests);
    }
}