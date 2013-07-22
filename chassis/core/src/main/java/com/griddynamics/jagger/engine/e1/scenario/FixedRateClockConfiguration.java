package com.griddynamics.jagger.engine.e1.scenario;

public class FixedRateClockConfiguration implements WorkloadClockConfiguration {

    private int tickInterval;
    private int rate;
    private int threadCount;
    private int delay;

    public FixedRateClockConfiguration() {}

    @Override
    public WorkloadClock getClock() {
        return new FixedRateClock(tickInterval, rate, threadCount, delay);
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
