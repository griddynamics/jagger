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
            JLoadProfileRps loadProfileRps = (JLoadProfileRps) jLoadProfile;
            QpsClockConfiguration qpsClockConfiguration = new QpsClockConfiguration();
            qpsClockConfiguration.setValue(loadProfileRps.getRequestsPerSecond());
            qpsClockConfiguration.setWarmUpTime(loadProfileRps.getWarmUpTimeInSeconds());
            qpsClockConfiguration.setMaxThreadNumber((int) loadProfileRps.getMaxLoadThreads());
            qpsClockConfiguration.setTickInterval(loadProfileRps.getTickInterval());
            clockConfiguration = qpsClockConfiguration;
        } else if (jLoadProfile instanceof JLoadProfileUserGroups) {
            JLoadProfileUserGroups profileUserGroups = (JLoadProfileUserGroups) jLoadProfile;
            List<User> users = profileUserGroups.getUserGroups().stream()
                    .map(userGroup -> new User(String.valueOf(userGroup.getNumberOfUsers()), String.valueOf(userGroup.getSlewRateUsersPerSecond()),
                            userGroup.getStartDelayInSeconds() + "s", "1s", userGroup.getLifeTimeInSeconds() + "s"))
                    .collect(toList());

            UserGroupsClockConfiguration userGroupsClockConfiguration = new UserGroupsClockConfiguration();
            userGroupsClockConfiguration.setUsers(users);
            userGroupsClockConfiguration.setDelay(new FixedDelay(profileUserGroups.getDelayBetweenInvocationsInSeconds()));
            userGroupsClockConfiguration.setTickInterval(profileUserGroups.getTickInterval());
            clockConfiguration = userGroupsClockConfiguration;
        } else if (jLoadProfile instanceof JLoadProfileInvocation) {
            JLoadProfileInvocation loadProfileInvocation = (JLoadProfileInvocation) jLoadProfile;
            ExactInvocationsClockConfiguration exactInvocationsClockConfiguration = new ExactInvocationsClockConfiguration();
            exactInvocationsClockConfiguration.setSamplesCount(loadProfileInvocation.getInvocationCount());
            exactInvocationsClockConfiguration.setThreads(loadProfileInvocation.getThreadCount());
            exactInvocationsClockConfiguration.setDelay(loadProfileInvocation.getDelay());
            String period = String.valueOf(loadProfileInvocation.getPeriod());
            exactInvocationsClockConfiguration.setPeriod("-1".equals(period) ? "-1" :
                    loadProfileInvocation.getPeriod() + "s");

            exactInvocationsClockConfiguration.setTickInterval(loadProfileInvocation.getTickInterval());
            clockConfiguration = exactInvocationsClockConfiguration;
        }
        return clockConfiguration;
    }
}
