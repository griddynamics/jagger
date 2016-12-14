package com.griddynamics.jagger.test.jaas.invoker;


import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Invoker without checking result codes.
 * I.e. if request returns 4xx or 5xx this invoker doesn't throw exception unlike DefaultHttpInvoker
 */
public class InvokerWithoutStatusCodeValidation extends DefaultHttpInvoker {
    public InvokerWithoutStatusCodeValidation() {
        super();
        Map<String, Object> params = new HashMap<>();
        params.put(SpringBasedHttpClient.JSpringBasedHttpClientParameters.ERROR_HANDLER.getValue(), new ResponseHandler());
        this.httpClient = new SpringBasedHttpClient(params);
    }
}
