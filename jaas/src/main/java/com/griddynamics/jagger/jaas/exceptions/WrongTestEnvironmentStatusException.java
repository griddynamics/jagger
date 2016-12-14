package com.griddynamics.jagger.jaas.exceptions;

import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import com.griddynamics.jagger.jaas.storage.model.LoadScenarioEntity;

import static java.lang.String.format;

public class WrongTestEnvironmentStatusException extends RuntimeException {
    public WrongTestEnvironmentStatusException(TestEnvironmentEntity.TestEnvironmentStatus status, LoadScenarioEntity runningLoadScenario) {
        super(format("Status [%s] doesn't correspond to runningLoadScenario [%s].", status, runningLoadScenario));
    }
}
