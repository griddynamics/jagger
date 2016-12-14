package com.griddynamics.jagger.user;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupListener;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.master.CompositableTask;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.monitoring.InfiniteDuration;
import com.griddynamics.jagger.monitoring.MonitoringTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
@Deprecated
public class TestGroupConfiguration {

    private String id;
    private List<TestConfiguration> tests;
    private List<Provider<TestGroupListener>> listeners = Collections.EMPTY_LIST;
    private List<Provider<TestGroupDecisionMakerListener>> testGroupDecisionMakerListeners = Collections.EMPTY_LIST;
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
        HashSet<String> names = new HashSet<String>();

        CompositeTask compositeTask = new CompositeTask();
        compositeTask.setLeading(new ArrayList<CompositableTask>());
        compositeTask.setAttendant(new ArrayList<CompositableTask>());
        compositeTask.setNumber(number);
        compositeTask.setListeners(listeners);
        compositeTask.setDecisionMakerListeners(testGroupDecisionMakerListeners);
        compositeTask.setName(id+"-group");

        for (TestConfiguration testConfig : tests) {
            testConfig.setTestGroupName(id);
            testConfig.setNumber(number);
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
            MonitoringTask attendantMonitoring = new MonitoringTask(number, id + " --- monitoring", id, new InfiniteDuration());
            compositeTask.getAttendant().add(attendantMonitoring);
        }
        return compositeTask;
    }
}
