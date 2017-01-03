package com.griddynamics.jagger.test.javabuilders.load;

import com.griddynamics.jagger.test.javabuilders.utils.JaggerPropertiesProvider;
import com.griddynamics.jagger.test.javabuilders.utils.LoadDurationMetric;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.*;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;

public class InvocationsLoadTests extends LoadTestsDefinition {
    private final int invocationsCount;
    private final double latency;
    private final JLimit LATENCY_LIMIT;
    private final int sleepDelay;

    public InvocationsLoadTests(JaggerPropertiesProvider provider) {
        super(provider);
        this.sleepDelay = provider.getIntPropertyValue("inv.sleep.delay");
        this.latency = (sleepDelay + provider.getIntPropertyValue("test.env.latency"))/1000.0;
        this.invocationsCount = provider.getIntPropertyValue("inv.count");
        this.LATENCY_LIMIT = deviationLimit(JMetricName.PERF_AVG_LATENCY, latency, 0.05, 0.1);
    }

    /**
     * Simple invocations load in one thread.
     * Make sure that
     * - result samples count equals to specified invocations count
     * - users are not more than 1 but about 1;
     * - test duration is about expected - latency*invocations
     */
    public JLoadTest testInvocationsInOneThread(){
        int threadsCount = 1;

        return test("Invocation in one thread",
                invocationLoad(invocationsCount, threadsCount).build(),
                iterTermination(invocationsCount),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                exactValueLimit(JMetricName.PERF_ITERATION_SAMPLES, invocationsCount),
                virtualUsersLimit(threadsCount),
                deviationLimit(JMetricName.PERF_DURATION, latency * invocationsCount, 0.05, 0.1));
    }

    /**
     * Simple invocations load in 5 thread.
     * Make sure that
     * - result samples count equals to specified invocations count
     * - users are not more than 5 but about 5;
     * - test duration is about expected - latency*invocations/5
     */
    public JLoadTest testInvocationsSeveralThreads(){
        int threadsCount = 5;

        // need to increase default invocations count to reduce the impact of starting and stopping of the load
        int invocationsCount = this.invocationsCount * 5;

        double expectedDuration = latency * invocationsCount / threadsCount;

        return test("Invocation in several thread",
                invocationLoad(invocationsCount, threadsCount).build(),
                iterTermination(invocationsCount),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                exactValueLimit(JMetricName.PERF_ITERATION_SAMPLES, invocationsCount),
                virtualUsersLimit(threadsCount),
                deviationLimit(JMetricName.PERF_DURATION, expectedDuration, 0.05, 0.1));
    }

