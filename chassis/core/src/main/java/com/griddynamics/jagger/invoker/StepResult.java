package com.griddynamics.jagger.invoker;

/**
 * Created by kgribov on 2/18/14.
 */
public class StepResult<Q, R, E> {
    private R result;
    private StepInfo<Q, E> stepInfo;

    public StepResult(StepInfo<Q, E> stepInfo, R result) {
        this.result = result;
        this.stepInfo = stepInfo;
    }

    public R getResult() {
        return result;
    }

    public StepInfo getStepInfo() {
        return stepInfo;
    }
}
