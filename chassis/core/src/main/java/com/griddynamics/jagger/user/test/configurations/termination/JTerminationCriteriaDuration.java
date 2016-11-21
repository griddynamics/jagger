package com.griddynamics.jagger.user.test.configurations.termination;

import com.griddynamics.jagger.user.test.configurations.termination.aux.DurationInSeconds;

import java.util.Objects;

/**
 * Test with such termination strategy will be executed for the defined time - duration.
 */
public class JTerminationCriteriaDuration implements JTerminationCriteria {
    
    private final long durationInSeconds;
    
    public JTerminationCriteriaDuration(DurationInSeconds durationInSeconds) {
        Objects.nonNull(durationInSeconds);
        
        this.durationInSeconds = durationInSeconds.value();
    }
    
    public static JTerminationCriteriaDuration of(DurationInSeconds durationInSeconds) {
        return new JTerminationCriteriaDuration(durationInSeconds);
    }
    
    public long getDurationInSeconds() {
        return durationInSeconds;
    }
}
