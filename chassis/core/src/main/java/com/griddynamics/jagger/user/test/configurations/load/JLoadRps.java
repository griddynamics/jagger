package com.griddynamics.jagger.user.test.configurations.load;

import java.util.Objects;

/**
 * This type of load implements an exact number of requests per second performed by Jagger.
 * The number of requests, the max allowed number of load threads and the warm up time values are configurable.
 *
 * This type of load generates an exact number of requests per second. Where request is invoked from Jagger.
 * By using attribute 'requestsPerSecond', you can configure a number of requests. Attribute 'maxLoadThreads'
 * says what is the maximum number of threads Jagger engine is allowed to create, to provide the requested load.
 * By default it equals 4000. You can change this value in property file. If attribute 'warmUpTimeInSeconds' is set,
 * load will increase from 0 to the value for this time.
 */
public class JLoadRps implements JLoad {

    private final long requestsPerSecond;
    private final long maxLoadThreads;
    private final long warmUpTimeInSeconds;
    
    public JLoadRps(RequestsPerSecond requestsPerSecond, MaxLoadThreads maxLoadThreads, WarmUpTimeInSeconds warmUpTimeInSeconds) {
        Objects.nonNull(requestsPerSecond);
        Objects.nonNull(maxLoadThreads);
        Objects.nonNull(warmUpTimeInSeconds);
        
        this.requestsPerSecond = requestsPerSecond.value();
        this.maxLoadThreads = maxLoadThreads.value();
        this.warmUpTimeInSeconds = warmUpTimeInSeconds.value();
    }
    
    public static JLoadRps of(RequestsPerSecond requestsPerSecond, MaxLoadThreads maxLoadThreads, WarmUpTimeInSeconds warmUpTimeInSeconds) {
        return new JLoadRps(requestsPerSecond, maxLoadThreads, warmUpTimeInSeconds);
    }
    
    public long getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public long getMaxLoadThreads() {
        return maxLoadThreads;
    }

    public long getWarmUpTimeInSeconds() {
        return warmUpTimeInSeconds;
    }
}
