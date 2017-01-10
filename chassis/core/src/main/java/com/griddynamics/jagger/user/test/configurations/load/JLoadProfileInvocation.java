package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;

import java.util.Objects;

/**
 * This type of load implements an exact number of invocation, performed by exact number of threads.
 * Available attributes:
 *     - invocationCount - A goal number of invocations
 *     - threadCount - How many threads will be used. Number of invocations, defined by @e invocationCount, will be distributed between this threads
 *
 * Optional attributes:
 *     - delayBetweenInvocationsInMilliseconds - Delay between invocations in milliseconds
 *     - periodInSeconds - Period between load generation in seconds. If periodInSeconds is set, Jagger will perform @e invocationCount of requests every @e periodInSeconds seconds
 *
 * Examples: @n
 * @code
 * JLoadProfileInvocation.builder(InvocationCount.of(500), ThreadCount.of(1));
 * @endcode
 * @image html load_InvocationsEnd.png "Invocations load"
 * @n
 * @code
 * JLoadProfileInvocation.builder(InvocationCount.of(500), ThreadCount.of(5)).withPeriodBetweenLoadInSeconds(30).build();
 * @endcode
 * @image html load_InvocationsWithPeriod.png "Periodic invocations load. Same set of requests is executed periodically. 'Period between load' > time to execute all requests"
 * @n
 * @code
 * JLoadProfileInvocation.builder(InvocationCount.of(500), ThreadCount.of(1)).withPeriodBetweenLoadInSeconds(10).build();
 * @endcode
 * @image html load_InvocationsWithPeriod2.png "Periodic invocations load. Same set of requests is executed periodically. 'Period between load' < time to execute all requests"
 *
 * @ingroup Main_Load_profiles_group
 */
public class JLoadProfileInvocation implements JLoadProfile {

    private final int invocationCount;
    private final int threadCount;
    private final int delayBetweenInvocationsInMilliseconds;
    private final int periodInSeconds;
    private final int tickInterval;

    private JLoadProfileInvocation(Builder builder) {
        this.invocationCount = builder.invocationCount;
        this.threadCount = builder.threadCount;
        this.delayBetweenInvocationsInMilliseconds = builder.delayBetweenInvocationsInMilliseconds;
        this.periodInSeconds = builder.periodInSeconds;
        this.tickInterval = builder.tickInterval;
    }

    /**
     * Builder of JLoadProfileInvocation
     *
     * @n
     * @details Constructor parameters are mandatory for the JLoadProfileInvocation. All parameters, set by setters are optional
     * @n
     *
     * @param invocationCount the number of invocation.
     * @param threadCount     the number of threads.
     */
    public static Builder builder(InvocationCount invocationCount, ThreadCount threadCount) {
        return new Builder(invocationCount, threadCount);
    }

    public static class Builder {
        static final int DEFAULT_TICK_INTERVAL = 1000;
        static final int DEFAULT_PERIOD = -1;
        static final int DEFAULT_DELAY = 0;
        private int invocationCount;
        private int threadCount;
        private int delayBetweenInvocationsInMilliseconds;
        private int periodInSeconds;
        // Tick interval doesn't have setter, since it's unclear if this field is needed. Check https://issues.griddynamics.net/browse/JFG-1031
        private int tickInterval;

        public Builder(InvocationCount invocationCount, ThreadCount threadCount) {
            Objects.requireNonNull(invocationCount);
            Objects.requireNonNull(threadCount);

            this.tickInterval = DEFAULT_TICK_INTERVAL;
            this.periodInSeconds = DEFAULT_PERIOD;
            this.delayBetweenInvocationsInMilliseconds = DEFAULT_DELAY;

            this.invocationCount = invocationCount.value();
            this.threadCount = threadCount.value();
        }

        /**
         * Optional: Delay between invocations in milliseconds. Default value is 0.
         *
         * @param delay The delay between invocations.
         */
        public Builder withDelayBetweenInvocationsInMilliseconds(int delay) {
            this.delayBetweenInvocationsInMilliseconds = delay;
            return this;
        }

        /**
         * Optional: Period between load generation in seconds. If periodInSeconds is set, Jagger will perform @e invocationCount of requests every @e periodInSeconds seconds
         *
         * @param period period between invocations.
         */
        public Builder withPeriodBetweenLoadInSeconds(int period) {
            this.periodInSeconds = period;
            return this;
        }

        /**
         * Creates an object of JLoadProfileInvocation type with custom parameters.
         *
         * @return JLoadProfileInvocation object.
         */
        public JLoadProfileInvocation build() {
            return new JLoadProfileInvocation(this);
        }


    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getInvocationCount() {
        return invocationCount;
    }

    public int getDelayBetweenInvocationsInMilliseconds() {
        return delayBetweenInvocationsInMilliseconds;
    }

    public int getPeriodInSeconds() {
        return periodInSeconds;
    }

    public int getTickInterval() {
        return tickInterval;
    }
}
