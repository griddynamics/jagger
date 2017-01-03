package com.griddynamics.jagger.test.javabuilders.load;

import com.griddynamics.jagger.test.javabuilders.utils.JaggerPropertiesProvider;
import com.griddynamics.jagger.test.javabuilders.utils.LoadDurationMetric;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;

public class UserGroupsLoadTests extends LoadTestsDefinition {
    private final Integer sleepDelay;
    private final double latency;
    private final Integer testDuration;
    private final JLimit DURATION_LIMIT;
    private JLimit LATENCY_LIMIT;

    public UserGroupsLoadTests(JaggerPropertiesProvider provider) {
        super(provider);
        sleepDelay = provider.getIntPropertyValue("group.sleep.delay");
        latency = (sleepDelay + provider.getIntPropertyValue("test.env.latency")) / 1000.0;
        testDuration = provider.getIntPropertyValue("group.test.duration");
        LATENCY_LIMIT = deviationLimit(JMetricName.PERF_AVG_LATENCY, latency, 0.05, 0.1);
        DURATION_LIMIT = deviationLimit(JMetricName.PERF_DURATION, testDuration, 0.05, 0.1);
    }

    /**
     * Check one user in one group create load with
     * - one virtual user
     * - expected iterations count
     * - expected throughput
     */
    public JLoadTest oneUserOneGroup() {
        JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(1)).build();

        double expectedRps = 1.0 / latency;
        double expectedIterations = expectedRps * testDuration;

