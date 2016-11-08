package com.griddynamics.jagger.jaas.exceptions;

public class InvalidJobException extends RuntimeException {

    public InvalidJobException(Exception cause) {
        super(cause);
    }

    public InvalidJobException(String message) {
        super(message);
    }
}
