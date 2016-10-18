package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;

/**
 * The user
 *
 * @author asokol
 *         created 10/18/16
 */
public class JTest {


    private String id;
    private JTestDescription jTestDescription;
    private long requestsPerSecond;
    private long maxLoadThreads = 4000;
    private long warmUpTimeInSeconds;


    private enum TerminationType {
        TERMINATION_ITERATIONS,
        TERMINATION_BACKGROUND
    }


    private JTest(Builder builder) {
        this.id = builder.id;
        this.jTestDescription = builder.jTestDescription;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private JTestDescription jTestDescription;
        private WorkloadClockConfiguration clockConfiguration;


        private TerminationType typeOfTermination;

        private Builder() {

        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withJTestDescription(JTestDescription jTestDescription) {
            this.jTestDescription = jTestDescription;
            return this;
        }

        public Builder withLoadRps(long requestsPerSecond, long maxLoadThreads, long warmUpTimeInSeconds) {
            return this;
        }

        public Builder withLoadRps(long requestsPerSecond, long maxLoadThreads) {
            return this;
        }

        public Builder withTerminationDuration(long durationInSeconds) {
            return this;
        }

        public Builder withTerminationIterations(long interationCount, long maxDurationInSeconds) {
            return this;
        }

        public Builder withTerminationBackgroud() {
            return this;
        }


        public JTest build() {
            return new JTest(this);
        }


    }

    public String getId() {
        return id;
    }

    public JTestDescription getjTestDescription() {
        return jTestDescription;
    }
}
