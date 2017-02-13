package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.ExactInvocationsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.FixedDelay;
import com.griddynamics.jagger.engine.e1.scenario.QpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.UserGroupsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.user.ProcessingConfig.Test.Task.User;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileTps;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toList;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link WorkloadClockConfiguration} entity from user-defined {@link JLoadProfile} entity.
 */
class WorkloadGenerator {

    static WorkloadClockConfiguration generateLoad(JLoadProfile jLoadProfile, int defaultMaxThreads) {
        WorkloadClockConfiguration clockConfiguration = null;
        if (jLoadProfile instanceof JLoadProfileRps) {
            clockConfiguration = generateRps((JLoadProfileRps) jLoadProfile, defaultMaxThreads);
        } else if (jLoadProfile instanceof JLoadProfileUserGroups) {
            clockConfiguration = generateUserGroup((JLoadProfileUserGroups) jLoadProfile);
        } else if (jLoadProfile instanceof JLoadProfileInvocation) {
            clockConfiguration = generateInvocation((JLoadProfileInvocation) jLoadProfile);
        } else if (jLoadProfile instanceof JLoadProfileTps) {
            clockConfiguration = generateTps((JLoadProfileTps) jLoadProfile, defaultMaxThreads);
        }
        return clockConfiguration;
    }


    private static WorkloadClockConfiguration generateRps(JLoadProfileRps jLoadProfile, int defaultMaxThreads) {
        QpsClockConfiguration qpsClockConfiguration = new QpsClockConfiguration();
        qpsClockConfiguration.setValue(jLoadProfile.getRequestsPerSecond());
        qpsClockConfiguration.setWarmUpTime(jLoadProfile.getWarmUpTimeInMilliseconds());
        int maxLoadThreads = (int) jLoadProfile.getMaxLoadThreads();
        if (JLoadProfileRps.USE_DEFAULT_MAX_THREAD_COUNT == maxLoadThreads) {
            maxLoadThreads = defaultMaxThreads;
        }
        qpsClockConfiguration.setMaxThreadNumber(maxLoadThreads);
        qpsClockConfiguration.setTickInterval(jLoadProfile.getTickInterval());
        return qpsClockConfiguration;
    }

    private static WorkloadClockConfiguration generateTps(JLoadProfileTps jLoadProfile, int defaultMaxThreads) {
        TpsClockConfiguration tpsClockConfiguration = new TpsClockConfiguration();
        tpsClockConfiguration.setValue(jLoadProfile.getTransactionsPerSecond());
        tpsClockConfiguration.setWarmUpTime(jLoadProfile.getWarmUpTimeInMilliseconds());
        int maxLoadThreads = (int) jLoadProfile.getMaxLoadThreads();
        if (JLoadProfileTps.USE_DEFAULT_MAX_THREAD_COUNT == maxLoadThreads) {
            maxLoadThreads = defaultMaxThreads;
        }
        tpsClockConfiguration.setMaxThreadNumber(maxLoadThreads);
        tpsClockConfiguration.setTickInterval(jLoadProfile.getTickInterval());
        return tpsClockConfiguration;
    }

    private static WorkloadClockConfiguration generateUserGroup(JLoadProfileUserGroups jLoadProfile) {
        List<User> users = jLoadProfile.getUserGroups().stream()
                .map(userGroup -> new User(String.valueOf(userGroup.getNumberOfUsers()), userGroup.getSlewRateUsersPerSecond(),
                        userGroup.getStartDelayInSeconds() + "s", "1s", userGroup.getLifeTimeInSeconds() + "s"))
                .collect(toList());

        UserGroupsClockConfiguration userGroupsClockConfiguration = new UserGroupsClockConfiguration();
        userGroupsClockConfiguration.setUsers(users);
        userGroupsClockConfiguration.setShutdown(new AtomicBoolean());
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
