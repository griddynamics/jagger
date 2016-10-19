package com.griddynamics.jagger.user.test.configurations.termination;

/**
 * Use this termination, when you would like to execute your load within a certain time.
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


        public Builder withDurationInSeconds(long durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
            return this;
        }


        public JTerminationDuration build() {
            return new JTerminationDuration(this);
        }

    }

    public long getDurationInSeconds() {
        return durationInSeconds;
    }
}
