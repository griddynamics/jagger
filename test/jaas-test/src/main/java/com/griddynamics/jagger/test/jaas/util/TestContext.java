package com.griddynamics.jagger.test.jaas.util;

import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;

import java.util.*;

/**
 * Created by ELozovan on 2016-09-28.
 */
public class TestContext {
    private static volatile TestContext instance;

    private Set<SessionEntity> sessions = new TreeSet<>();

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
     * Returns NULL is no session entity found.
     *
     */
    public static SessionEntity getSession(String id) {
        return get().sessions.stream().filter((s)->id.equals(s.getId())).findFirst().orElse(null);
    }

    public static void setSessions(Set<SessionEntity> sessions) {
        get().sessions = sessions;
    }
}