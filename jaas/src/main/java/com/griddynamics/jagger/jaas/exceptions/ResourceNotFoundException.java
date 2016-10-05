package com.griddynamics.jagger.jaas.exceptions;

/**
 * Supposed to be handled by Spring runtime to produce corresponding response.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException() {
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
    
    public ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
