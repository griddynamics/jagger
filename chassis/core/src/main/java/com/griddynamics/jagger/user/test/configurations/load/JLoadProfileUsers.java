package com.griddynamics.jagger.user.test.configurations.load;

public class JLoadProfileUsers {


    private final int numberOfUsers;

    private long lifeTimeInSeconds;

    private int startDelayInSeconds;

    private int slewRateUsersPerSecond;

    public JLoadProfileUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
        this.lifeTimeInSeconds = 60 * 60 * 48; // 2 days
        this.slewRateUsersPerSecond = numberOfUsers;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public long getLifeTimeInSeconds() {
        return lifeTimeInSeconds;
    }

    public JLoadProfileUsers withLifeTimeInSeconds(long lifeTimeInSeconds) {
        this.lifeTimeInSeconds = lifeTimeInSeconds;
        return this;
    }

    public int getStartDelayInSeconds() {
        return startDelayInSeconds;
    }

    public JLoadProfileUsers withStartDelayInSeconds(int startDelayInSeconds) {
        this.startDelayInSeconds = startDelayInSeconds;
        return this;
    }

    public int getSlewRateUsersPerSecond() {
        return slewRateUsersPerSecond;
    }

    public JLoadProfileUsers withSlewRateUsersPerSecond(int slewRateUsersPerSecond) {
        this.slewRateUsersPerSecond = slewRateUsersPerSecond;
        return this;
    }
}
