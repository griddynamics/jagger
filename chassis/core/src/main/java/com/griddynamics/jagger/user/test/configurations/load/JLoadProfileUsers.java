package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;

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
    private final long lifeTimeInSeconds;

    /**
     * Delay before first thread will start. Default is 0.
     */
    private final long startDelayInSeconds;

    /**
     * Describes how many threads to start during every iteration. Default is numberOfUsers value.
     */
    private final long slewRateUsersPerSecond;

    private JLoadProfileUsers(Builder builder) {
        this.numberOfUsers = builder.numberOfUsers.value();
        this.lifeTimeInSeconds = builder.lifeTimeInSeconds;
        this.startDelayInSeconds = builder.startDelayInSeconds;
        this.slewRateUsersPerSecond = builder.slewRateUsersPerSecond;
    }

    public static Builder builder(NumberOfUsers numberOfUsers) {
        return new Builder(numberOfUsers);
    }

    public static class Builder {
        private final NumberOfUsers numberOfUsers;
        private long lifeTimeInSeconds;
        private long startDelayInSeconds;
        private long slewRateUsersPerSecond;

        private Builder(NumberOfUsers numberOfUsers) {
            Objects.nonNull(numberOfUsers);

            this.numberOfUsers = numberOfUsers;
            this.lifeTimeInSeconds = 60 * 60 * 48; // 2 days
            this.slewRateUsersPerSecond = numberOfUsers.value();
        }

        public JLoadProfileUsers build() {
            return new JLoadProfileUsers(this);
        }

        /**
         * Describes how long threads will be alive. Default is 2 days.
         */
        public Builder withLifeTimeInSeconds(long lifeTimeInSeconds) {
            this.lifeTimeInSeconds = lifeTimeInSeconds;
            return this;
        }

        /**
         * Delay before first thread will start. Default is 0.
         */
        public Builder withStartDelayInSeconds(long startDelayInSeconds) {
            this.startDelayInSeconds = startDelayInSeconds;
            return this;
        }

        /**
         * Describes how many threads to start during every iteration. Default is numberOfUsers value.
         */
        public Builder withSlewRateUsersPerSecond(long slewRateUsersPerSecond) {
            this.slewRateUsersPerSecond = slewRateUsersPerSecond;
            return this;
        }
    }

    public long getNumberOfUsers() {
        return numberOfUsers;
    }

    public long getLifeTimeInSeconds() {
        return lifeTimeInSeconds;
    }

    public long getStartDelayInSeconds() {
        return startDelayInSeconds;
    }

    public long getSlewRateUsersPerSecond() {
        return slewRateUsersPerSecond;
    }
}
