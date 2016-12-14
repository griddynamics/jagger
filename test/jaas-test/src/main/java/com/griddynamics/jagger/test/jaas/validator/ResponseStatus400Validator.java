package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;

public class ResponseStatus400Validator extends ResponseStatusValidator {

    public ResponseStatus400Validator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "ResponseStatus400Validator";
    }

    protected int getExpectedStatusCode() {
        return 400;
    }
}