        return oneUserTest("oneUserOneGroup", u, expectedIterations, expectedRps, 1);
    }

    /**
     * Check 5 user in one group create load with
     * - 5 virtual user
     * - expected iterations count
     * - expected throughput
     */
    public JLoadTest severalUsersOneGroup() {
        JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(5)).build();

        double expectedRps = 5.0 / latency;
        double expectedIterations = expectedRps * testDuration;

        return oneUserTest("severalUsersOneGroup", u, expectedIterations, expectedRps, 5);
    }

    /**
     * make sure that group load is delayed for specified time and produce expected average
     * throughput and virtual users count.
     */
    public JLoadTest userGroupWithDelay() {
        int delay = testDuration / 6;
        int loadDuration = testDuration - delay;
        JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(5))
                .withStartDelayInSeconds(delay).build();

        double expectedRps = 5.0 / latency;
        double expectedIterations = expectedRps * loadDuration;
        double expectedUsers = 5.0 * loadDuration / testDuration;

        return oneUserTest("userGroupWithDelay", u, expectedIterations, expectedRps, expectedUsers, loadDuration);
    }

    /**
     * make sure that load stops when thread live time ends
     */
    public JLoadTest userGroupWithLifeTimeLessTestDuration() {
        int lifeTime = testDuration / 2;
        JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(2))
                .withLifeTimeInSeconds(lifeTime).build();
        double expectedRps = 2.0 / latency;

        double expectedIterations = expectedRps * lifeTime;
        double expectedUsers = 1;

        return oneUserTest("userGroupWithLifeTimeLessTestDuration", u, expectedIterations, expectedRps, expectedUsers, lifeTime);
    }

    /**
     * make sure that jagger correctly handle case when life time grater than test duration.
     * Load should be the same as if life time is not specified.
     */
    public JLoadTest userGroupWithLifeTimeGreaterTestDuration() {
        int lifeTime = testDuration * 2;
        JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(1))
                .withLifeTimeInSeconds(lifeTime).build();

        double expectedRps = 1.0 / latency;
        double expectedIterations = expectedRps * testDuration;

        return oneUserTest("userGroupWithLifeTimeGreaterTestDuration", u, expectedIterations, expectedRps, 1);
    }

    /**
     * make sure that the system provide correct slew rate with expected average parameters
     */
    public JLoadTest userGroupWithSlewRate() {
        int users = 20;
        int growthTime = testDuration / 3;
        int slewRate = users / growthTime;
        growthTime = slewRate * users;

        JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(users))
                .withSlewRateUsersPerSecond(slewRate).build();

        double possibleRps = users * 1.0 / latency;
        double expectedIterations = possibleRps * (testDuration - growthTime) + 0.5 * possibleRps * growthTime;
        double expectedRps = expectedIterations / testDuration;
        double expectedUsers = users * (1 - 0.5 * growthTime / testDuration);

        return oneUserTest("userGroupWithSlewRate", u, expectedIterations, expectedRps, expectedUsers);
    }

    /**
     * make sure that the system correctly handle load when load growing is terminated
     */
    public JLoadTest userGroupWithSlewRateTerminated() {
        int growthTime = testDuration * 2;
        int slewRate = 1;
        int users = growthTime * slewRate;

        JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(users))
                .withSlewRateUsersPerSecond(slewRate).build();

        double expectedUsers = 0.5 * slewRate * testDuration;
        double expectedRps = expectedUsers / latency;
        double expectedIterations = expectedRps * testDuration;

        return oneUserTest("userGroupWithSlewRateTerminated", u, expectedIterations, expectedRps, expectedUsers);
    }

    /**
     * make sure that load with delay between invocation works as expected.
     */
    public JLoadTest userGroupDelayBetweenInvocations(){
        int delay = 2;
        JLoadProfileUsers u = JLoadProfileUsers.builder(NumberOfUsers.of(1)).build();
        JLoadProfileUserGroups load = JLoadProfileUserGroups.builder(u)
                .withDelayBetweenInvocationsInSeconds(delay*1000).build();

        double invocationDuration = latency + delay;
        double expectedIterations = testDuration / invocationDuration;
        double expectedRps = 1/invocationDuration;

        return JLoadTest.builder(Id.of("userGroupDelayBetweenInvocations"),
                sleepTestDefinition(sleepDelay),
                load, durationTermination(testDuration))
                .withLimits(
                        deviationLimit(JMetricName.PERF_THROUGHPUT, expectedRps, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterations, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_VIRTUAL_USERS, 1, 0.05, 0.1),
                        DURATION_LIMIT, SUCCESS_LIMIT, LATENCY_LIMIT).build();
    }

    /**
     * make sure that the system correctly process load with few user profiles
     */
    public JLoadTest fewUserGroups(){
        JLoadProfileUsers u1 = JLoadProfileUsers.builder(NumberOfUsers.of(2)).build();
        JLoadProfileUsers u2 = JLoadProfileUsers.builder(NumberOfUsers.of(3)).build();

        double expectedRps = 5.0 / latency;
        double expectedIterations = expectedRps * testDuration;


        return JLoadTest.builder(Id.of("fewUserGroups"), sleepTestDefinition(sleepDelay),
                JLoadProfileUserGroups.builder(u1, u2).build(),
                durationTermination(testDuration))
                .withLimits(
                        deviationLimit(JMetricName.PERF_THROUGHPUT, expectedRps, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterations, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_VIRTUAL_USERS, 5, 0.05, 0.1),
                        DURATION_LIMIT, SUCCESS_LIMIT, LATENCY_LIMIT).build();
    }

    /**
     * create and check loading with complicated load profile.
     start                           end
     group 1
     |______________________        |
     |                              |
     group 2
     |        ______________________|
     |                              |
     group 3
     |   ________________________   |
     |  /                        \  |
     | /                          \ |
     |/                            \|
     group 4
     |        ______________________|
     |       /                      |
     |      /                       |
     |     /                        |
     */
    public JLoadTest userGroupsComplexLoad(){
        JLoadProfileUsers u1 = JLoadProfileUsers.builder(NumberOfUsers.of(5)).withLifeTimeInSeconds(50).build();
        JLoadProfileUsers u2 = JLoadProfileUsers.builder(NumberOfUsers.of(5)).withStartDelayInSeconds(5).build();
        JLoadProfileUsers u3 = JLoadProfileUsers.builder(NumberOfUsers.of(20)).withLifeTimeInSeconds(40).withSlewRateUsersPerSecond(1).build();
        JLoadProfileUsers u4 = JLoadProfileUsers.builder(NumberOfUsers.of(10)).withStartDelayInSeconds(10).withSlewRateUsersPerSecond(1).build();

        double it1 = 50 * 5/latency;
        double it2 = 55 * 5/latency;
        double it3 = 20 * 10/latency + 20 * 20/latency + 20 * 10/latency;
        double it4 = 40 * 10/latency + 10 * 5/latency;
        double expectedIterations = it1+it2+it3+it4;
        double expectedAvgUsers = expectedIterations*latency/testDuration;

        return JLoadTest.builder(Id.of("userGroupsComplexLoad"), sleepTestDefinition(sleepDelay),
                JLoadProfileUserGroups.builder(u1, u2, u3, u4).build(),
                durationTermination(testDuration))
                .withLimits(
                        deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterations, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_VIRTUAL_USERS, expectedAvgUsers, 0.05, 0.1),
                        DURATION_LIMIT, SUCCESS_LIMIT, LATENCY_LIMIT).build();
    }

    private JLoadTest oneUserTest(String id, JLoadProfileUsers u, double expectedIterations, double expectedRps,
                                  double expectedUsers, double expectedLoadDuration) {
        return JLoadTest.builder(Id.of(id), sleepTestDefinition(sleepDelay), JLoadProfileUserGroups.builder(u).build(),
                durationTermination(testDuration))
                .withLimits(
                        deviationLimit(LoadDurationMetric.NAME, expectedLoadDuration, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_THROUGHPUT, expectedRps, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterations, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_VIRTUAL_USERS, expectedUsers, 0.05, 0.1),
                        DURATION_LIMIT, SUCCESS_LIMIT, LATENCY_LIMIT).build();
    }

    private JLoadTest oneUserTest(String id, JLoadProfileUsers u, double expectedIterations, double expectedRps,
                                  double expectedUsers){
        return oneUserTest(id, u,expectedIterations,expectedRps, expectedUsers, testDuration);
    }

}
