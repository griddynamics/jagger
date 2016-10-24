package com.griddynamics.jagger.user.test.configurations.termination;

/**
 * Test with such termination strategy will wait another tests in test-group to be stopped.
 */
public class JTerminationBackground implements JTermination {

    private JTerminationBackground(Builder builder) {
    }

    public static class Builder {
        /**
         * Creates the {@link JTermination} instance.
         *
         * @return the termination instance.
         */
        public JTerminationBackground build() {
            return new JTerminationBackground(this);
        }

    }
}
