package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;

import java.util.List;

public interface TestExecutionService {
    TestExecutionEntity read(Long id);

    List<TestExecutionEntity> readAll();

    List<TestExecutionEntity> readAllPending();

    TestExecutionEntity create(TestExecutionEntity testExecution);

    void delete(Long testExecutionId);

    void finishExecution(String environmentId, String loadScenarioId);

    void startExecution(String environmentId, String testSuiteId);
}
