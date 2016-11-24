package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.MaxLoadThreads;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.WarmUpTimeInSeconds;

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
        Objects.nonNull(builder);

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
     * @param maxLoadThreads      - The maximum number of threads, which Jagger engine can create to provide the requested load
     * @param warmUpTimeInSeconds - The warm up time value in seconds. Jagger increases load from 0 to @b requestPerSecond by @b warmUpTimeInSeconds
     */
    public static Builder builder(RequestsPerSecond requestsPerSecond, MaxLoadThreads maxLoadThreads, WarmUpTimeInSeconds warmUpTimeInSeconds) {
        return new Builder(requestsPerSecond, maxLoadThreads, warmUpTimeInSeconds);
    }

    public static class Builder {
        static final int DEFAULT_TICK_INTERVAL = 1000;
        private final long requestsPerSecond;
        private final long maxLoadThreads;
        private final long warmUpTimeInSeconds;
        private int tickInterval;

        /** Builder of JLoadProfileRps: request per seconds
         * @n
         * @details Constructor parameters are mandatory for the JLoadProfileRps. All parameters, set by setters are optional
         * @n
         * @param requestsPerSecond   - The number of requests per second Jagger shall perform
         * @param maxLoadThreads      - The maximum number of threads, which Jagger engine can create to provide the requested load
         * @param warmUpTimeInSeconds - The warm up time value in seconds. Jagger increases load from 0 to @b requestPerSecond by @b warmUpTimeInSeconds
         */
        public Builder(RequestsPerSecond requestsPerSecond, MaxLoadThreads maxLoadThreads, WarmUpTimeInSeconds warmUpTimeInSeconds) {
            Objects.requireNonNull(requestsPerSecond);
            Objects.requireNonNull(maxLoadThreads);
            Objects.requireNonNull(warmUpTimeInSeconds);

            this.requestsPerSecond = requestsPerSecond.value();
            this.maxLoadThreads = maxLoadThreads.value();
            this.warmUpTimeInSeconds = warmUpTimeInSeconds.value();
            this.tickInterval = DEFAULT_TICK_INTERVAL;
        }

        /** Creates the object of JLoadProfileRps type with custom parameters.
         * @return JLoadProfileRps object.
         */
        public JLoadProfileRps build() {
            return new JLoadProfileRps(this);
        }

        /** Optional: Tick interval (in ms). Default is 1000 ms.
         * @param tickInterval tick interval of load
         */
        public Builder withTickInterval(int tickInterval) {
            this.tickInterval = tickInterval;
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
