package com.griddynamics.jagger.user.test.configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes the group of {@link JTest} that should run in parallel.
 */
public class JTestGroup {
    private final String id;
    private final List<JTest> tests;

    public static Builder builder(Id id, List<JTest> tests) {
        return new Builder(id, tests);
    }
    
    public static Builder builder(Id id, JTest test, JTest... tests) {
        
        List<JTest> testList = new ArrayList<>();
        testList.add(test);
        Collections.addAll(testList, tests);
        
        return new Builder(id, testList);
    }

    private JTestGroup(Builder builder) {
        this.id = builder.id.value();
        this.tests = builder.tests;
    }

    public static class Builder {
        private final Id id;

        private final List<JTest> tests;
    
        public Builder(Id id, List<JTest> tests) {
            this.id = id;
            this.tests = tests;
        }

        /**
         * Creates the object of {@link JTestGroup} type with custom parameters.
         *
         * @return {@link JTestGroup} object.
         */
        public JTestGroup build() {
            return new JTestGroup(this);
        }

    }

    public List<JTest> getTests() {
        return tests;
    }

    public String getId() {
        return id;
    }
}
