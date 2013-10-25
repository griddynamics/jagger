package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadExecutionStatus;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/11/13
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadCalculator implements MetricCalculator<WorkloadExecutionStatus> {
    @Override
    public Integer calculate(WorkloadExecutionStatus status) {
        return new Integer(status.getTotalThreads());
    }
}
