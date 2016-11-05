package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;

import java.util.List;

public interface TestEnvironmentService {
    TestEnvironmentEntity read(String envId);

    List<TestEnvironmentEntity> readAll();

    TestEnvironmentEntity create(TestEnvironmentEntity testEnvironment);

    TestEnvironmentEntity update(TestEnvironmentEntity testEnvironment);

    void delete(String envId);

    boolean exists(String envId);

    boolean existsWithSessionId(String envId, String sessionId);
}
