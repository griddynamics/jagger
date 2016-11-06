package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.RpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JLoadRps;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link WorkloadClockConfiguration} entity from user-defined {@link JLoad} entity.
 */
class WorkloadGenerator {

    static WorkloadClockConfiguration generateLoad(JLoad jLoad) {
        WorkloadClockConfiguration clockConfiguration = null;
        if (jLoad instanceof JLoadRps) {
            clockConfiguration = new RpsClockConfiguration();
            ((RpsClockConfiguration) clockConfiguration).setValue(((JLoadRps) jLoad).getRequestsPerSecond());
            ((RpsClockConfiguration) clockConfiguration).setWarmUpTime(((JLoadRps) jLoad).getWarmUpTimeInSeconds());
            ((RpsClockConfiguration) clockConfiguration).setMaxThreadNumber((int) ((JLoadRps) jLoad).getMaxLoadThreads());
        }
        return clockConfiguration;
    }
}
