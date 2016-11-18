package com.griddynamics.jagger.user.test.configurations.load.aux;

/**
 * The maximum number of threads, which Jagger engine can create to provide the requested load.
 * <p>
 * Created by Andrey Badaev
 * Date: 16/11/16
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
