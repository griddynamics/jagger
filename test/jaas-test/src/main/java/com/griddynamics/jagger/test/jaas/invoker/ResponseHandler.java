package com.griddynamics.jagger.test.jaas.invoker;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * This response handler suppress checking of response codes
 */
public class ResponseHandler extends DefaultResponseErrorHandler {

    protected boolean hasError(HttpStatus statusCode) {
        return false;
    }

}
