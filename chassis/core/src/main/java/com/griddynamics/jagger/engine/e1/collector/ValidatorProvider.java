package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 8/15/13
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidatorProvider implements KernelSideObjectProvider<Validator>{

    private KernelSideObjectProvider<ResponseValidator<Object, Object, Object>> validatorProvider;
    private String displayName;

    public Validator provide(String sessionId, String taskId, NodeContext kernelContext){
        return new Validator(taskId, sessionId, kernelContext, validatorProvider.provide(sessionId, taskId, kernelContext), displayName);
    }

    public void setValidator(KernelSideObjectProvider<ResponseValidator<Object, Object, Object>> validatorProvider) {
        this.validatorProvider = validatorProvider;
    }

    public KernelSideObjectProvider<ResponseValidator<Object, Object, Object>> getValidatorProvider() {
        return validatorProvider;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
