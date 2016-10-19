package com.griddynamics.jagger.user.test.configurations.termination;

import lombok.Builder;
import lombok.ToString;

/**
 * This termination strategy is helpful, when you would like to execute an exact number of iterations. Attribute 'iterations'
 * say how much iterations you would like to execute. With attribute 'maxDuration' you can configure maximum execution time of test.
 * By default it equals 2 hours.
 */
@Builder
@ToString
public class JTerminationIterations implements JTermination {

    private long iterationCount;
    private long maxDurationInSeconds;

}
