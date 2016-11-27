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

import com.google.common.collect.*;
import com.griddynamics.jagger.util.Pair;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Queue;

public class NodeTpsRecorder implements NodeTpsStatistics {
    private final Map<Long, Pair<WorkloadConfiguration, BigDecimal>> tpsTimeHistory = Maps.newTreeMap();
    private final Table<Integer, Integer, Pair<Long, BigDecimal>> tpsTable;
    private BigDecimal currentTps = BigDecimal.ZERO;
    private WorkloadConfiguration currentConfiguration = WorkloadConfiguration.zero();
    private Long lastRecordedTime;
    private Long currentSamples = 0l;
    private final int limit;

    private Queue<WorkloadConfiguration> queue = Lists.newLinkedList();


    public NodeTpsRecorder(int limit) {
        tpsTable = HashBasedTable.create();
        this.limit = limit;
    }


    public void recordStatus(int threads, int delay, long samples, long pollTime) {
        currentTps = calculateTps(samples, pollTime);
        currentConfiguration = WorkloadConfiguration.with(threads, delay);
        lastRecordedTime = pollTime;
        currentSamples = samples;

        tpsTimeHistory.put(pollTime, Pair.of(currentConfiguration, currentTps));

        if (threads != 0 && delay != 0) {
            queue.add(currentConfiguration);
        }

        if (queue.size() >= limit) {
            WorkloadConfiguration outdatedConfiguration = queue.peek();
            tpsTable.remove(outdatedConfiguration.getThreads(), outdatedConfiguration.getDelay());
        }

        tpsTable.put(threads, delay, Pair.of(pollTime, currentTps));

    }

    private BigDecimal calculateTps(long samples, long pollTime) {
        BigDecimal tps = BigDecimal.ZERO;

        if (lastRecordedTime != null) {
            long interval = pollTime - lastRecordedTime;
            tps = new BigDecimal(samples - currentSamples).divide(new BigDecimal(interval), 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(1000));
        }

        return tps;
    }

    @Override
    public WorkloadConfiguration getCurrentWorkloadConfiguration() {
        return currentConfiguration;
    }

    @Override
    public BigDecimal getCurrentTps() {
        return currentTps;
    }

    @Override
    public Long getCurrentSamples() {
        return currentSamples;
    }

    @Override
    public Long getLastRecordedTime() {
        return lastRecordedTime;
    }

    @Override
    public Map<Long, Pair<WorkloadConfiguration, BigDecimal>> getTpsHistory() {
        return ImmutableMap.copyOf(tpsTimeHistory);
    }

    @Override
    public Table<Integer, Integer, Pair<Long, BigDecimal>> getThreadDelayStats() {
        return tpsTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeTpsRecorder that = (NodeTpsRecorder) o;

        if (currentConfiguration != null ? !currentConfiguration.equals(that.currentConfiguration) : that.currentConfiguration != null)
            return false;
        if (currentSamples != null ? !currentSamples.equals(that.currentSamples) : that.currentSamples != null)
            return false;
        if (currentTps != null ? !currentTps.equals(that.currentTps) : that.currentTps != null) return false;
        if (lastRecordedTime != null ? !lastRecordedTime.equals(that.lastRecordedTime) : that.lastRecordedTime != null)
            return false;
        if (tpsTable != null ? !tpsTable.equals(that.tpsTable) : that.tpsTable != null) return false;
        if (tpsTimeHistory != null ? !tpsTimeHistory.equals(that.tpsTimeHistory) : that.tpsTimeHistory != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tpsTimeHistory != null ? tpsTimeHistory.hashCode() : 0;
        result = 31 * result + (tpsTable != null ? tpsTable.hashCode() : 0);
        result = 31 * result + (currentTps != null ? currentTps.hashCode() : 0);
        result = 31 * result + (currentConfiguration != null ? currentConfiguration.hashCode() : 0);
        result = 31 * result + (lastRecordedTime != null ? lastRecordedTime.hashCode() : 0);
        result = 31 * result + (currentSamples != null ? currentSamples.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NodeTpsRecorder" +
                "\n{" +
                "\n\t tpsTimeHistory=" + tpsTimeHistory +
                "\n\t tpsTable=" + tpsTable +
                "\n\t currentTps=" + currentTps +
                "\n\t currentConfiguration=" + currentConfiguration +
                "\n\t lastRecordedTime=" + lastRecordedTime +
                "\n\t currentSamples=" + currentSamples +
                "\n\t queue=" + queue +
                "\n\t limit=" + limit +
                "\n}";
    }
}
