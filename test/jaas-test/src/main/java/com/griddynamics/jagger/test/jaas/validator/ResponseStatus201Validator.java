package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;

public class ResponseStatus201Validator extends ResponseStatusValidator {

    public ResponseStatus201Validator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "ResponseStatus201Validator";
    }

    protected int getExpectedStatusCode() {
        return 201;
    }
}