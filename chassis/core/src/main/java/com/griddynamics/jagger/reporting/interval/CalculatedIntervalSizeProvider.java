package com.griddynamics.jagger.reporting.interval;

/**
 * @author Nikolay Musienko
 *         Date: 12/9/13
 */

public class CalculatedIntervalSizeProvider implements IntervalSizeProvider {

    private final int intervalsCount;

    public CalculatedIntervalSizeProvider(int intervalsCount) {
        this.intervalsCount = intervalsCount;
    }

    @Override
    public int getIntervalSize(long minTime, long maxTime) {
        return (int) Math.ceil((1d * maxTime - minTime) / intervalsCount);
    }
}
