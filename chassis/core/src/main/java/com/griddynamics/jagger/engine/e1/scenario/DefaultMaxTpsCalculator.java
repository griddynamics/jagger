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

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.griddynamics.jagger.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultMaxTpsCalculator implements MaxTpsCalculator {
    private static final Logger log = LoggerFactory.getLogger(DefaultMaxTpsCalculator.class);

    private final static int SAMPLE_SIZE = 4;

    @Override
    public BigDecimal getMaxTps(NodeTpsStatistics stats) {
        Table<Integer, Integer, Pair<Long, BigDecimal>> threadDelayStats = stats.getThreadDelayStats();


        Map<Integer, Pair<Long, BigDecimal>> threadsTps = threadDelayStats.column(0);
        log.debug("Going to calculate max tps for {}", threadsTps);
        List<Integer> threads = Lists.newLinkedList(threadsTps.keySet());

        if (threads.size() < SAMPLE_SIZE) {
            log.debug("Not enough samples to guess a tps max on node");
            return null;
        }

        Collections.sort(threads);

        BigDecimal max = null;
        boolean isNonIncreasing = true;


        int start = threads.size() - SAMPLE_SIZE;
        int end = threads.size();

        BigDecimal previous = null;
        for (int i = start; i < end; i++) {
            Integer threadCount = threads.get(i);

            BigDecimal tps = threadsTps.get(threadCount).getSecond();

            if (previous == null) {
                max = tps;
                previous = tps;
            }

            if (tps.compareTo(previous) > 0) {
                isNonIncreasing = false;
                break;
            }

            if (previous.compareTo(max) > 0) {
                max = previous;
            }
        }

        if (!isNonIncreasing) {
            log.debug("Cannot guess max tps. According to stats tps is increasing.");
            return null;
        }

        return max;
    }
}
