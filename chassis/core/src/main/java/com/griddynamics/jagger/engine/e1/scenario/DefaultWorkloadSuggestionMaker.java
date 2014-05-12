/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

    private static final WorkloadConfiguration CALIBRATION_CONFIGURATION = WorkloadConfiguration.with(1, 0);
    private static final int MIN_DELAY = 10;

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

        if (!threadDelayStats.contains(CALIBRATION_CONFIGURATION.getThreads(), CALIBRATION_CONFIGURATION.getDelay())) {
            log.debug("Statistics is empty. Going to return calibration info.");
            return CALIBRATION_CONFIGURATION;
        }
        if (threadDelayStats.size() == 2 && areEqual(threadDelayStats.get(1, 0).getSecond(), BigDecimal.ZERO)) {
            log.warn("No calibration info. Going to retry.");
            return CALIBRATION_CONFIGURATION;
        }

        Map<Integer, Pair<Long, BigDecimal>> noDelays = threadDelayStats.column(0);


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

        if (noDelays.containsKey(threadCount) && noDelays.get(threadCount).getSecond().compareTo(desiredTps) < 0) {
            if (log.isDebugEnabled()) {
                log.debug("Statistics for current point has been already calculated and it is less then desired one" +
                        "\nLook like we have achieved maximum for this node." +
                        "\nGoing to help max tps detector.");
            }
            int threads = currentThreads;
            if (threads < maxThreads) {
                threads++;
            }
            return WorkloadConfiguration.with(threads, 0);
        }

        if (!threadDelayStats.contains(threadCount, 0)) {
            return WorkloadConfiguration.with(threadCount, 0);
        }

        Map<Integer, Pair<Long, BigDecimal>> delays = threadDelayStats.row(threadCount);

        if (delays.size() == 1) {
            int delay = suggestDelay(delays.get(0).getSecond(), threadCount, desiredTps);

            return WorkloadConfiguration.with(threadCount, delay);
        }

        Integer delay = findClosestPoint(desiredTps, threadDelayStats.row(threadCount));


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
        double previousNumberOfThreads = -1.0;
        double numberOfThreads;
        double measuredTps;

        int indx = 0;
        while (iterator.hasNext()) {

            tempIndex = iterator.next().getValue();

            if (previousNumberOfThreads < 0.0) {
                previousNumberOfThreads = tempIndex.floatValue();
            }
            numberOfThreads = tempIndex.floatValue();
            measuredTps = stats.get(tempIndex).getSecond().floatValue();

            regression.addData(numberOfThreads, measuredTps);

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
            approxPoint = previousNumberOfThreads;
        }

        // if approximation point is negative - ignore it
        if (approxPoint < 0) {
            approxPoint = previousNumberOfThreads;
        }

        return (int)Math.round(approxPoint);
    }

    private static int suggestDelay(BigDecimal tpsFromStat, Integer threadCount, BigDecimal desiredTps) {
        BigDecimal oneSecond = new BigDecimal(TimeUtils.secondsToMillis(1));
        BigDecimal result = oneSecond.multiply(new BigDecimal(threadCount)).divide(desiredTps, 3, BigDecimal.ROUND_HALF_UP);
        result = result.subtract(oneSecond.multiply(new BigDecimal(threadCount)).divide(tpsFromStat, 3, BigDecimal.ROUND_HALF_UP));

        int i = result.intValue();
        if (i == 0) {
            i = MIN_DELAY;
        }
        return i;
    }

}
