package com.griddynamics.jagger.user.test.configurations;

import java.util.List;

/**
 * Group of test which that should be run simultaneously.
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
         * The group of test which should be run simultaneously.
         *
         * @param tests a list of test.
         */
        public Builder withTests(List<JTest> tests) {
            this.tests = tests;
            return this;
        }

        /**
         * Sets id or group name.
         *
         * @param id group name.
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

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
