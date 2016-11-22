package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.LifeTimeInSeconds;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.SlewRateUsersPerSecond;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.StartDelayInSeconds;

import java.util.Objects;


/**
 * This class represents a user group in {@link JLoadProfileUserGroups}.
 */
public class JLoadProfileUsers {

    /**
     * A goal number of threads.
     */
    private final long numberOfUsers;

    /**
     * Describes how long threads will be alive. Default is 2 days.
     */
    private long lifeTimeInSeconds;

    /**
     * Delay before first thread will start. Default is 0.
     */
    private long startDelayInSeconds;

    /**
     * Describes how many threads to start during every iteration. Default is numberOfUsers value.
     */
    private long slewRateUsersPerSecond;

    public JLoadProfileUsers(NumberOfUsers numberOfUsers) {
        Objects.nonNull(numberOfUsers);

        this.numberOfUsers = numberOfUsers.value();
        this.lifeTimeInSeconds = 60 * 60 * 48; // 2 days
        this.slewRateUsersPerSecond = numberOfUsers.value();
    }

    public static JLoadProfileUsers of(NumberOfUsers numberOfUsers) {
        return new JLoadProfileUsers(numberOfUsers);
    }

    public long getNumberOfUsers() {
        return numberOfUsers;
    }

    public long getLifeTimeInSeconds() {
        return lifeTimeInSeconds;
    }

    /**
     * Describes how long threads will be alive. Default is 2 days.
     */
    public JLoadProfileUsers withLifeTimeInSeconds(LifeTimeInSeconds lifeTimeInSeconds) {
        this.lifeTimeInSeconds = lifeTimeInSeconds.value();
        return this;
    }

    public long getStartDelayInSeconds() {
        return startDelayInSeconds;
    }

    /**
     * Delay before first thread will start. Default is 0.
     */
    public JLoadProfileUsers withStartDelayInSeconds(StartDelayInSeconds startDelayInSeconds) {
        this.startDelayInSeconds = startDelayInSeconds.value();
        return this;
    }

    public long getSlewRateUsersPerSecond() {
        return slewRateUsersPerSecond;
    }

    /**
     * Describes how many threads to start during every iteration. Default is numberOfUsers value.
     */
    public JLoadProfileUsers withSlewRateUsersPerSecond(SlewRateUsersPerSecond slewRateUsersPerSecond) {
        this.slewRateUsersPerSecond = slewRateUsersPerSecond.value();
        return this;
    }
}
