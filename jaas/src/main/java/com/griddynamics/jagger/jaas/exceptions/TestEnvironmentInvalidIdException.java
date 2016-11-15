package com.griddynamics.jagger.jaas.exceptions;

import java.util.regex.Pattern;

import static java.lang.String.format;

public class TestEnvironmentInvalidIdException extends RuntimeException {
    public TestEnvironmentInvalidIdException(String envId, Pattern envIdPattern) {
        super(format("Test environment id [%s] doesn't match pattern [%s]", envId, envIdPattern.pattern()));
    }
}
