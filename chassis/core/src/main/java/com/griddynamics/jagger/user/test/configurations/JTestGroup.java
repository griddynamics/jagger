package com.griddynamics.jagger.user.test.configurations;

import java.util.List;

/**
 * Group of test which that should be run simultaneously.
 */
public class JTestGroup {
    private List<JTest> tests;


    public static Builder builder() {
        return new Builder();
    }

    private JTestGroup(Builder builder) {
        this.tests = builder.tests;
    }


    public static class Builder {
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

        public JTestGroup build() {
            return new JTestGroup(this);
        }

    }

    public List<JTest> getTests() {
        return tests;
    }
}