    /**
     * Invocations load terminated by iterations count less than invocations count
     * Make sure that
     * - result samples count is about specified iterations count
     * - test duration is about expected - latency*iterations
     */
    public JLoadTest testInvocationsTerminatedByIterations(){
        int threadsCount = 1;
        int maxIterationToTerminate = invocationsCount*4/5;
        double expectedDuration = latency * maxIterationToTerminate / threadsCount;

        return test("Invocation terminated by iterations count less than invocations count",
                invocationLoad(invocationsCount, threadsCount).build(),
                iterTermination(maxIterationToTerminate),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, maxIterationToTerminate, 0.05, 0.1),
                virtualUsersLimit(threadsCount),
                deviationLimit(JMetricName.PERF_DURATION, expectedDuration, 0.05, 0.1));
    }


    /**
     * Invocations load terminated by duration before target invocations count reached.
     * Make sure that
     * - result samples count is less target invocations count
     * - test duration is about specified by limit
     */
    public JLoadTest testInvocationsTerminatedByDurationBeforeInvocationsReached(){
        int threadsCount = 1;
        double duration = latency * invocationsCount / threadsCount / 2;

        return test("Invocation terminated by duration before target invocations count reached",
                invocationLoad(invocationsCount, threadsCount).build(),
                durationTermination((long) duration),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                JLimitVsRefValue.builder(JMetricName.PERF_ITERATION_SAMPLES, RefValue.of((double) invocationsCount))
                        .withOnlyUpperThresholds(UpWarnThresh.of(0.9), UpErrThresh.of(1.0)).build(),
                virtualUsersLimit(threadsCount),
                deviationLimit(JMetricName.PERF_DURATION, duration, 0.05, 0.1));
    }

    /**
     * Invocations load terminated by duration after target invocations count reached.
     * Make sure that
     * - result samples count equals to specified invocations count
     * - test duration is about specified by limit
     */
    public JLoadTest testInvocationsTerminatedByDurationAfterInvocationsReached(){
        int threadsCount = 1;
        double duration = latency * invocationsCount * 1.25/ threadsCount;

        return test("Invocation terminated by duration after target invocations count reached",
                invocationLoad(invocationsCount, threadsCount).build(),
                durationTermination((long) duration),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                exactValueLimit(JMetricName.PERF_ITERATION_SAMPLES, invocationsCount),
                deviationLimit(JMetricName.PERF_DURATION, duration, 0.05, 0.1));
    }

    /**
     * Invocations load with delays between invocations.
     * Make sure that
     * - result samples count equals to specified invocations count
     * - test duration is about expected - (latency+delay)*invocations/threads
     */
    public JLoadTest testInvocationsWithDelayBetweenInvocations(){
        int threadsCount = 1;
        int delayBetweenInvocations = provider.getIntPropertyValue("inv.delay.between.invocations");
        int expectedDuration = 60;
        int targetInvCount = (int) Math.round(expectedDuration*threadsCount/(latency + delayBetweenInvocations/1000));

        return test("Invocation with delays between invocations",
                invocationLoad(targetInvCount, threadsCount)
                        .withDelayBetweenInvocationsInMilliseconds(delayBetweenInvocations).build(),
                iterTermination(targetInvCount),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                exactValueLimit(JMetricName.PERF_ITERATION_SAMPLES, targetInvCount),
                virtualUsersLimit(threadsCount),
                deviationLimit(JMetricName.PERF_DURATION, expectedDuration, 0.05, 0.1),
                deviationLimit(LoadDurationMetric.NAME, expectedDuration, 0.05, 0.1));
    }


    /**
     * Invocations load is repeated periodically when invocation time less load period and execution is terminated by
     * iterations count.
     *   load period
     * |----------|
     *  invocation
     *  time
     * |----|
     * _____       ______
     *      |     |
     *      |     |
     *      |_____|
     * Make sure that when load period = 2 * invocation time and termination by iteration count = 3*invocationsCount
     * - virtual users count = 1/2 threads count
     * - test duration = 5 invocations time
     */
    public JLoadTest testInvocationsWithPeriodLoadingMaxIterations(){
        int threadsCount = 5;
        int invocationTime = (int) (invocationsCount*latency/threadsCount);
        int loadPeriod = invocationTime*2;
        int iterationsCount = invocationsCount*3;
        int expectedDuration = 2*loadPeriod+invocationTime;

        return test("Invocation repeated periodically max iterations",
                invocationLoad(invocationsCount, threadsCount)
                        .withPeriodBetweenLoadInSeconds(loadPeriod).build(),
                iterTermination(iterationsCount),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                exactValueLimit(JMetricName.PERF_ITERATION_SAMPLES, iterationsCount),
                deviationLimit(JMetricName.PERF_VIRTUAL_USERS, threadsCount*3/5, 0.05, 0.1),
                deviationLimit(JMetricName.PERF_THROUGHPUT, iterationsCount/expectedDuration, 0.05, 0.1),
                deviationLimit(JMetricName.PERF_DURATION, expectedDuration, 0.05, 0.1));
    }

    /**
     * Invocations load is repeated periodically when invocation time less load period and execution is terminated by
     * duration.
     *   load period
     * |----------|
     *  invocation
     *  time
     * |----|
     * _____       ______
     *      |     |
     *      |     |
     *      |_____|
     * Make sure that when load period = 2 * invocation time and termination duration = 3.5 invocation time
     * - virtual users count = 1/2 threads count
     * - iteration count = 2 * invocations count
     */
    public JLoadTest testInvocationsWithPeriodLoadingMaxDuration(){
        int threadsCount = 5;
        int invocationTime = (int) (invocationsCount*latency/threadsCount);
        int loadPeriod = 2 * invocationTime;
        int duration = (int) (3.5 * invocationTime);
        int expectedIterationsCount = 2*invocationsCount;

        return test("Invocation repeated periodically max durations",
                invocationLoad(invocationsCount, threadsCount)
                        .withPeriodBetweenLoadInSeconds(loadPeriod).build(),
                durationTermination(duration),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                exactValueLimit(JMetricName.PERF_ITERATION_SAMPLES, expectedIterationsCount),
                deviationLimit(JMetricName.PERF_VIRTUAL_USERS, threadsCount*2/3.5, 0.05, 0.1),
                deviationLimit(JMetricName.PERF_DURATION, duration, 0.05, 0.1));
    }

    /**
     * Invocations load is repeated periodically when load period less invocation time
     * duration.
     *   load period
     * |----------|
     *  invocation
     *  time
     * |----|
     * _____       ______
     *      |     |
     *      |     |
     *      |_____|
     * Make sure that
     * - load period doesn't affect loading
     */
    public JLoadTest testInvocationsWithSmallerLoadingPeriod(){
        int threadsCount = 1;
        int invocationTime = (int) (invocationsCount*latency/threadsCount);
        int loadPeriod = (int) (0.5 * invocationTime);
        int iterationsCount = 2*invocationsCount;
        int expectedDuration = 2*invocationTime;

        return test("Invocation repeated periodically smaller period",
                invocationLoad(invocationsCount, threadsCount)
                        .withPeriodBetweenLoadInSeconds(loadPeriod).build(),
                iterTermination(iterationsCount),
                SUCCESS_LIMIT, LATENCY_LIMIT,
                deviationLimit(JMetricName.PERF_ITERATION_SAMPLES, iterationsCount, 0.01, 0.05),
                deviationLimit(JMetricName.PERF_VIRTUAL_USERS, threadsCount, 0.05, 0.1),
                deviationLimit(JMetricName.PERF_DURATION, expectedDuration, 0.05, 0.1));
    }

    private JLoadProfileInvocation.Builder invocationLoad(int count, int threads){
        return JLoadProfileInvocation.builder(InvocationCount.of(count), ThreadCount.of(threads));
    }

    private JLoadTest test(String id, JLoadProfile l, JTerminationCriteria t, JLimit... limits){
        return JLoadTest.builder(Id.of(id), sleepTestDefinition(sleepDelay), l, t).withLimits(limits).build();
    }

    private JLimit virtualUsersLimit(double expected){
        return JLimitVsRefValue.builder(JMetricName.PERF_VIRTUAL_USERS, RefValue.of(expected))
                .withExactLimits(LowErrThresh.of(0.9), LowWarnThresh.of(0.95), UpWarnThresh.of(1.0), UpErrThresh.of(1.0))
                .build();
    }

}
