package com.griddynamics.jagger.user.test.configurations.termination;

/**
 * Test with such termination strategy will be executed for the defined time - duration.
 */
public class JTerminationDuration implements JTermination {
    private long durationInSeconds;

    private JTerminationDuration(Builder builder) {
        this.durationInSeconds = builder.durationInSeconds;
    }


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        long durationInSeconds;

        /**
         * Sets test execution time in seconds.
         *
         * @param durationInSeconds test execution time value.
         */
        public Builder withDurationInSeconds(long durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
            return this;
        }

        /**
         * Creates the {@link JTermination} instance.
         *
         * @return the termination instance.
         */
        public JTerminationDuration build() {
            return new JTerminationDuration(this);
        }

    }

    public long getDurationInSeconds() {
        return durationInSeconds;
    }
}
