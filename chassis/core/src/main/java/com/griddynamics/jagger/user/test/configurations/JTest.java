package com.griddynamics.jagger.user.test.configurations;

/**
 * Test configuration holder.
 * Here a user can set termination strategy, load configuration and other parameters of a test.
 */
public class JTest {


    private String id;
    private JTestDescription testDescription;
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
        this.testDescription = builder.jTestDescription;
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

        /**
         * Set {@code id} for a test.
         *
         * @param id for of a test.
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set description for a test.
         *
         * @param jTestDescription the test description.
         */
        public Builder withJTestDescription(JTestDescription jTestDescription) {
            this.jTestDescription = jTestDescription;
            return this;
        }

        /**
         * This type of load imitates an exact number of requests per second. Where request is invoke from Jagger.
         * By using attribute 'requestsPerSecond', you can configure a number of requests. Attribute 'maxLoadThreads'
         * says what is the maximum number of threads Jagger engine is allowed to create, to provide the requested load.
         * By default it equals 4000. If attribute 'warmUpTimeInSeconds' is set,
         * load will increase from 0 to the value for this time.
         *
         * @param requestsPerSecond   request per second.
         * @param maxLoadThreads      maximum number of threads Jagger engine is allowed to create.
         * @param warmUpTimeInSeconds load will increase from 0 to the value for this time
         */
        public Builder withLoadRps(long requestsPerSecond, long maxLoadThreads, long warmUpTimeInSeconds) {
            this.requestsPerSecond = requestsPerSecond;
            this.maxLoadThreads = maxLoadThreads;
            this.warmUpTimeInSeconds = warmUpTimeInSeconds;
            return this;
        }

        /**
         * This type of load imitates an exact number of requests per second. Where request is invoke from Jagger.
         * By using attribute 'requestsPerSecond', you can configure a number of requests. Attribute 'maxLoadThreads'
         * says what is the maximum number of threads Jagger engine is allowed to create, to provide the requested load.
         * By default it equals 4000.
         *
         * @param requestsPerSecond request per second.
         * @param maxLoadThreads    maximum number of threads Jagger engine is allowed to create.
         */
        public Builder withLoadRps(long requestsPerSecond, long maxLoadThreads) {
            this.requestsPerSecond = requestsPerSecond;
            this.maxLoadThreads = maxLoadThreads;
            return this;
        }

        /**
         * Termination describes how long load will be executed. Termination can be configured by element termination.
         * There are some types of termination.
         * Use this termination, when you would like to execute your
         * load within a certain time.
         *
         * @param durationInSeconds duration in seconds.
         */
        public Builder withTerminationDuration(long durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
            this.terminationType = TerminationType.TERMINATION_DURATION;
            return this;
        }

        /**
         * Termination describes how long load will be executed. Termination can be configured by element termination.
         * This termination strategy is helpful, when you would like to execute an exact number of iterations. Attribute 'iterations'
         * say how much iterations you would like to execute. With attribute 'maxDuration' you can configure maximum execution time of test.
         * By default it equals 2 hours.
         *
         * @param iterationCount       the number of iterations.
         * @param maxDurationInSeconds max durations.
         */
        public Builder withTerminationIterations(long iterationCount, long maxDurationInSeconds) {
            this.iterationCount = iterationCount;
            this.maxDurationInSeconds = durationInSeconds;
            this.terminationType = TerminationType.TERMINATION_ITERATIONS;
            return this;
        }

        /**
         * Termination describes how long load will be executed. Termination can be configured by element termination.
         * Test with such termination strategy will wait another tests in test-group to be stopped.
         */
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

    public JTestDescription getTestDescription() {
        return testDescription;
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
