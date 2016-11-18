package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaDuration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link TerminateStrategyConfiguration} entity from user-defined {@link JTerminationCriteria} entity.
 */
class TerminationGenerator {

    static TerminateStrategyConfiguration generateTermination(JTerminationCriteria jTerminationCriteria) {
        TerminateStrategyConfiguration termination = null;
        if (jTerminationCriteria instanceof JTerminationCriteriaIterations) {
            termination = generateIterationTermination((JTerminationCriteriaIterations) jTerminationCriteria);
        } else if (jTerminationCriteria instanceof JTerminationCriteriaDuration) {
            termination = generateDurationTermination((JTerminationCriteriaDuration) jTerminationCriteria);
        } else if (jTerminationCriteria instanceof JTerminationCriteriaBackground) {
            termination = new InfiniteTerminationStrategyConfiguration();
        }
        return termination;
    }

    private static String parseDuration(long durationInSecond) {
        return durationInSecond + "s";
    }

    private static TerminateStrategyConfiguration generateIterationTermination(JTerminationCriteriaIterations jTerminationIterations) {
        IterationsOrDurationStrategyConfiguration termination = new IterationsOrDurationStrategyConfiguration();
        String duration = parseDuration(jTerminationIterations.getMaxDurationInSeconds());
        termination.setDuration(duration);
        termination.setIterations((int) jTerminationIterations.getIterationsNumber());
        termination.setShutdown(new AtomicBoolean(false));
        return termination;
    }

    private static TerminateStrategyConfiguration generateDurationTermination(JTerminationCriteriaDuration jTermination) {
        IterationsOrDurationStrategyConfiguration termination = new IterationsOrDurationStrategyConfiguration();
        String duration = parseDuration(jTermination.getDurationInSeconds());
        termination.setDuration(duration);
        termination.setShutdown(new AtomicBoolean(false));
        return termination;
    }
}
