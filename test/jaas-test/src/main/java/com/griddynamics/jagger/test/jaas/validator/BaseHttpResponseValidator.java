package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseHttpResponseValidator<Q, E> extends ResponseValidator<Q, E, JHttpResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHttpResponseValidator.class);

    public BaseHttpResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "BaseHttpResponseValidator";
    }

    @Override
    public abstract boolean validate(Q query, E endpoint, JHttpResponse result, long duration);

    protected void logResponseAsFailed(E endpoint, JHttpResponse response){
        LOGGER.warn(String.format
                ("------> Failed response:\nEndpoint=%s \nStatus=%s \nBody=%s ",
                        endpoint.toString(), response.getStatus().toString(), response.getBody()));
    }
}