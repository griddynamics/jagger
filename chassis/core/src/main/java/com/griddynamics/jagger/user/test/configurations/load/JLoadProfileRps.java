package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;

import java.util.Objects;

/**
 * This type of load implements an exact number of requests per second performed by Jagger.
 *
 * @ingroup Main_Load_profiles_group
 */
public class JLoadProfileRps implements JLoadProfile {

    private final long requestsPerSecond;
    private final long maxLoadThreads;
    private final long warmUpTimeInSeconds;
    private final int tickInterval;

    private JLoadProfileRps(Builder builder) {
        Objects.requireNonNull(builder);

        this.requestsPerSecond = builder.requestsPerSecond;
        this.maxLoadThreads = builder.maxLoadThreads;
        this.warmUpTimeInSeconds = builder.warmUpTimeInSeconds;
        this.tickInterval = builder.tickInterval;
    }

    /** Builder of the JLoadProfileRps: request per seconds
     * @n
     * @details Constructor parameters are mandatory for the JLoadProfileRps. All parameters, set by setters are optional
     * @n
     * @param requestsPerSecond   - The number of requests per second Jagger shall perform
     */
    public static Builder builder(RequestsPerSecond requestsPerSecond) {
        return new Builder(requestsPerSecond);
    }

    public static class Builder {
        static final int DEFAULT_TICK_INTERVAL = 1000;
        static final int DEFAULT_MAX_LOAD_THREADS = 4000;
        static final int DEFAULT_WARM_UP_TIME = -1;
        private final long requestsPerSecond;
        private long maxLoadThreads;
        private long warmUpTimeInSeconds;

        // Tick interval doesn't have setter, since it's unclear if this field is needed. Check https://issues.griddynamics.net/browse/JFG-1000
        private int tickInterval;

        /** Builder of JLoadProfileRps: request per seconds
         * @n
         * @details Constructor parameters are mandatory for the JLoadProfileRps. All parameters, set by setters are optional
         * @n
         * @param requestsPerSecond   - The number of requests per second Jagger shall perform
         */
        public Builder(RequestsPerSecond requestsPerSecond) {
            Objects.requireNonNull(requestsPerSecond);

            this.requestsPerSecond = requestsPerSecond.value();
            this.maxLoadThreads = DEFAULT_MAX_LOAD_THREADS;
            this.warmUpTimeInSeconds = DEFAULT_WARM_UP_TIME;
            this.tickInterval = DEFAULT_TICK_INTERVAL;
        }

        /** Creates an object of JLoadProfileRps type with custom parameters.
         * @return JLoadProfileRps object.
         */
        public JLoadProfileRps build() {
            return new JLoadProfileRps(this);
        }

        /** Optional: Max load threads. Default is 4000.
         * @param maxLoadThreads The maximum number of threads, which Jagger engine can create to provide the requested load
         */
        public Builder withMaxLoadThreads(long maxLoadThreads) {
            if (maxLoadThreads <= 0) {
                throw new IllegalArgumentException(String.format("The maximum number of threads must be > 0. Provided value is %s", maxLoadThreads));
            }
            this.maxLoadThreads = maxLoadThreads;
            return this;
        }

        /** Optional: Warm up time (in seconds). Default is -1.
         * @param warmUpTimeInSeconds The warm up time value in seconds. Jagger increases load from 0 to @b requestPerSecond by @b warmUpTimeInSeconds
         */
        public Builder withWarmUpTimeInSeconds(long warmUpTimeInSeconds) {
            if (warmUpTimeInSeconds < 0) {
                throw new IllegalArgumentException(
                        String.format("The warm up time value in seconds. must be >= 0. Provided value is %s", warmUpTimeInSeconds));
            }
            this.warmUpTimeInSeconds = warmUpTimeInSeconds;
            return this;
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

    public int getTickInterval() {
        return tickInterval;
    }
}
