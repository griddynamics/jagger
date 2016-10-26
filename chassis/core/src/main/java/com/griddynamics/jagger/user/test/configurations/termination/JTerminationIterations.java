package com.griddynamics.jagger.user.test.configurations.termination;

/**
 * Test with such termination strategy will last till defined number of requests are executed.
 */
public class JTerminationIterations implements JTermination {

    private long iterationCount;
    private long maxDurationInSeconds = 7200;


    private JTerminationIterations(Builder builder) {
        this.iterationCount = builder.iterationCount;
        this.maxDurationInSeconds = builder.maxDurationInSeconds;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private long iterationCount;
        private long maxDurationInSeconds;

        /**
         * Sets target number of requests to the system under test.
         *
         * @param iterationCount the number of test executions.
         */
        public Builder withIterationsCount(long iterationCount) {
            this.iterationCount = iterationCount;
            return this;
        }

        /**
         * sets timeout for the test execution.
         * Default value is 2 hours.
         *
         * @param maxDurationInSeconds maximum test execution time value.
         */
        public Builder withMaxDurationInSeconds(long maxDurationInSeconds) {
            this.maxDurationInSeconds = maxDurationInSeconds;
            return this;
        }


        /**
         * Creates the {@link JTermination} instance.
         *
         * @return termination instance.
         */
        public JTerminationIterations build() {
            return new JTerminationIterations(this);
        }

    }

    public long getIterationCount() {
        return iterationCount;
    }

    public long getMaxDurationInSeconds() {
        return maxDurationInSeconds;
    }
}
