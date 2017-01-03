package com.griddynamics.jagger.test.javabuilders.smoke_components;


import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;

/**
 * - For smoke tests of JLoadProfileRps each optional parameter should be specified and unspecified at least once.
 * - For smoke tests of JLoadProfileUserGroups should create load with one and several user groups with and without delay
 * between invocation. For JLoadProfileUsers each optional parameter should be specified and unspecified at least once.
 * - TPS, Invocation - TBD
 */
public class TestLoadVariations {
    public JLoadProfile oneRPSWithAllDefaults(){
        return JLoadProfileRps.builder(RequestsPerSecond.of(1)).build();
    }

    public JLoadProfile rpsFiveSecWarmUp(){
        return JLoadProfileRps.builder(RequestsPerSecond.of(10))
                .withWarmUpTimeInMilliseconds(5000)
                .build();
    }

    public JLoadProfile rpsWith0WarmUp(){
        return JLoadProfileRps.builder(RequestsPerSecond.of(1))
                //.withWarmUpTimeInMilliseconds(0) TODO uncomment when JFG-1009 will be fixed
                .build();
    }

    public JLoadProfile rpsOneThreadMax(){
        return JLoadProfileRps.builder(RequestsPerSecond.of(1))
                .withMaxLoadThreads(1)
                .build();
    }

    public JLoadProfile rpsAllFields(){
        return JLoadProfileRps.builder(RequestsPerSecond.of(100))
                .withMaxLoadThreads(10)
                .withWarmUpTimeInMilliseconds(2000)
                .build();
    }

    public JLoadProfile singleGroupAllDefaults(){
        JLoadProfileUsers group = JLoadProfileUsers.builder(NumberOfUsers.of(1)).build();
        return JLoadProfileUserGroups.builder(group).withDelayBetweenInvocationsInSeconds(1)
                .build();
    }

    public JLoadProfile severalGroupWithUsersVariations(){
        JLoadProfileUsers withLifeTime = JLoadProfileUsers.builder(NumberOfUsers.of(10))
                .withLifeTimeInSeconds(2)
                .build();
        JLoadProfileUsers withStartDelay = JLoadProfileUsers.builder(NumberOfUsers.of(50))
                .withStartDelayInSeconds(1)
                .build();
        JLoadProfileUsers withSlewRate = JLoadProfileUsers.builder(NumberOfUsers.of(20))
                .withSlewRateUsersPerSecond(5)
                .build();
        JLoadProfileUsers withAllFields = JLoadProfileUsers.builder(NumberOfUsers.of(40))
                .withLifeTimeInSeconds(5)
                .withSlewRateUsersPerSecond(5)
                .withStartDelayInSeconds(2)
                .build();

        return JLoadProfileUserGroups.builder(withLifeTime, withStartDelay, withSlewRate, withAllFields)
                .build();
    }



}
