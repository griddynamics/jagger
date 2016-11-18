package com.griddynamics.jagger.user.test.configurations.load.aux;

/**
 * The warm up time value in seconds.
 * Jagger increases load from 0 to {@code requestPerSecond} by {@code warmUpTimeInSeconds}.
 * <p>
 * Created by Andrey Badaev
 * Date: 16/11/16
 */
public final class WarmUpTimeInSeconds {
    private final long warmUpTimeInSeconds;
    
    private WarmUpTimeInSeconds(long warmUpTimeInSeconds) {
        if (warmUpTimeInSeconds < 0) {
            throw new IllegalArgumentException(
                    String.format("The warm up time value in seconds. must be >= 0. Provided value is %s",
                            warmUpTimeInSeconds
                    ));
        }
        this.warmUpTimeInSeconds = warmUpTimeInSeconds;
    }
    
    public static WarmUpTimeInSeconds of(long warmUpTimeInSeconds) {
        return new WarmUpTimeInSeconds(warmUpTimeInSeconds);
    }
    
    public long value() {
        return warmUpTimeInSeconds;
    }
}
