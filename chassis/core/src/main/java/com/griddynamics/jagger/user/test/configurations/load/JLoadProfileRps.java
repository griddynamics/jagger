package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;

import java.util.Objects;

/**
 * This type of load implements an exact number of requests per second performed by Jagger.
 * @details Request is invoke from Jagger without waiting for the response.
 * Available attributes:
 *     - requestsPerSecond - A goal number of requests per second
 *
 * Optional attributes:
 *     - maxLoadThreads - Maximum number of parallel threads allowed for load generation
 *     - warmUpTimeInMilliseconds - Load will increase from 0 to @e requestsPerSecond in this time
 *
 * Examples: @n
 * @code
 * LoadProfileRps.builder(RequestsPerSecond.of(50)).withWarmUpTimeInMilliseconds(15000).build();
 * @endcode
 * @image html load_rpsWarmUp.png "Requests per seconds load with warm up time"
 * @n
 * Example of load balancing with changing response time: @n
 * In this example, response time is changing periodically from approx 0 to 200 ms. Load generator is keeping load at 50 rps by adjusting number of virtual users
 * @code
 * JLoadProfileRps.builder(RequestsPerSecond.of(50)).build();
 * @endcode
 * @image html load_rps_balance1_latency.png "Response time from the SUT is changing"
 * @image html load_rps_balance2_throughput.png "Number of request per seconds has constant rate due to the number of virtual users balancing"
 * @image html load_rps_balance3_virtual_users.png "Adjustment of virtual users"
 *
 * @ingroup Main_Load_profiles_group
 */
public class JLoadProfileRps implements JLoadProfile {

    private final long requestsPerSecond;
    private final long maxLoadThreads;
    private final long warmUpTimeInMilliseconds;
    private final int tickInterval;

    private JLoadProfileRps(Builder builder) {
        Objects.requireNonNull(builder);

        this.requestsPerSecond = builder.requestsPerSecond;
        this.maxLoadThreads = builder.maxLoadThreads;
        this.warmUpTimeInMilliseconds = builder.warmUpTimeInMilliseconds;
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
        static final int DEFAULT_MAX_LOAD_THREADS = 500;
        static final int DEFAULT_WARM_UP_TIME = -1;
        private final long requestsPerSecond;
        private long maxLoadThreads;
        private long warmUpTimeInMilliseconds;

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
            this.warmUpTimeInMilliseconds = DEFAULT_WARM_UP_TIME;
            this.tickInterval = DEFAULT_TICK_INTERVAL;
        }

        /** Creates an object of JLoadProfileRps type with custom parameters.
         * @return JLoadProfileRps object.
         */
        public JLoadProfileRps build() {
            return new JLoadProfileRps(this);
        }

        /** Optional: Max load threads. Default is 500.
         * @param maxLoadThreads The maximum number of threads, which Jagger engine can create to provide the requested load
         */
        public Builder withMaxLoadThreads(long maxLoadThreads) {
            if (maxLoadThreads <= 0) {
                throw new IllegalArgumentException(String.format("The maximum number of threads must be > 0. Provided value is %s", maxLoadThreads));
            }
            this.maxLoadThreads = maxLoadThreads;
            return this;
        }

        /** Optional: Warm up time (in milliseconds). Default is -1.
         * @param warmUpTimeInMilliseconds The warm up time value in milliseconds. Jagger increases load from 0 to @b requestPerSecond by @b warmUpTimeInMilliseconds
         */
        public Builder withWarmUpTimeInMilliseconds(long warmUpTimeInMilliseconds) {
            if (warmUpTimeInMilliseconds < 0) {
                throw new IllegalArgumentException(
                        String.format("The warm up time value in milliseconds. must be >= 0. Provided value is %s", warmUpTimeInMilliseconds));
            }
            this.warmUpTimeInMilliseconds = warmUpTimeInMilliseconds;
            return this;
        }
    }

    public long getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public long getMaxLoadThreads() {
        return maxLoadThreads;
    }

    public long getWarmUpTimeInMilliseconds() {
        return warmUpTimeInMilliseconds;
    }

    public int getTickInterval() {
        return tickInterval;
    }
}
