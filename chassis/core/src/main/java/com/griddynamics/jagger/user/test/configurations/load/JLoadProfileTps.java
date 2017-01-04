package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.TransactionsPerSecond;

import java.util.Objects;

/**
 * This type of load implements an exact number of transactions per second performed by Jagger.
 * @details Transaction is invoke from Jagger + response from system under test.
 * Available attributes:
 *     - transactionsPerSecond - A goal number of transactions per second
 *
 * Optional attributes:
 *     - maxLoadThreads - Maximum number of parallel threads allowed for load generation
 *     - warmUpTimeInMilliseconds - Load will increase from 0 to @e transactionsPerSecond in this time
 *
 * @ingroup Main_Load_profiles_group
 */
public class JLoadProfileTps implements JLoadProfile {

    private final long transactionsPerSecond;
    private final long maxLoadThreads;
    private final long warmUpTimeInMilliseconds;
    private final int tickInterval;

    private JLoadProfileTps(Builder builder) {
        Objects.requireNonNull(builder);

        this.transactionsPerSecond = builder.transactionsPerSecond;
        this.maxLoadThreads = builder.maxLoadThreads;
        this.warmUpTimeInMilliseconds = builder.warmUpTimeInMilliseconds;
        this.tickInterval = builder.tickInterval;
    }

    /** Builder of the JLoadProfileTps: transactions per seconds
     * @n
     * @details Constructor parameters are mandatory for the JLoadProfileTps. All parameters, set by setters are optional
     * @n
     * @param transactionsPerSecond   - The number of transactions per second Jagger shall perform
     */
    public static Builder builder(TransactionsPerSecond transactionsPerSecond) {
        return new Builder(transactionsPerSecond);
    }

    public static class Builder {
        static final int DEFAULT_TICK_INTERVAL = 1000;
        static final int DEFAULT_MAX_LOAD_THREADS = 500;
        static final int DEFAULT_WARM_UP_TIME = -1;
        private final long transactionsPerSecond;
        private long maxLoadThreads;
        private long warmUpTimeInMilliseconds;

        // Tick interval doesn't have setter, since it's unclear if this field is needed. Check https://issues.griddynamics.net/browse/JFG-1000
        private int tickInterval;

        /** Builder of JLoadProfileTps: transactions per seconds
         * @n
         * @details Constructor parameters are mandatory for the JLoadProfileTps. All parameters, set by setters are optional
         * @n
         * @param transactionsPerSecond   - The number of transactions per second Jagger shall perform
         */
        public Builder(TransactionsPerSecond transactionsPerSecond) {
            Objects.requireNonNull(transactionsPerSecond);

            this.transactionsPerSecond = transactionsPerSecond.value();
            this.maxLoadThreads = DEFAULT_MAX_LOAD_THREADS;
            this.warmUpTimeInMilliseconds = DEFAULT_WARM_UP_TIME;
            this.tickInterval = DEFAULT_TICK_INTERVAL;
        }

        /** Creates an object of JLoadProfileTps type with custom parameters.
         * @return JLoadProfileTps object.
         */
        public JLoadProfileTps build() {
            return new JLoadProfileTps(this);
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
         * @param warmUpTimeInMilliseconds The warm up time value in milliseconds. Jagger increases load from 0 to @b transactionsPerSecond by @b warmUpTimeInMilliseconds
         */
        public Builder withWarmUpTimeInSeconds(long warmUpTimeInMilliseconds) {
            if (warmUpTimeInMilliseconds < 0) {
                throw new IllegalArgumentException(
                        String.format("The warm up time value in milliseconds. must be >= 0. Provided value is %s", warmUpTimeInMilliseconds));
            }
            this.warmUpTimeInMilliseconds = warmUpTimeInMilliseconds;
            return this;
        }
    }

    public long getTransactionsPerSecond() {
        return transactionsPerSecond;
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
