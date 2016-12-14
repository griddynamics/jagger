package com.griddynamics.jagger.jaas.rest.error;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private String errorMessage;

    private HttpStatus httpStatus;

    private ErrorResponse(String errorMessage, HttpStatus httpStatus) {
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public static ErrorResponse errorResponse(String errorMessage, HttpStatus httpStatus) {
        return new ErrorResponse(errorMessage, httpStatus);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
