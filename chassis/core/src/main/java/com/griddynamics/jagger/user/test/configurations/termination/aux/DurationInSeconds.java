package com.griddynamics.jagger.user.test.configurations.termination.aux;

/**
 * Test load execution time in seconds.
 */
public final class DurationInSeconds {
    private DurationInSeconds(long durationInSeconds) {
        if (durationInSeconds <= 0) {
            throw new IllegalArgumentException(
                    String.format("Duration in seconds must be > 0. Provided value is %s", durationInSeconds));
        }
        this.durationInSeconds = durationInSeconds;
    }
    
    public static DurationInSeconds of(long value) {
        return new DurationInSeconds(value);
    }

    public long value() {
        return durationInSeconds;
    }

    private final long durationInSeconds;
}
