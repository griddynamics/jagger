package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.RpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link WorkloadClockConfiguration} entity from user-defined {@link JLoadProfile} entity.
 */
class WorkloadGenerator {

    static WorkloadClockConfiguration generateLoad(JLoadProfile jLoadProfile) {
        WorkloadClockConfiguration clockConfiguration = null;
        if (jLoadProfile instanceof JLoadProfileRps) {
            clockConfiguration = new RpsClockConfiguration();
            ((RpsClockConfiguration) clockConfiguration).setValue(((JLoadProfileRps) jLoadProfile).getRequestsPerSecond());
            ((RpsClockConfiguration) clockConfiguration).setWarmUpTime(((JLoadProfileRps) jLoadProfile).getWarmUpTimeInSeconds());
            ((RpsClockConfiguration) clockConfiguration).setMaxThreadNumber((int) ((JLoadProfileRps) jLoadProfile).getMaxLoadThreads());
        }
        return clockConfiguration;
    }
}
