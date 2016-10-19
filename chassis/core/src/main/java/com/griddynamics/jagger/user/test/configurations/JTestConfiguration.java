package com.griddynamics.jagger.user.test.configurations;

import java.util.List;

/**
 * @author asokol
 *         created 10/18/16
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

        public Builder withTestGroups(List<JTestGroup> testGroups) {
            this.testGroups = testGroups;
            return this;
        }

        public JTestConfiguration build() {
            return new JTestConfiguration(this);
        }


    }


}
