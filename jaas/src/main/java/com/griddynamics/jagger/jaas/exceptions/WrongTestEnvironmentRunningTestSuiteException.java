package com.griddynamics.jagger.jaas.exceptions;

public class WrongTestEnvironmentRunningTestSuiteException extends RuntimeException {

    public WrongTestEnvironmentRunningTestSuiteException(String message) {
        super(message);
    }
}
