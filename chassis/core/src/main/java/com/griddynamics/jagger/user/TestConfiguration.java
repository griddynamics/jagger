package com.griddynamics.jagger.user;

import com.griddynamics.jagger.engine.e1.scenario.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: evelina
 * Date: 2/16/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestConfiguration {

    private WorkloadClockConfiguration clockConfiguration;
    private String id;
    private TerminateStrategyConfiguration terminateStrategyConfiguration;
    private WorkloadTask prototype;
    private int number;
    private String testGroupName;

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTestGroupName(String testGroupName) {
        this.testGroupName = testGroupName;
    }

    public WorkloadTask getPrototype() {
        return prototype;
    }

    public void setPrototype(WorkloadTask prototype) {
        this.prototype = prototype;
    }

    public void setTestDescription(WorkloadTask prototype) {
        this.prototype = prototype;
    }

    public WorkloadClockConfiguration getClockConfiguration() {
        return clockConfiguration;
    }

    public void setLoad(WorkloadClockConfiguration clockConfiguration) {
        this.clockConfiguration = clockConfiguration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String id) {
        this.id = id;
    }

    public TerminateStrategyConfiguration getTerminateStrategyConfiguration() {
        return terminateStrategyConfiguration;
    }

    public void setTerminateStrategy(TerminateStrategyConfiguration terminateStrategyConfiguration) {
        this.terminateStrategyConfiguration = terminateStrategyConfiguration;
    }

    public String getName() {
        return String.format("%s [%s]", testGroupName, id);
    }

    public boolean isAttendant() {
        //TODO
        return terminateStrategyConfiguration instanceof InfiniteTerminationStrategyConfiguration;
    }

    public WorkloadTask generate(AtomicBoolean shutdown) {
        WorkloadTask task = prototype.copy();
        task.setName(getName());
        task.setNumber(number);
        task.setParentTaskId(testGroupName);
        //TODO refactor
        if (clockConfiguration instanceof UserGroupsClockConfiguration) {
            ((UserGroupsClockConfiguration) clockConfiguration).setShutdown(shutdown);
        }
        task.setClockConfiguration(clockConfiguration);
        if (terminateStrategyConfiguration instanceof IterationsOrDurationStrategyConfiguration) {
            ((IterationsOrDurationStrategyConfiguration)terminateStrategyConfiguration).setShutdown(shutdown);
        }
        task.setTerminateStrategyConfiguration(terminateStrategyConfiguration);
        return task;
    }
}
