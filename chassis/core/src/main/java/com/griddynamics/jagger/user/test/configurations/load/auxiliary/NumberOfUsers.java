package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

/**
 * The number of virtual users for performing workload.
 */
public class NumberOfUsers {

    private final long numberOfUsers;

    private NumberOfUsers(long numberOfUsers) {
        if (numberOfUsers <= 0) {
            throw new IllegalArgumentException(String.format("Number of users must be > 0. Provided value is %s", numberOfUsers));
        }
        this.numberOfUsers = numberOfUsers;
    }

    public long value() {
        return numberOfUsers;
    }

    public static NumberOfUsers of(long numberOfUsers) { return new NumberOfUsers(numberOfUsers); }
}
