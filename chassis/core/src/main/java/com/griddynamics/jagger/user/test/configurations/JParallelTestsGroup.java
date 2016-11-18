package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.aux.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes the group of {@link JLoadTest} instances that will be executed in parallel.
 */
public class JParallelTestsGroup {
    private final String id;
    private final List<JLoadTest> tests;

    public static Builder builder(Id id, List<JLoadTest> tests) {
        return new Builder(id, tests);
    }
    
    public static Builder builder(Id id, JLoadTest test, JLoadTest... tests) {
        
        List<JLoadTest> testList = new ArrayList<>();
        testList.add(test);
        Collections.addAll(testList, tests);
        
        return new Builder(id, testList);
    }

    private JParallelTestsGroup(Builder builder) {
        this.id = builder.id.value();
        this.tests = builder.tests;
    }

    public static class Builder {
        private final Id id;

        private final List<JLoadTest> tests;
    
        public Builder(Id id, List<JLoadTest> tests) {
            this.id = id;
            this.tests = tests;
        }

        /**
         * Creates the object of {@link JParallelTestsGroup} type with custom parameters.
         *
         * @return {@link JParallelTestsGroup} object.
         */
        public JParallelTestsGroup build() {
            return new JParallelTestsGroup(this);
        }

    }

    public List<JLoadTest> getTests() {
        return tests;
    }

    public String getId() {
        return id;
    }
}
