package com.griddynamics.jagger.user.test.configurations.termination;

/**
 * This termination strategy is helpful, when you would like to execute an exact number of iterations. Attribute 'iterations'
 * say how much iterations you would like to execute. With attribute 'maxDuration' you can configure maximum execution time of test.
 * By default it equals 2 hours.
 */
public class JTerminationIterations implements JTermination {

    private long iterationCount;
    private long maxDurationInSeconds;


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


        public Builder withIterationsCount(long iterationCount) {
            this.iterationCount = iterationCount;
            return this;
        }

        public Builder withMaxDurationInSeconds(long maxDurationInSeconds) {
            this.maxDurationInSeconds = maxDurationInSeconds;
            return this;
        }


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
