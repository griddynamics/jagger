package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;

public class ResponseStatus202Validator extends ResponseStatusValidator {

    public ResponseStatus202Validator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "ResponseStatus202Validator";
    }

    protected int getExpectedStatusCode(){ return 202; }
}