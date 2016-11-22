package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

/**
 * Virtual user life time.
 */
public class LifeTimeInSeconds {
    private final long lifeTimeInSeconds;

    private LifeTimeInSeconds(long lifeTimeInSeconds) {
        if (lifeTimeInSeconds <= 0) {
            throw new IllegalArgumentException(String.format("Lifetime in seconds must be > 0. Provided value is %s", lifeTimeInSeconds));
        }
        this.lifeTimeInSeconds = lifeTimeInSeconds;
    }

    public long value() {
        return lifeTimeInSeconds;
    }

    public static LifeTimeInSeconds of(long lifeTimeInSeconds) { return new LifeTimeInSeconds(lifeTimeInSeconds); }
}
