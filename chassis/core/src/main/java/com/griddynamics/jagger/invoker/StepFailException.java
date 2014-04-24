package com.griddynamics.jagger.invoker;

/**
 * Created by kgribov on 2/18/14.
 */
public class StepFailException extends InvocationException {
    private String stepName;

    public StepFailException(String stepName) {
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }

    @Override
    public String toString() {
        return "StepFailException{" +
                "stepName='" + stepName + '\'' +
                '}';
    }
}
