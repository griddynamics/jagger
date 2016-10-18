package com.griddynamics.jagger.user.test.configurations;

import java.util.List;

/**
 * @author asokol
 *         created 10/18/16
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

        public Builder withTests(List<JTest> tests) {
            this.tests = tests;
            return this;
        }

        public JTestGroup build() {
            return new JTestGroup(this);
        }


    }

}
