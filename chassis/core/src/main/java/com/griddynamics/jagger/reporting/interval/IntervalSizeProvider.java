package com.griddynamics.jagger.reporting.interval;

/**
 * @author Nikolay Musienko
 *         Date: 12/9/13
 */

public interface IntervalSizeProvider {
    public int getIntervalSize(long minTime, long maxTime);
}
