package com.griddynamics.jagger.jaas.exceptions;

import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import com.griddynamics.jagger.jaas.storage.model.TestSuiteEntity;

import static java.lang.String.format;

public class WrongTestEnvironmentStatusException extends RuntimeException {
    public WrongTestEnvironmentStatusException(TestEnvironmentEntity.TestEnvironmentStatus status, TestSuiteEntity runningTestSuite) {
        super(format("Status [%s] doesn't correspond to runningTestSuite [%s].", status, runningTestSuite));
    }
}
