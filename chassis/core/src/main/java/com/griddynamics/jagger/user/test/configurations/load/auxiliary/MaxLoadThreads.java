package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

/**
 * The maximum number of threads, which Jagger engine can create to provide the requested load.
 * By default it equals 4000. You can change this value in property file
 *
 */
public final class MaxLoadThreads {
    private final long maxLoadThreads;

    private MaxLoadThreads(long maxLoadThreads) {
        if (maxLoadThreads <= 0) {
            throw new IllegalArgumentException(
                    String.format("The maximum number of threads must be > 0. Provided value is %s", maxLoadThreads));
        }
        this.maxLoadThreads = maxLoadThreads;
    }

    public static MaxLoadThreads of(long maxLoadThreads) {
        return new MaxLoadThreads(maxLoadThreads);
    }

    public long value() {
        return maxLoadThreads;
    }
}
