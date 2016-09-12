package com.griddynamics.jagger.user;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.limits.LimitSet;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.engine.e1.scenario.*;
import com.griddynamics.jagger.master.SessionInfoProvider;
import com.griddynamics.jagger.master.TaskIdProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: evelina
 * Date: 2/16/13
 * Time: 7:18 PM
 */
public class TestConfiguration {
    
    @Autowired
    private SessionInfoProvider sessionInfoProvider;
    
    @Autowired
    private TaskIdProvider taskIdProvider;

    private WorkloadClockConfiguration clockConfiguration;
    private String id;
    private TerminateStrategyConfiguration terminateStrategyConfiguration;
    private int groupNumber;
    private String groupName;
    private String parentTaskId;
    private long startDelay = -1;
    private List<Provider<TestListener>> listeners = Collections.emptyList();
    private TestDescription testDescription;
    private LimitSet limits = null;

    public long getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(long waitBefore) {
        this.startDelay = waitBefore;
    }
    
    public String getParentTaskId() {
        return parentTaskId;
    }
    
    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
    
    public void setGroupNumber(int number) {
        this.groupNumber = number;
    }

    public void setGroupName(String testGroupName) {
        this.groupName = testGroupName;
    }

    public TestDescription getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(TestDescription testDescription) {
        this.testDescription = testDescription;
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

    public List<Provider<TestListener>> getListeners() {
        return listeners;
    }

    public void setListeners(List<Provider<TestListener>> listeners) {
        this.listeners = listeners;
    }

    public String getName() {
        if ("".equals(id)){
            return groupName;
        }
        return String.format("%s:%s", groupName, id);
    }

    public boolean isAttendant() {
        //TODO
        return terminateStrategyConfiguration instanceof InfiniteTerminationStrategyConfiguration;
    }

    public WorkloadTask generate(AtomicBoolean shutdown) {
        WorkloadTask task = testDescription.generatePrototype();
        task.setName(getName());
        task.setGroupNumber(groupNumber);
        task.setSessionId(sessionInfoProvider.getSessionId());
        task.setTaskId(taskIdProvider.getTaskId());
        task.setParentTaskId(parentTaskId);
        if (startDelay > 0) {
            task.setStartDelay(startDelay);
        }
        if (task.getVersion()==null) task.setVersion("0");
        
        task.setTestListeners(listeners);
        task.setLimits(limits);

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

    public void setLimits(LimitSet limits) {
        this.limits = limits;
    }
}
