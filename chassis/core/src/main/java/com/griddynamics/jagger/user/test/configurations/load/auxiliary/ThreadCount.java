package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

/**
 * The number of threads for {@link com.griddynamics.jagger.user.test.configurations.load.JLoadProfile}.
 */
public class ThreadCount {
    private final int threadCount;

    private ThreadCount(int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException(String.format("Number of threads must be > 0. Provided value is %s", threadCount));
        }
        this.threadCount = threadCount;
    }

    public int value() {
        return threadCount;
    }

    public static ThreadCount of(int invocationCount) {
        return new ThreadCount(invocationCount);
    }
}
