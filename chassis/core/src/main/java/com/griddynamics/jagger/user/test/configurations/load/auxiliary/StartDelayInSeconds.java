package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

public class StartDelayInSeconds {
    private final long startDelayInSeconds;

    private StartDelayInSeconds(long startDelayInSeconds) {
        if (startDelayInSeconds < 0) {
            throw new IllegalArgumentException(String.format("Start delay in seconds must be >= 0. Provided value is %s", startDelayInSeconds));
        }
        this.startDelayInSeconds = startDelayInSeconds;
    }

    public long value() {
        return startDelayInSeconds;
    }

    public static StartDelayInSeconds of(long startDelayInSeconds) { return new StartDelayInSeconds(startDelayInSeconds); }
}
