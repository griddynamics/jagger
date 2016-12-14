package com.griddynamics.jagger.engine.e1.scenario;

import com.griddynamics.jagger.util.SystemClock;

import java.math.BigDecimal;

/**
 * Clock Configuration for Qps load
 */
public class QpsClockConfiguration extends AbstractRateClockConfiguration {

    @Override
    protected WorkloadClock getRateClock(int tickInterval, TpsRouter tpsRouter, WorkloadSuggestionMaker workloadSuggestionMaker, SystemClock systemClock, int maxThreadNumber) {

        return new QpsClock(tickInterval, maxThreadNumber, createDesiredTps(BigDecimal.valueOf(getTps())));
    }

    @Override
    public String toString() {
        if (isRumpUp()){
            return getTps() + " rump-up rps";
        }
        return getTps() + " rps";
    }
}
