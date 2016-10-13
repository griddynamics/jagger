package com.griddynamics.jagger.jaas.exceptions;

/**
 * Supposed to be thrown in case requested REST resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName) {
        super(String.format("%s resource with provided id not found.", resourceName));
    }
}
