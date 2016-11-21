package com.griddynamics.jagger.user.test.configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines the test suite consisting of several {@link JTestGroup}.
 */
public class JTestSuite {

    private final String id;
    private final List<JTestGroup> testGroups;

    public static Builder builder(Id id, List<JTestGroup> testGroups) {
        return new Builder(id, testGroups);
    }
    
    public static Builder builder(Id id, JTestGroup testGroup, JTestGroup... testGroups) {
        
        List<JTestGroup> jTestGroupList = new ArrayList<>();
        jTestGroupList.add(testGroup);
        Collections.addAll(jTestGroupList, testGroups);
        
        return new Builder(id, jTestGroupList);
    }

    private JTestSuite(Builder builder) {
        this.id = builder.id.value();
        this.testGroups = builder.testGroups;
    }

    public static class Builder {
        private final Id id;
        private final List<JTestGroup> testGroups;
    
        public Builder(Id id, List<JTestGroup> testGroups) {
            this.id = id;
            this.testGroups = testGroups;
        }

        /**
         * Creates the object of {@link JTestSuite} type with custom parameters.
         *
         * @return {@link JTestSuite} object.
         */
        public JTestSuite build() {
            return new JTestSuite(this);
        }

    }

    public List<JTestGroup> getTestGroups() {
        return testGroups;
    }

    public String getId() {
        return id;
    }
}
