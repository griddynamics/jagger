package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;

import java.util.Objects;

/**
 * * This type of load implements an exact number of invocation with exact number of threads performed by Jagger.
 *
 * @ingroup Main_Load_profiles_group
 */
public class JLoadProfileInvocation implements JLoadProfile {

    private final int invocationCount;
    private final int threadCount;
    private final int delay;
    private final int period;
    private final int tickInterval;

    private JLoadProfileInvocation(Builder builder) {
        this.invocationCount = builder.invocationCount;
        this.threadCount = builder.threadCount;
        this.delay = builder.delay;
        this.period = builder.period;
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
        private int delay;
        private int period;
        // Tick interval doesn't have setter, since it's unclear if this field is needed. Check https://issues.griddynamics.net/browse/JFG-1031
        private int tickInterval;

        public Builder(InvocationCount invocationCount, ThreadCount threadCount) {
            Objects.requireNonNull(invocationCount);
            Objects.requireNonNull(threadCount);

            this.tickInterval = DEFAULT_TICK_INTERVAL;
            this.period = DEFAULT_PERIOD;
            this.delay = DEFAULT_DELAY;

            this.invocationCount = invocationCount.value();
            this.threadCount = threadCount.value();
        }

        /**
         * Optional: Delay between invocations. Default value is 0.
         *
         * @param delay The delay between invocations.
         */
        public Builder withDelayBetweenInvocationsInSeconds(int delay) {
            this.delay = delay;
            return this;
        }

        /**
         * Optional: Period of invocation. By default is not used.
         *
         * @param period period between invocations.
         */
        public Builder withPeriodBetweenLoadInSeconds(int period) {
            this.period = period;
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

    public int getDelay() {
        return delay;
    }

    public int getPeriod() {
        return period;
    }

    public int getTickInterval() {
        return tickInterval;
    }
}
