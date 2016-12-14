package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import junit.framework.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertTrue;

public abstract class BaseHttpResponseValidator<T> extends ResponseValidator<JHttpQuery<String>, JHttpEndpoint, JHttpResponse<T>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHttpResponseValidator.class);

    public BaseHttpResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "BaseHttpResponseValidator";
    }

    @Override
    public boolean validate(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<T> result, long duration) {
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

    protected void logResponseAsFailed(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse response, String message) {
        LOGGER.warn("{}'s query response content is not valid, due to [{}].", query.toString(), message);
        LOGGER.warn(String.format
                ("------> Failed response:\n%s \n%s",
                        endpoint.toString(), response.toString()));
    }

    /**
     * Child classes shall provide own implementation of the method.
     * That allows to simplify functional checks.
     */
    protected abstract boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<T> result);
}