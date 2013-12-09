package com.griddynamics.jagger.reporting.interval;

/**
 * @author Nikolay Musienko
 *         Date: 12/9/13
 */

public class FixedIntervalSizeProvider implements IntervalSizeProvider{

    private final int interval;

    public FixedIntervalSizeProvider(int interval) {
        this.interval = interval;
    }


    @Override
    public int getIntervalSize(long minTime, long maxTime) {
        return interval;
    }
}
