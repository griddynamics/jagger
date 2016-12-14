/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.util.TimeUtils;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import static com.griddynamics.jagger.util.DecimalUtil.areEqual;

public class DefaultWorkloadSuggestionMaker implements WorkloadSuggestionMaker {
    private static final Logger log = LoggerFactory.getLogger(DefaultWorkloadSuggestionMaker.class);

    private static final WorkloadConfiguration FIRST_POINT_CONFIGURATION = WorkloadConfiguration.with(1, 0);
    private static final int MIN_DELAY = 10;
    // critical. if delay is too long we are not able to stop threads fast during balancing =>
    // change workload configuration fails by timeout
    private static final int MAX_DELAY = 1000;

    private final int maxDiff;

    public DefaultWorkloadSuggestionMaker(int maxDiff) {
        this.maxDiff = maxDiff;
    }

    @Override
    public WorkloadConfiguration suggest(BigDecimal desiredTps, NodeTpsStatistics statistics, int maxThreads) {
        log.debug("Going to suggest workload configuration. desired tps {}. statistics {}", desiredTps, statistics);

        Table<Integer, Integer, Pair<Long, BigDecimal>> threadDelayStats = statistics.getThreadDelayStats();

        if(areEqual(desiredTps, BigDecimal.ZERO)) {
            return WorkloadConfiguration.with(0, 0);
        }

        if (threadDelayStats.isEmpty()) {
            throw new IllegalArgumentException("Cannot suggest workload configuration");
        }

        if (!threadDelayStats.contains(FIRST_POINT_CONFIGURATION.getThreads(), FIRST_POINT_CONFIGURATION.getDelay())) {
            log.debug("Statistics is empty. Injecting empty entry");
            return FIRST_POINT_CONFIGURATION;
        }
        if (threadDelayStats.size() == 2 && areEqual(threadDelayStats.get(1, 0).getSecond(), BigDecimal.ZERO)) {
            log.warn("Statistics is still empty. Injecting empty entry");
            return FIRST_POINT_CONFIGURATION;
        }

        Map<Integer, Pair<Long, BigDecimal>> noDelays = threadDelayStats.column(0);

        log.debug("Calculate next thread count");
        Integer threadCount = findClosestPoint(desiredTps, noDelays);

        if (threadCount == 0) {
            threadCount = 1;
        }

        if (threadCount > maxThreads) {
            log.warn("{} calculated max {} allowed", threadCount, maxThreads);
            threadCount = maxThreads;
        }

        int currentThreads = statistics.getCurrentWorkloadConfiguration().getThreads();
        int diff = threadCount - currentThreads;
        if (diff > maxDiff) {
            log.debug("Increasing to {} is required current thread count is {} max allowed diff is {}", new Object[]{threadCount, currentThreads, maxDiff});
            return WorkloadConfiguration.with(currentThreads + maxDiff, 0);
        }

        diff = currentThreads - threadCount;
        if (diff > maxDiff) {
            log.debug("Decreasing to {} is required current thread count is {} max allowed diff is {}", new Object[]{threadCount, currentThreads, maxDiff});
            if ((currentThreads - maxDiff) > 1) {
                return WorkloadConfiguration.with(currentThreads - maxDiff, 0);
            }
            else {
                return WorkloadConfiguration.with(1, 0);
            }
        }

        if (!threadDelayStats.contains(threadCount, 0)) {
            return WorkloadConfiguration.with(threadCount, 0);
        }

        // <delay, <timestamp,tps>>
        Map<Integer, Pair<Long, BigDecimal>> delays = threadDelayStats.row(threadCount);

        // not enough statistics to calculate
        if (delays.size() == 1) {
            int delay = 0;
            BigDecimal tpsFromStat = delays.get(0).getSecond();

            // try to guess
            // tpsFromStat can be zero if no statistics was captured till this time
            if ((tpsFromStat.compareTo(BigDecimal.ZERO) > 0) &&
                    (desiredTps.compareTo(BigDecimal.ZERO) > 0)) {

                BigDecimal oneSecond = new BigDecimal(TimeUtils.secondsToMillis(1));
                BigDecimal result = oneSecond.multiply(new BigDecimal(threadCount)).divide(desiredTps, 3, BigDecimal.ROUND_HALF_UP);
                result = result.subtract(oneSecond.multiply(new BigDecimal(threadCount)).divide(tpsFromStat, 3, BigDecimal.ROUND_HALF_UP));

                delay = result.intValue();
            }
            // to have some non zero point in statistics
            if (delay == 0) {
                delay = MIN_DELAY;
            }

            delay = checkDelayInRange(delay);
            return WorkloadConfiguration.with(threadCount, delay);
        }

        log.debug("Calculate next delay");
        Integer delay = findClosestPoint(desiredTps, threadDelayStats.row(threadCount));

        delay = checkDelayInRange(delay);
        return WorkloadConfiguration.with(threadCount, delay);

    }

    private static Integer findClosestPoint(BigDecimal desiredTps, Map<Integer, Pair<Long, BigDecimal>> stats) {
        final int MAX_POINTS_FOR_REGRESSION = 10;

        SortedMap<Long, Integer> map = Maps.newTreeMap(new Comparator<Long>() {
            @Override
            public int compare(Long first, Long second) {
                return second.compareTo(first);
            }
        });
        for (Map.Entry<Integer, Pair<Long, BigDecimal>> entry : stats.entrySet()) {
            map.put(entry.getValue().getFirst(), entry.getKey());
        }

        if (map.size() < 2) {
            throw new IllegalArgumentException("Not enough stats to calculate point");
        }

        // <time><number of threads> - sorted by time
        Iterator<Map.Entry<Long, Integer>> iterator = map.entrySet().iterator();

        SimpleRegression regression = new SimpleRegression();
        Integer tempIndex;
        double previousValue = -1.0;
        double value;
        double measuredTps;

        log.debug("Selecting next point for balancing");
        int indx = 0;
        while (iterator.hasNext()) {

            tempIndex = iterator.next().getValue();

            if (previousValue < 0.0) {
                previousValue = tempIndex.floatValue();
            }
            value = tempIndex.floatValue();
            measuredTps = stats.get(tempIndex).getSecond().floatValue();

            regression.addData(value, measuredTps);

            log.debug(String.format("   %7.2f    %7.2f",value,measuredTps));

            indx++;
            if (indx > MAX_POINTS_FOR_REGRESSION) {
                break;
            }
        }

        double intercept = regression.getIntercept();
        double slope = regression.getSlope();

        double approxPoint;

        // if no slope => use previous number of threads
        if (Math.abs(slope) > 1e-12) {
            approxPoint = (desiredTps.doubleValue() - intercept) / slope;
        } else {
            approxPoint = previousValue;
        }

        // if approximation point is negative - ignore it
        if (approxPoint < 0) {
            approxPoint = previousValue;
        }

        log.debug(String.format("Next point   %7d    (target tps: %7.2f)",(int)Math.round(approxPoint),desiredTps.doubleValue()));

        return (int)Math.round(approxPoint);
    }

    private static int checkDelayInRange(int delay) {
        if (delay < 0) {
            delay = 0;
        }
        if (delay > MAX_DELAY) {
            delay = MAX_DELAY;
        }
        return delay;
    }

}
