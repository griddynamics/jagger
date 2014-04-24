package com.griddynamics.jagger.invoker;

/**
 * Created by kgribov on 2/21/14.
 */
public interface InvokerStep {
    StepResult execute(StepInfo stepInfo);
    String getName();
}
