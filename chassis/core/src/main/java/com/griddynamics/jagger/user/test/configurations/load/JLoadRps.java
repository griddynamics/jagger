package com.griddynamics.jagger.user.test.configurations.load;

/**
 * @author asokol
 *         created 10/19/16
 */
public class JLoadRps implements JLoad {

    private long requestsPerSecond;
    private long maxLoadThreads;
    private long warmUpTimeInSeconds;

    private JLoadRps(Builder builder) {
        this.maxLoadThreads = builder.maxLoadThreads;
        this.requestsPerSecond = builder.requestsPerSecond;
        this.warmUpTimeInSeconds = builder.warmUpTimeInSeconds;
    }


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private long requestsPerSecond;
        private long maxLoadThreads;
        private long warmUpTimeInSeconds;


        public Builder withRequestPerSecond(long requestPerSecond) {
            this.requestsPerSecond = requestPerSecond;
            return this;
        }

        public Builder withMaxLoadThreads(long maxLoadThreads) {
            this.maxLoadThreads = maxLoadThreads;
            return this;
        }

        public Builder withWarmUpTimeInSeconds(long warmUpTimeInSeconds) {
            this.warmUpTimeInSeconds = warmUpTimeInSeconds;
            return this;
        }


        public JLoadRps build() {
            return new JLoadRps(this);
        }


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
}
