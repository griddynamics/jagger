package com.griddynamics.jagger.user;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.limits.LimitSet;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.UserGroupsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: evelina
 * Date: 2/16/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
@Deprecated
public class TestConfiguration {

    private WorkloadClockConfiguration clockConfiguration;
    private String id;
    private TerminateStrategyConfiguration terminateStrategyConfiguration;
    private int number;
    private String testGroupName;
    private long startDelay = -1;
    private List<Provider<TestListener>> listeners = Collections.EMPTY_LIST;
    private TestDescription testDescription;
    private LimitSet limits = null;

    public long getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(long waitBefore) {
        this.startDelay = waitBefore;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTestGroupName(String testGroupName) {
        this.testGroupName = testGroupName;
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
            return testGroupName;
        }
        return String.format("%s [%s]", testGroupName, id);
    }

    public boolean isAttendant() {
        //TODO
        return terminateStrategyConfiguration instanceof InfiniteTerminationStrategyConfiguration;
    }

    public WorkloadTask generate(AtomicBoolean shutdown) {
        WorkloadTask task = testDescription.generatePrototype();
        task.setName(getName());
        task.setNumber(number);
        if (startDelay > 0) {
            task.setStartDelay(startDelay);
        }
        if (task.getVersion()==null) task.setVersion("0");
        task.setParentTaskId(testGroupName);
        if (task.getTestListeners() == null)
            task.setTestListeners(listeners);
        else
            task.getTestListeners().addAll(listeners);
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
