package com.griddynamics.jagger.user.test.configurations;

import java.util.List;

/**
 * Contains the groups of tests.
 */
public class JTestConfiguration {

    private List<JTestGroup> testGroups;


    public static Builder builder() {
        return new Builder();
    }

    private JTestConfiguration(Builder builder) {
        this.testGroups = builder.testGroups;
    }


    public static class Builder {
        private List<JTestGroup> testGroups;

        private Builder() {
        }

        /**
         * Sets the {@code testGroups} for configuration.
         *
         * @param testGroups List of test groups.
         */
        public Builder withTestGroups(List<JTestGroup> testGroups) {
            this.testGroups = testGroups;
            return this;
        }

        public JTestConfiguration build() {
            return new JTestConfiguration(this);
        }

    }

    public List<JTestGroup> getTestGroups() {
        return testGroups;
    }
}
