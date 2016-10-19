package com.griddynamics.jagger.user.test.configurations.termination;

import lombok.Builder;

/**
 * Use this termination, when you would like to execute your load within a certain time.
 */
@Builder
public class JTerminationDuration implements JTermination {
    private long durationInSeconds;


}
