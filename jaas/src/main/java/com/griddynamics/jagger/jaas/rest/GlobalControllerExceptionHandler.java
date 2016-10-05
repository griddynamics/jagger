package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import org.hibernate.StaleStateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handles exceptional situations common for all rest controllers.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {
    
    /**
     * Catches an {@link org.hibernate.StaleStateException} exception which occurs if we try delete or update a row that
     * does not exist.
     * Catches an {@link com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException} exception which occurs
     * once requested resource not found.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({StaleStateException.class, ResourceNotFoundException.class})
    void noDataFound() {
    }
}
