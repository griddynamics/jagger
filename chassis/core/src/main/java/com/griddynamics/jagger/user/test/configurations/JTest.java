package com.griddynamics.jagger.user.test.configurations;

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
    private long maxLoadThreads;
    private long warmUpTimeInSeconds;
    private TerminationType terminationType;
    private long durationInSeconds;
    private long iterationCount;
    private long maxDurationInSeconds;


    public enum TerminationType {
        TERMINATION_ITERATIONS,
        TERMINATION_DURATION,
        TERMINATION_BACKGROUND
    }


    private JTest(Builder builder) {
        this.id = builder.id;
        this.jTestDescription = builder.jTestDescription;
        this.requestsPerSecond = builder.requestsPerSecond;
        this.maxLoadThreads = builder.maxLoadThreads;
        this.warmUpTimeInSeconds = builder.warmUpTimeInSeconds;
        this.terminationType = builder.terminationType;
        this.durationInSeconds = builder.durationInSeconds;
        this.iterationCount = builder.iterationCount;
        this.maxDurationInSeconds = builder.maxDurationInSeconds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private JTestDescription jTestDescription;
        private long requestsPerSecond;
        private long maxLoadThreads = 4000;
        private long warmUpTimeInSeconds;
        private long durationInSeconds;
        private long iterationCount;
        private long maxDurationInSeconds;
        private TerminationType terminationType;

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
            this.requestsPerSecond = requestsPerSecond;
            this.maxLoadThreads = maxLoadThreads;
            this.warmUpTimeInSeconds = warmUpTimeInSeconds;
            return this;
        }

        public Builder withLoadRps(long requestsPerSecond, long maxLoadThreads) {
            this.requestsPerSecond = requestsPerSecond;
            this.maxLoadThreads = maxLoadThreads;
            return this;
        }

        public Builder withTerminationDuration(long durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
            this.terminationType = TerminationType.TERMINATION_DURATION;
            return this;
        }

        public Builder withTerminationIterations(long iterationCount, long maxDurationInSeconds) {
            this.iterationCount = iterationCount;
            this.maxDurationInSeconds = durationInSeconds;
            this.terminationType = TerminationType.TERMINATION_ITERATIONS;
            return this;
        }

        public Builder withTerminationBackground() {
            this.terminationType = TerminationType.TERMINATION_BACKGROUND;
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

    public long getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public long getMaxLoadThreads() {
        return maxLoadThreads;
    }

    public long getWarmUpTimeInSeconds() {
        return warmUpTimeInSeconds;
    }

    public TerminationType getTerminationType() {
        return terminationType;
    }

    public long getDurationInSeconds() {
        return durationInSeconds;
    }

    public long getIterationCount() {
        return iterationCount;
    }

    public long getMaxDurationInSeconds() {
        return maxDurationInSeconds;
    }
}
