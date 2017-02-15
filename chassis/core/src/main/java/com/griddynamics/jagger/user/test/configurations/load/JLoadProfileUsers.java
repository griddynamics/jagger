package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;

import java.util.Objects;


/**
 * This class represents a group of virtual users.
 * Available attributes:<p>
 *     - numberOfUsers - A goal number of threads.<p>
 *     - lifeTimeInSeconds - Describes how long threads will be alive. Default is 2 days.<p>
 *     - startDelayInSeconds - Delay before first thread will start. Default is 0.<p>
 *     - slewRateUsersPerSecond - Describes how many threads to start during every iteration. Default is numberOfUsers value.<p>
 *
 * Examples: @n
 * @code
 * JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(5)).withStartDelayInSeconds(10).build();
 * @endcode
 * @image html load_GroupWithDelay.png "User group load with start delay"
 * @n
 * @code
 * JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(20)).withSlewRateUsersPerSecond(1).build();
 * @endcode
 * @image html load_GroupSlewRate.png "User group load with slew rate"
 *
 * @ingroup Main_Load_profiles_group

 */
public class JLoadProfileUsers {

    private final long numberOfUsers;
    private final long lifeTimeInSeconds;
    private final long startDelayInSeconds;
    private final double slewRateUsersPerSecond;

    private JLoadProfileUsers(Builder builder) {
        this.numberOfUsers = builder.numberOfUsers.value();
        this.lifeTimeInSeconds = builder.lifeTimeInSeconds;
        this.startDelayInSeconds = builder.startDelayInSeconds;
        this.slewRateUsersPerSecond = builder.slewRateUsersPerSecond;
    }

    /** Builder of the JLoadProfileUsers
     * @n
     * @details Constructor parameters are mandatory for the JLoadProfileUsers. All parameters, set by setters are optional
     * @n
     * @param numberOfUsers   - The number of users in user group
     */
    public static Builder builder(NumberOfUsers numberOfUsers) {
        return new Builder(numberOfUsers);
    }

    public static class Builder {
        private final NumberOfUsers numberOfUsers;
        private long lifeTimeInSeconds;
        private long startDelayInSeconds;
        private double slewRateUsersPerSecond;

        /** Builder of the JLoadProfileUsers
         * @n
         * @details Constructor parameters are mandatory for the JLoadProfileUsers. All parameters, set by setters are optional
         * @n
         * @param numberOfUsers   - The number of users in user group
         */
        private Builder(NumberOfUsers numberOfUsers) {
            Objects.requireNonNull(numberOfUsers);

            this.numberOfUsers = numberOfUsers;
            this.lifeTimeInSeconds = 60 * 60 * 48; // 2 days
            this.slewRateUsersPerSecond = numberOfUsers.value();
        }

        /** Creates an object of JLoadProfileUsers type with custom parameters.
         * @return JLoadProfileUsers object.
         */
        public JLoadProfileUsers build() {
            return new JLoadProfileUsers(this);
        }

        /**
         * Optional: Life time in seconds. Default is 2 days.
         * @param lifeTimeInSeconds Describes how long threads will be alive
         */
        public Builder withLifeTimeInSeconds(long lifeTimeInSeconds) {
            if (lifeTimeInSeconds <= 0) {
                throw new IllegalArgumentException(String.format("Life time must be > 0. Provided value is %s", lifeTimeInSeconds));
            }
            this.lifeTimeInSeconds = lifeTimeInSeconds;
            return this;
        }

        /**
         * Optional: Start delay in secondsStart delay in seconds. Default is 0.
         * @param startDelayInSeconds Delay before first thread will start
         */
        public Builder withStartDelayInSeconds(long startDelayInSeconds) {
            if (startDelayInSeconds < 0) {
                throw new IllegalArgumentException(String.format("Start delay must be >= 0. Provided value is %s", startDelayInSeconds));
            }
            this.startDelayInSeconds = startDelayInSeconds;
            return this;
        }

        /**
         * Optional: Slew rate users per second. Default is numberOfUsers value.
         * @param slewRateUsersPerSecond Describes how many threads to start during every iteration
         */
        public Builder withSlewRateUsersPerSecond(double slewRateUsersPerSecond) {
            if (slewRateUsersPerSecond <= 0) {
                throw new IllegalArgumentException(
                        String.format("Slew rate users per second must be > 0. Provided value is %s", slewRateUsersPerSecond));
            }
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

    public double getSlewRateUsersPerSecond() {
        return slewRateUsersPerSecond;
    }
}
