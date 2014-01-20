package com.griddynamics.jagger.engine.e1.scenario;

import com.griddynamics.jagger.util.Parser;

public class ExactInvocationsClockConfiguration implements WorkloadClockConfiguration {

    private int threadCount;

    private int samplesCount;

    private int delay;

    private String period;

    private int tickInterval = 1000;

    public void setPeriod(String period) {
        this.period = period;
    }

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

    public String getPeriod() {
        return period;
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
        return new ExactInvocationsClock(samplesCount, threadCount, delay, tickInterval, Parser.parseTimeMillis(period));
    }
}
