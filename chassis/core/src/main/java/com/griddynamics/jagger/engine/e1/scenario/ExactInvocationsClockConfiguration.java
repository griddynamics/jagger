package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExactInvocationsClockConfiguration implements WorkloadClockConfiguration {

    private int threadCount;

    private int samplesCount;

    private int delay;

    private int tickInterval = 1000;

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setSamplesCount(int samplesCount) {
        this.samplesCount = samplesCount;
    }

    public void setExactcount(int samplesCount) {
        this.samplesCount = samplesCount;
    }

    public void setThreads(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public int getDelay() {
        return delay;
    }

    public int getSamplesCount() {
        return samplesCount;
    }

    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public WorkloadClock getClock() {
        return new ExactInvocationsClock(samplesCount, threadCount, delay, tickInterval);
    }
}
