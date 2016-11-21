package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.aux.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines the test suite consisting of several {@link JParallelTestsGroup}.
 */
public class JLoadScenario {

    private final String id;
    private final List<JParallelTestsGroup> testGroups;

    public static Builder builder(Id id, List<JParallelTestsGroup> testGroups) {
        return new Builder(id, testGroups);
    }
    
    public static Builder builder(Id id, JParallelTestsGroup testGroup, JParallelTestsGroup... testGroups) {
        
        List<JParallelTestsGroup> jParallelTestsGroupList = new ArrayList<>();
        jParallelTestsGroupList.add(testGroup);
        Collections.addAll(jParallelTestsGroupList, testGroups);
        
        return new Builder(id, jParallelTestsGroupList);
    }

    private JLoadScenario(Builder builder) {
        this.id = builder.id.value();
        this.testGroups = builder.testGroups;
    }

    public static class Builder {
        private final Id id;
        private final List<JParallelTestsGroup> testGroups;
    
        public Builder(Id id, List<JParallelTestsGroup> testGroups) {
            this.id = id;
            this.testGroups = testGroups;
        }

        /**
         * Creates the object of {@link JLoadScenario} type with custom parameters.
         *
         * @return {@link JLoadScenario} object.
         */
        public JLoadScenario build() {
            return new JLoadScenario(this);
        }

    }

    public List<JParallelTestsGroup> getTestGroups() {
        return testGroups;
    }

    public String getId() {
        return id;
    }
}
