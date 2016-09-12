package com.griddynamics.jagger.user;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupListener;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.SessionInfoProvider;
import com.griddynamics.jagger.master.TaskIdProvider;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.monitoring.InfiniteDuration;
import com.griddynamics.jagger.monitoring.MonitoringTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestGroupConfiguration {
    
    @Autowired
    private SessionInfoProvider sessionInfoProvider;
    
    @Autowired
    private TaskIdProvider taskIdProvider;

    private String id;
    private List<TestConfiguration> tests;
    private List<Provider<TestGroupListener>> listeners = Collections.emptyList();
    private List<Provider<TestGroupDecisionMakerListener>> testGroupDecisionMakerListeners = Collections.emptyList();
    private boolean monitoringEnabled;
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }

    public void setMonitoringEnabled(boolean monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
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

    public List<TestConfiguration> getTests() {
        return tests;
    }

    public void setTests(List<TestConfiguration> tests) {
        this.tests = tests;
    }

    public List<Provider<TestGroupListener>> getListeners() {
        return listeners;
    }

    public void setListeners(List<Provider<TestGroupListener>> listeners) {
        this.listeners = listeners;
    }

    public List<Provider<TestGroupDecisionMakerListener>> getTestGroupDecisionMakerListeners() {
        return testGroupDecisionMakerListeners;
    }

    public void setTestGroupDecisionMakerListeners(List<Provider<TestGroupDecisionMakerListener>> testGroupDecisionMakerListeners) {
        this.testGroupDecisionMakerListeners = testGroupDecisionMakerListeners;
    }

    public Task generate() {
        HashSet<String> names = new HashSet<>();

        CompositeTask compositeTask = new CompositeTask();
        compositeTask.setGroupNumber(number);
        compositeTask.setTaskId(taskIdProvider.getTaskId());
        compositeTask.setName(id+"-group");
        compositeTask.setSessionId(sessionInfoProvider.getSessionId());
        
        compositeTask.setLeading(new ArrayList<>());
        compositeTask.setAttendant(new ArrayList<>());
        compositeTask.setListeners(listeners);
        compositeTask.setDecisionMakerListeners(testGroupDecisionMakerListeners);

        for (TestConfiguration testConfig : tests) {
            testConfig.setGroupName(compositeTask.getName());
            testConfig.setGroupNumber(number);
            testConfig.setParentTaskId(compositeTask.getTaskId());
            if (!names.contains(testConfig.getName())) {
                names.add(testConfig.getName());
                //TODO figure out if it's really needed
                AtomicBoolean shutdown = new AtomicBoolean(false);
                if (testConfig.isAttendant()) {
                    compositeTask.getAttendant().add(testConfig.generate(shutdown));
                } else {
                    compositeTask.getLeading().add(testConfig.generate(shutdown));
                }
            } else {
                throw new IllegalArgumentException(String.format("Task with name '%s' already exists", testConfig.getName()));
            }
        }

        if (monitoringEnabled) {
            MonitoringTask attendantMonitoring =
                    new MonitoringTask(number, String.format("%s:monitoring", compositeTask.getName()),
                                       compositeTask.getTaskId(), new InfiniteDuration()
                    );
            attendantMonitoring.setTaskId(taskIdProvider.getTaskId());
            attendantMonitoring.setSessionId(sessionInfoProvider.getSessionId());
            compositeTask.getAttendant().add(attendantMonitoring);
        }
        return compositeTask;
    }
}
