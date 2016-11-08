package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.http.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.HttpRelated;
import junit.framework.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertTrue;

public abstract class BaseHttpResponseValidator extends ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> implements HttpRelated {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHttpResponseValidator.class);

    public BaseHttpResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "BaseHttpResponseValidator";
    }

    @Override
    public boolean validate(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result, long duration){
        boolean isValid;

        try {
            assertTrue("The response is not valid.", isValid(query, endpoint, result));
            isValid = true;
        } catch (AssertionFailedError e) {
            isValid = false;
            logResponseAsFailed(query, endpoint, result, e.getMessage());
        }

        return isValid;
    }

    protected void logResponseAsFailed(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse response, String message){
        LOGGER.warn("{}'s query response content is not valid, due to [{}].", query.toString(), message);
        LOGGER.warn(String.format
                ("------> Failed response:\n%s \n%s",
                        endpoint.toString(), response.toString()));
    }

    /**
     * Child classes shall provide own implementation of the method.
     * That allows to simplify functional checks.
     */
    protected abstract boolean isValid(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result);
}