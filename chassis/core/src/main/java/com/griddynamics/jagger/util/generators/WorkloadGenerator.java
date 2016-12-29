package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.ExactInvocationsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.FixedDelay;
import com.griddynamics.jagger.engine.e1.scenario.QpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.UserGroupsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.user.ProcessingConfig.Test.Task.User;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link WorkloadClockConfiguration} entity from user-defined {@link JLoadProfile} entity.
 */
class WorkloadGenerator {

    static WorkloadClockConfiguration generateLoad(JLoadProfile jLoadProfile) {
        WorkloadClockConfiguration clockConfiguration = null;
        if (jLoadProfile instanceof JLoadProfileRps) {
            clockConfiguration = generateRps((JLoadProfileRps) jLoadProfile);
        } else if (jLoadProfile instanceof JLoadProfileUserGroups) {
            clockConfiguration = generateUserGroup((JLoadProfileUserGroups) jLoadProfile);
        } else if (jLoadProfile instanceof JLoadProfileInvocation) {
            clockConfiguration = generateInvocation((JLoadProfileInvocation) jLoadProfile);
        }
        return clockConfiguration;
    }


    private static WorkloadClockConfiguration generateRps(JLoadProfileRps jLoadProfile) {
        QpsClockConfiguration qpsClockConfiguration = new QpsClockConfiguration();
        qpsClockConfiguration.setValue(jLoadProfile.getRequestsPerSecond());
        qpsClockConfiguration.setWarmUpTime(jLoadProfile.getWarmUpTimeInSeconds());
        qpsClockConfiguration.setMaxThreadNumber((int) jLoadProfile.getMaxLoadThreads());
        qpsClockConfiguration.setTickInterval(jLoadProfile.getTickInterval());
        return qpsClockConfiguration;
    }

    private static WorkloadClockConfiguration generateUserGroup(JLoadProfileUserGroups jLoadProfile) {
        List<User> users = jLoadProfile.getUserGroups().stream()
                .map(userGroup -> new User(String.valueOf(userGroup.getNumberOfUsers()), String.valueOf(userGroup.getSlewRateUsersPerSecond()),
                        userGroup.getStartDelayInSeconds() + "s", "1s", userGroup.getLifeTimeInSeconds() + "s"))
                .collect(toList());

        UserGroupsClockConfiguration userGroupsClockConfiguration = new UserGroupsClockConfiguration();
        userGroupsClockConfiguration.setUsers(users);
        userGroupsClockConfiguration.setDelay(new FixedDelay(jLoadProfile.getDelayBetweenInvocationsInMilliseconds()));
        userGroupsClockConfiguration.setTickInterval(jLoadProfile.getTickInterval());
        return userGroupsClockConfiguration;
    }

    private static WorkloadClockConfiguration generateInvocation(JLoadProfileInvocation jLoadProfile) {
        ExactInvocationsClockConfiguration exactInvocationsClockConfiguration = new ExactInvocationsClockConfiguration();
        exactInvocationsClockConfiguration.setSamplesCount(jLoadProfile.getInvocationCount());
        exactInvocationsClockConfiguration.setThreads(jLoadProfile.getThreadCount());
        exactInvocationsClockConfiguration.setDelay(jLoadProfile.getDelayBetweenInvocationsInMilliseconds());
        String period = jLoadProfile.getPeriodInSeconds() > 0 ? jLoadProfile.getPeriodInSeconds() + "s" : ExactInvocationsClockConfiguration.DEFAULT_PERIOD;
        exactInvocationsClockConfiguration.setPeriod(period);
        exactInvocationsClockConfiguration.setTickInterval(jLoadProfile.getTickInterval());
        return exactInvocationsClockConfiguration;
    }
}
