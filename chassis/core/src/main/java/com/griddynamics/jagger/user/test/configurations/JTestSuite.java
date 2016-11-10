package com.griddynamics.jagger.user.test.configurations;

import java.util.List;

/**
 * Defines the test suite consisting of several {@link JTestGroup}.
 */
public class JTestSuite {


    private String name;
    private List<JTestGroup> testGroups;


    public static Builder builder() {
        return new Builder();
    }

    private JTestSuite(Builder builder) {
        this.testGroups = builder.testGroups;
        this.name = builder.name;
    }


    public static class Builder {
        private String name;
        private List<JTestGroup> testGroups;

        private Builder() {
        }

        /**
         * Sets the {@code testGroups} for test suite.
         * Test groups will be executed in the order from beginning to the end of the list.
         *
         * @param testGroups List of test groups.
         */
        public Builder withTestGroups(List<JTestGroup> testGroups) {
            this.testGroups = testGroups;
            return this;
        }

        /**
         * Sets name for a {@link JTestSuite}.
         *
         * @param name name of TestSuite.
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
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

    public String getName() {
        return name;
    }
}
