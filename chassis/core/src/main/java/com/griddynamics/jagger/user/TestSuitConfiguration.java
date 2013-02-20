package com.griddynamics.jagger.user;

import com.griddynamics.jagger.master.configuration.Task;

import java.util.ArrayList;
import java.util.List;

public class TestSuitConfiguration {

    private List<TestGroupConfiguration> testGroups;

    public void setTestGroups(List<TestGroupConfiguration> testGroups) {
        this.testGroups = testGroups;
    }

    public List<TestGroupConfiguration> getTestGroups() {
        return testGroups;
    }

    public List<Task> generate() {
        if (testGroups == null)
            return null;

        int number = 0;
        List<Task> result = new ArrayList<Task>(testGroups.size());
        for (TestGroupConfiguration testGroupConfiguration: testGroups) {
            testGroupConfiguration.setNumber(number++);
            result.add(testGroupConfiguration.generate());
        }
        return result;
    }
}
