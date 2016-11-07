package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationDuration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link TerminateStrategyConfiguration} entity from user-defined {@link JTermination} entity.
 */
class TerminationGenerator {

    static TerminateStrategyConfiguration generateTermination(JTermination jTermination) {
        TerminateStrategyConfiguration termination = null;
        if (jTermination instanceof JTerminationIterations) {
            termination = new IterationsOrDurationStrategyConfiguration();
            String duration = parseDuration(((JTerminationIterations) jTermination).getMaxDurationInSeconds());
            ((IterationsOrDurationStrategyConfiguration) termination).setDuration(duration);
            ((IterationsOrDurationStrategyConfiguration) termination).setIterations(
                    (int) ((JTerminationIterations) jTermination).getIterationCount());
            ((IterationsOrDurationStrategyConfiguration) termination).setShutdown(new AtomicBoolean(false));
        } else if (jTermination instanceof JTerminationDuration) {
            termination = new IterationsOrDurationStrategyConfiguration();
            String duration = parseDuration(((JTerminationDuration) jTermination).getDurationInSeconds());
            ((IterationsOrDurationStrategyConfiguration) termination).setDuration(duration);
            ((IterationsOrDurationStrategyConfiguration) termination).setShutdown(new AtomicBoolean(false));
        } else if (jTermination instanceof JTerminationBackground) {
            termination = new InfiniteTerminationStrategyConfiguration();
        }
        return termination;
    }

    private static String parseDuration(long durationInSecond) {
        return durationInSecond + "s";
    }
}
