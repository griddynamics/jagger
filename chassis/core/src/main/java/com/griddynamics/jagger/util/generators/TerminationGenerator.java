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
            termination = generateIterationTermination((JTerminationIterations) jTermination);
        } else if (jTermination instanceof JTerminationDuration) {
            termination = generateDurationTermination((JTerminationDuration) jTermination);
        } else if (jTermination instanceof JTerminationBackground) {
            termination = new InfiniteTerminationStrategyConfiguration();
        }
        return termination;
    }

    private static String parseDuration(long durationInSecond) {
        return durationInSecond + "s";
    }

    private static TerminateStrategyConfiguration generateIterationTermination(JTerminationIterations jTerminationIterations) {
        IterationsOrDurationStrategyConfiguration termination = new IterationsOrDurationStrategyConfiguration();
        String duration = parseDuration(jTerminationIterations.getMaxDurationInSeconds());
        termination.setDuration(duration);
        termination.setIterations((int) jTerminationIterations.getIterationCount());
        termination.setShutdown(new AtomicBoolean(false));
        return termination;
    }

    private static TerminateStrategyConfiguration generateDurationTermination(JTerminationDuration jTermination) {
        IterationsOrDurationStrategyConfiguration termination = new IterationsOrDurationStrategyConfiguration();
        String duration = parseDuration(jTermination.getDurationInSeconds());
        termination.setDuration(duration);
        termination.setShutdown(new AtomicBoolean(false));
        return termination;
    }
}
