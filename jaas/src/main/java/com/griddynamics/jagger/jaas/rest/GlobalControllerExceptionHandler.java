package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import org.hibernate.StaleStateException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles exceptional situations common for all rest controllers.
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    
    /**
     * Catches an {@link org.hibernate.StaleStateException} exception which occurs if we try delete or update a row that
     * does not exist.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StaleStateException.class)
    public void noDataFound() {
    }
    
    /**
     * Catches an {@link com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException} exception which occurs
     * once requested resource not found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> resourceNotFound(ResourceNotFoundException rnfe) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                String.format("{\"reason\": \"%s\"}", rnfe.getMessage()), httpHeaders, HttpStatus.NOT_FOUND);
    }
}
