package com.griddynamics.jagger.engine.e1.collector.test;

import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.master.configuration.Task;

/**
 * Extension for {@link AbstractTestInfo}
 * based on {@link com.griddynamics.jagger.master.configuration.Task} instance composition.
 */
public abstract class TaskBasedTestInfo<T extends Task> extends AbstractTestInfo {
    
    private T test;
    
    public TaskBasedTestInfo(T test) {
        this.test = test;
    }
    
    public T getTest() {
        return test;
    }
    
    @Override
    public String getSessionId() {
        return test.getSessionId();
    }
    
    public void setStartTime() {
        test.getStartDate();
    }
    
    @Override
    public Long getStartTime() {
        return test.getStartDate();
    }
    
    public void setEndTime() {
        test.setEndDate();
    }
    
    @Override
    public Long getEndTime() {
        return test.getEndDate();
    }
    
    public void setStatus(TaskData.ExecutionStatus status) {
        test.setStatus(status);
    }
    
    public TaskData.ExecutionStatus getStatus() {
        return test.getStatus();
    }
    
    @Override
    public void onStart(AbstractTestInfo testInfo) {
        if (testInfo == this) {
            setStartTime();
            setStatus(TaskData.ExecutionStatus.IN_PROGRESS);
        }
    }
    
    @Override
    public void onStop(AbstractTestInfo testInfo) {
        if (testInfo == this) {
            doOnStop();
            setStatus(TaskData.ExecutionStatus.SUCCEEDED);
        }
    }
    
    @Override
    public void onFailure(AbstractTestInfo testInfo) {
        if (testInfo == this) {
            doOnStop();
            setStatus(TaskData.ExecutionStatus.FAILED);
        }
    }
    
    protected void doOnStop() {
        this.setEndTime();
    }
}
