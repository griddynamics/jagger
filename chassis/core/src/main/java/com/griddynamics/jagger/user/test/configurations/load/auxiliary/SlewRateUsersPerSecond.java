package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

public class SlewRateUsersPerSecond {

    private final long slewRateUsersPerSecond;

    private SlewRateUsersPerSecond(long slewRateUsersPerSecond) {
        if (slewRateUsersPerSecond <= 0) {
            throw new IllegalArgumentException(String.format("Slew rate users per second must be > 0. Provided value is %s", slewRateUsersPerSecond));
        }
        this.slewRateUsersPerSecond = slewRateUsersPerSecond;
    }

    public long value() {
        return slewRateUsersPerSecond;
    }

    public static SlewRateUsersPerSecond of(long slewRateUsersPerSecond) { return new SlewRateUsersPerSecond(slewRateUsersPerSecond); }
}
