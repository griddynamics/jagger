package com.griddynamics.jagger.user.test.configurations.load;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class JRpsLoad implements JLoad {

    private long requestsPerSecond;
    private long maxLoadThreads;
    private long warmUpTimeInSeconds;

}
