package com.griddynamics.jagger.test.javabuilders.smoke_components;

import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.coordinator.NodeContext;

/**
 * Validator to test definitions with custom validator. Always valid.
 */
public class TrueValidator extends ResponseValidator<Object, Object, Object> {

    public TrueValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "TrueValidator";
    }

    @Override
    public boolean validate(Object query, Object endpoint, Object result, long duration) {
        return true;
    }
}
