package com.griddynamics.jagger.test.javabuilders.load;

import com.griddynamics.jagger.test.javabuilders.utils.JaggerPropertiesProvider;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;


public class RpsLoadTests extends LoadTestsDefinition{

    private final double latency;
    private final int sleepDelay;
    private final int testDuration;
    private final int rps;
    private final JLimit LATENCY_LIMIT;
    private final JLimit DURATION_LIMIT;

    public RpsLoadTests(JaggerPropertiesProvider provider) {
        super(provider);
        sleepDelay = provider.getIntPropertyValue("rps.sleep.delay");
        testDuration = provider.getIntPropertyValue("rps.test.duration");
        rps = provider.getIntPropertyValue("rps.test.rps");
        latency = (sleepDelay + provider.getIntPropertyValue("test.env.latency"))/1000.0;
        LATENCY_LIMIT = deviationLimit(JMetricName.PERF_AVG_LATENCY, latency, 0.05, 0.1);
        DURATION_LIMIT = deviationLimit(JMetricName.PERF_DURATION, testDuration, 0.05, 0.1);
    }


    /**
     * With static latency make sure that:
     * - test execution is stopped after specified amount of sec
     * - jagger generate load with specified rate of RPS
     * - amount of threads and iterations count is as expected
     */
    public JLoadTest testRpsLoadWithDurationTermination() {
        double expectedUsersCount = rps * latency;
        int expectedIterations = rps * testDuration;

        return test("simple rps Load terminated by duration",
                rpsLoad(rps).build(),
                durationTermination(testDuration),
                deviationLimit(JMetricName.PERF_THROUGHPUT, rps, 0.05, 0.1),
                deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterations, 0.05, 0.1),
                userLimit(expectedUsersCount),
                LATENCY_LIMIT, DURATION_LIMIT, SUCCESS_LIMIT);
    }

    /**
     * With static latency make sure that:
     * - test execution is stopped after specified amount of iterations
     * - jagger generate load with specified rate of RPS
     * - amount of threads and iterations count is as expected
     */
    public JLoadTest testRpsLoadWithIterationTermination() {
        double expectedUsersCount = rps * latency;
        int iterationsCount = rps * testDuration;

        return test("simple rps Load terminated by iterations",
                rpsLoad(rps).build(),
                iterTermination(iterationsCount),
                deviationLimit(JMetricName.PERF_THROUGHPUT, rps, 0.05, 0.1),
                deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, iterationsCount, 0.05, 0.1),
                userLimit(expectedUsersCount),
                LATENCY_LIMIT, DURATION_LIMIT, SUCCESS_LIMIT);
    }

    /**
     * With static latency make sure that when max threads less than needed:
     * - jagger generate max possible rate of RPS
     * - amount of threads and iterations count is as expected
     */
    public JLoadTest testRpsLoadLimitThreads() {
        int threadsCount = 10;
        double expectedRps = threadsCount / latency;
        int requestedRps = (int) (expectedRps * 2);

        double expectedIterationsCount = expectedRps * testDuration;

        return test("simple rps Load with max threads",
                rpsLoad(requestedRps)
                        .withMaxLoadThreads(threadsCount).build(),
                durationTermination(testDuration),
                deviationLimit(JMetricName.PERF_THROUGHPUT, expectedRps, 0.05, 0.1),
                deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterationsCount, 0.05, 0.1),
                userLimit(threadsCount),
                LATENCY_LIMIT, DURATION_LIMIT, SUCCESS_LIMIT);
    }

    /**
     * With static latency make sure that:
     * - test execution is stopped after specified amount of iterations
     * - jagger generate load with specified rate of RPS
     * - amount of threads and iterations count is as expected
     */
    public JLoadTest testRpsLoadWithWarmUp() {
        long warmUp = testDuration/4;

        double expectedRps = 3*rps/4 + rps/2/4;
        double expectedUsersCount = expectedRps * latency;
        double expectedIterations = expectedRps * testDuration;

        return test("simple rps Load with warm up",
                rpsLoad(rps)
                        .withWarmUpTimeInMilliseconds(warmUp * 1000).build(),
                durationTermination(testDuration),
                deviationLimit(JMetricName.PERF_THROUGHPUT, expectedRps, 0.05, 0.1),
                deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterations, 0.05, 0.1),
                userLimit(expectedUsersCount),
                LATENCY_LIMIT, DURATION_LIMIT, SUCCESS_LIMIT);
    }

    /**
     * make sure that jagger provides a specified rps when latency is not stable
     */
    public JLoadTest testRpsBalancingPulse(){
        int maxDelay = sleepDelay * 2;
        int period = testDuration*1000;
        double expectedUsersCount = rps * latency;
        double expectedIterations = rps * testDuration;

        return JLoadTest.builder(Id.of("rps balancing with pulse delay"),
                testDefinition("PulseLoad"+period+"x"+maxDelay, "/sleep/pulse/"+period+"/"+maxDelay),
                rpsLoad(rps).build(),
                durationTermination(testDuration))
                .withLimits(
                        deviationLimit(JMetricName.PERF_THROUGHPUT, rps, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterations, 0.05, 0.1),
                        userLimit(expectedUsersCount),
                        LATENCY_LIMIT, DURATION_LIMIT, SUCCESS_LIMIT).build();
    }

    /**
     * make sure that jagger provides a specified rps when latency is not stable
     */
    public JLoadTest testRpsBalancingRnd(){
        int maxDelay = sleepDelay * 2;
        int minDelay = sleepDelay/2;
        double latency = (maxDelay + minDelay)/2000.0;

        double expectedUsersCount = rps * latency;
        double expectedIterations = rps * testDuration;

        return JLoadTest.builder(Id.of("rps balancing with random delay"),
                testDefinition("RandomLoad"+minDelay+"-"+maxDelay, "/sleep/"+minDelay+"-"+maxDelay),
                rpsLoad(rps).build(),
                durationTermination(testDuration))
                .withLimits(
                        deviationLimit(JMetricName.PERF_AVG_LATENCY, latency, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_THROUGHPUT, rps, 0.05, 0.1),
                        deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterations, 0.05, 0.1),
                        userLimit(expectedUsersCount),
                        DURATION_LIMIT, SUCCESS_LIMIT).build();
    }


    private JLoadProfileRps.Builder rpsLoad(long rps){
        return JLoadProfileRps.builder(RequestsPerSecond.of(rps));
    }

    private JLoadTest test(String id, JLoadProfile l, JTerminationCriteria t, JLimit... limits){
        return JLoadTest.builder(Id.of(id), sleepTestDefinition(sleepDelay), l, t).withLimits(limits).build();
    }

    private JLimit userLimit(double expectedUsersCount) {
        return absoluteDeviationLimit(JMetricName.PERF_VIRTUAL_USERS, expectedUsersCount, 1, 1.5);
    }

}
