package com.griddynamics.jagger.user.test.configurations;

import java.util.List;

/**
 * Describes the group of {@link JTest} that should run in parallel.
 */
public class JTestGroup {
    private String id;
    private List<JTest> tests;


    public static Builder builder() {
        return new Builder();
    }

    private JTestGroup(Builder builder) {
        this.tests = builder.tests;
        this.id = builder.id;
    }


    public static class Builder {
        private String id;

        private List<JTest> tests;

        private Builder() {
        }

        /**
         * Sets the tests executed in parallel.
         *
         * @param tests a list of tests.
         */
        public Builder withTests(List<JTest> tests) {
            this.tests = tests;
            return this;
        }

        /**
         * Sets id for the group.
         *
         * @param id group name.
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
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
