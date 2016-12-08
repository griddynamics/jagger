package com.griddynamics.jagger.jaas.exceptions;

public class InvalidTestExecutionException extends RuntimeException {
    public InvalidTestExecutionException(String message) {
        super(message);
    }

    public InvalidTestExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
