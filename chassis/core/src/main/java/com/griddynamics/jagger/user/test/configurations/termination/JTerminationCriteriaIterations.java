package com.griddynamics.jagger.user.test.configurations.termination;

import com.griddynamics.jagger.user.test.configurations.termination.aux.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.aux.MaxDurationInSeconds;

import java.util.Objects;

/**
 * Test with such termination strategy will last till defined number of requests are executed.
 */
public class JTerminationCriteriaIterations implements JTerminationCriteria {

    private final long iterationsNumber;
    private final long maxDurationInSeconds;
    
    public JTerminationCriteriaIterations(IterationsNumber iterationsNumber, MaxDurationInSeconds maxDurationInSeconds) {
        Objects.nonNull(iterationsNumber);
        Objects.nonNull(maxDurationInSeconds);
        
        this.iterationsNumber = iterationsNumber.value();
        this.maxDurationInSeconds = maxDurationInSeconds.value();
    }
    
    public static JTerminationCriteriaIterations of(IterationsNumber iterationsNumber, MaxDurationInSeconds maxDurationInSeconds) {
        return new JTerminationCriteriaIterations(iterationsNumber, maxDurationInSeconds);
    }
    
    public long getIterationsNumber() {
        return iterationsNumber;
    }

    public long getMaxDurationInSeconds() {
        return maxDurationInSeconds;
    }
}
