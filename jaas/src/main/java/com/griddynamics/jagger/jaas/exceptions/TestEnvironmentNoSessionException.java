package com.griddynamics.jagger.jaas.exceptions;

public class TestEnvironmentNoSessionException extends RuntimeException {

    public TestEnvironmentNoSessionException() {
        super("Environment-Session cookie is not present.");
    }
}
