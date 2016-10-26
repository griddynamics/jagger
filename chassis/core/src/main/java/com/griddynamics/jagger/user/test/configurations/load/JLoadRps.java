package com.griddynamics.jagger.user.test.configurations.load;

/**
 * This type of load implements an exact number of requests per second performed by Jagger.
 * The number of requests, the max allowed number of load threads and the warm up time values are configurable.
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

    /**
     * This type of load generates an exact number of requests per second. Where request is invoke from Jagger.
     * By using attribute 'requestsPerSecond', you can configure a number of requests. Attribute 'maxLoadThreads'
     * says what is the maximum number of threads Jagger engine is allowed to create, to provide the requested load.
     * By default it equals 4000. You can change this value in property file. If attribute 'warmUpTimeInSeconds' is set,
     * load will increase from 0 to the value for this time.
     */
    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private long requestsPerSecond;
        private long maxLoadThreads = 4000;
        private long warmUpTimeInSeconds;


        /**
         * Sets the number of requests per second Jagger shall perform.
         *
         * @param requestPerSecond the number of requests per second.
         */
        public Builder withRequestPerSecond(long requestPerSecond) {
            this.requestsPerSecond = requestPerSecond;
            return this;
        }

        /**
         * Sets the maximum number of threads, which Jagger engine can create to provide the requested load.
         * By default it equals 4000.
         *
         * @param maxLoadThreads the maximum number of threads.
         */
        public Builder withMaxLoadThreads(long maxLoadThreads) {
            this.maxLoadThreads = maxLoadThreads;
            return this;
        }

        /**
         * Sets warm up time value in seconds.
         * Jagger increases load from 0 to {@code requestPerSecond} by {@code warmUpTimeInSeconds}.
         *
         * @param warmUpTimeInSeconds time to warm up.
         */
        public Builder withWarmUpTimeInSeconds(long warmUpTimeInSeconds) {
            this.warmUpTimeInSeconds = warmUpTimeInSeconds;
            return this;
        }

        /**
         * Creates the {@link JLoad} instance.
         *
         * @return load instance.
         */
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
