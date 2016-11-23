package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.FixedDelay;
import com.griddynamics.jagger.engine.e1.scenario.RpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.UserGroupsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.user.ProcessingConfig.Test.Task.User;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
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
            clockConfiguration = new RpsClockConfiguration();
            ((RpsClockConfiguration) clockConfiguration).setValue(((JLoadProfileRps) jLoadProfile).getRequestsPerSecond());
            ((RpsClockConfiguration) clockConfiguration).setWarmUpTime(((JLoadProfileRps) jLoadProfile).getWarmUpTimeInSeconds());
            ((RpsClockConfiguration) clockConfiguration).setMaxThreadNumber((int) ((JLoadProfileRps) jLoadProfile).getMaxLoadThreads());
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
        }
        return clockConfiguration;
    }
}
