package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;

import java.util.List;

public interface TestExecutionDao extends CrudDao<TestExecutionEntity, Long> {
    List<TestExecutionEntity> readByEnvAndLoadScenario(String envId, String loadScenarioId);

    List<TestExecutionEntity> readByEnv(String envId);

    List<TestExecutionEntity> readAllPending();
}