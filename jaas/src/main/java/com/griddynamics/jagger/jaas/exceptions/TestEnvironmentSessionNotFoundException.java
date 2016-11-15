package com.griddynamics.jagger.jaas.exceptions;

import static java.lang.String.format;

public class TestEnvironmentSessionNotFoundException extends RuntimeException {

    public TestEnvironmentSessionNotFoundException(String envId, String sessionId) {
        super(format("Test environment [%s] with sessionId=%s is not found.", envId, sessionId));
    }
}
