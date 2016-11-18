package com.griddynamics.jagger.user.test.configurations.termination;

import java.util.Objects;

/**
 * Test with such termination strategy will last till defined number of requests are executed.
 */
public class JTerminationIterations implements JTermination {

    private final long iterationsNumber;
    private final long maxDurationInSeconds;
    
    public JTerminationIterations(IterationsNumber iterationsNumber, MaxDurationInSeconds maxDurationInSeconds) {
        Objects.nonNull(iterationsNumber);
        Objects.nonNull(maxDurationInSeconds);
        
        this.iterationsNumber = iterationsNumber.value();
        this.maxDurationInSeconds = maxDurationInSeconds.value();
    }
    
    public static JTerminationIterations of(IterationsNumber iterationsNumber, MaxDurationInSeconds maxDurationInSeconds) {
        return new JTerminationIterations(iterationsNumber, maxDurationInSeconds);
    }
    
    public long getIterationsNumber() {
        return iterationsNumber;
    }

    public long getMaxDurationInSeconds() {
        return maxDurationInSeconds;
    }
}
