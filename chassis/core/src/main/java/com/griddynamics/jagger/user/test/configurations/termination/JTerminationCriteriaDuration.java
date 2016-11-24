package com.griddynamics.jagger.user.test.configurations.termination;

import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.DurationInSeconds;

import java.util.Objects;

/**
 * Test with such termination strategy will be executed for the defined time - duration.
 *
 * @ingroup Main_Termination_criteria_group
 */
public class JTerminationCriteriaDuration implements JTerminationCriteria {
    
    private final long durationInSeconds;

    /** Create termination criteria: by duration
     * @n
     * @param durationInSeconds - Test load execution time in seconds. After this time load test will be terminated
     */
    public JTerminationCriteriaDuration(DurationInSeconds durationInSeconds) {
        Objects.requireNonNull(durationInSeconds);
        
        this.durationInSeconds = durationInSeconds.value();
    }

    /** Create termination criteria: by duration
     * @n
     * @param durationInSeconds - Test load execution time in seconds. After this time load test will be terminated
     */
    public static JTerminationCriteriaDuration of(DurationInSeconds durationInSeconds) {
        return new JTerminationCriteriaDuration(durationInSeconds);
    }
    
    public long getDurationInSeconds() {
        return durationInSeconds;
    }
}
