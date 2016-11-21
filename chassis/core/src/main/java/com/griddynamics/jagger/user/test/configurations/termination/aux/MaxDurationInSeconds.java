package com.griddynamics.jagger.user.test.configurations.termination.aux;

/**
 * The timeout for the test execution.
 */
public final class MaxDurationInSeconds {
    private final long maxDurationInSeconds;

    public MaxDurationInSeconds(long maxDurationInSeconds) {
        if (maxDurationInSeconds <= 0) {
            throw new IllegalArgumentException(
                    String.format("Max duration in seconds must be > 0. Provided value is %s", maxDurationInSeconds));
        }
        this.maxDurationInSeconds = maxDurationInSeconds;
    }
    
    public static MaxDurationInSeconds of(long maxDurationInSeconds) {
        return new MaxDurationInSeconds(maxDurationInSeconds);
    }
    
    public long value() {
        return maxDurationInSeconds;
    }
}
