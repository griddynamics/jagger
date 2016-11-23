package com.griddynamics.jagger.user.test.configurations.termination;

import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;

import java.util.Objects;

/**
 * Test with such termination strategy will last till defined number of requests are executed.
 *
 * @ingroup Main_Termination_criteria_group
 */
public class JTerminationCriteriaIterations implements JTerminationCriteria {

    private final long iterationsNumber;
    private final long maxDurationInSeconds;

    /** Create termination criteria: by number of itterations
     * @n
     * @param iterationsNumber - The target number of requests to the system under test. After this number of the requests is reached, load test will be terminated
     * @param maxDurationInSeconds - The timeout for the test execution. If termination criteria was not reached, load test will be stopped by timeout
     */
    public JTerminationCriteriaIterations(IterationsNumber iterationsNumber, MaxDurationInSeconds maxDurationInSeconds) {
        Objects.nonNull(iterationsNumber);
        Objects.nonNull(maxDurationInSeconds);
        
        this.iterationsNumber = iterationsNumber.value();
        this.maxDurationInSeconds = maxDurationInSeconds.value();
    }

    /** Create termination criteria: by number of itterations
     * @n
     * @param iterationsNumber - The target number of requests to the system under test. After this number of the requests is reached, load test will be terminated
     * @param maxDurationInSeconds - The timeout for the test execution. If termination criteria was not reached, load test will be stopped by timeout
     */
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
