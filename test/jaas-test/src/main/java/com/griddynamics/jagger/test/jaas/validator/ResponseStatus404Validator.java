package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;

public class ResponseStatus404Validator extends ResponseStatusValidator {

    public ResponseStatus404Validator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "ResponseStatus404Validator";
    }

    protected int getExpectedStatusCode() {
        return 404;
    }
}