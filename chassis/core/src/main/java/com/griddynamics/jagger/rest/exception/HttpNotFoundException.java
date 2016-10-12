package com.griddynamics.jagger.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * To be thrown to generate HTTP content not found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class HttpNotFoundException extends RuntimeException {
    
    private static final HttpNotFoundException INSTANCE = new HttpNotFoundException();
    
    public static HttpNotFoundException getInstance() {
        return INSTANCE;
    }
    
    
    public HttpNotFoundException() {
    }
    
    public HttpNotFoundException(String message) {
        super(message);
    }
    
    public HttpNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public HttpNotFoundException(Throwable cause) {
        super(cause);
    }
    
    public HttpNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
