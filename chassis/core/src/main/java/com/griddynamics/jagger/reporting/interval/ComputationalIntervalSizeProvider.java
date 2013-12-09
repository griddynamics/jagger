package com.griddynamics.jagger.reporting.interval;

/**
 * @author Nikolay Musienko
 *         Date: 12/9/13
 */

public class ComputationalIntervalSizeProvider implements IntervalSizeProvider {

    private final int intervalsCount;

    public ComputationalIntervalSizeProvider(int intervalsCount) {
        this.intervalsCount = intervalsCount;
    }

    @Override
    public int getIntervalSize(long minTime, long maxTime) {
        return (int) ((maxTime - minTime) / intervalsCount);
    }
}
