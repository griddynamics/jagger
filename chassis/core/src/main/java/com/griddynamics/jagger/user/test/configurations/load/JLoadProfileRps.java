package com.griddynamics.jagger.user.test.configurations.load;

import com.griddynamics.jagger.user.test.configurations.load.aux.MaxLoadThreads;
import com.griddynamics.jagger.user.test.configurations.load.aux.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.load.aux.WarmUpTimeInSeconds;

import java.util.Objects;

/**
 * This type of load implements an exact number of requests per second performed by Jagger.
 *
 * @ingroup Main_Load_profiles_group
 */
public class JLoadProfileRps implements JLoadProfile {

    private final long requestsPerSecond;
    private final long maxLoadThreads;
    private final long warmUpTimeInSeconds;

    /** Create load profile: request per seconds
     * @n
     * @param requestsPerSecond - The number of requests per second Jagger shall perform
     * @param maxLoadThreads - The maximum number of threads, which Jagger engine can create to provide the requested load
     * @param warmUpTimeInSeconds - The warm up time value in seconds. Jagger increases load from 0 to @b requestPerSecond by @b warmUpTimeInSeconds
     */
    public JLoadProfileRps(RequestsPerSecond requestsPerSecond, MaxLoadThreads maxLoadThreads, WarmUpTimeInSeconds warmUpTimeInSeconds) {
        Objects.nonNull(requestsPerSecond);
        Objects.nonNull(maxLoadThreads);
        Objects.nonNull(warmUpTimeInSeconds);
        
        this.requestsPerSecond = requestsPerSecond.value();
        this.maxLoadThreads = maxLoadThreads.value();
        this.warmUpTimeInSeconds = warmUpTimeInSeconds.value();
    }

    /** Create load profile: request per seconds
     * @n
     * @param requestsPerSecond - The number of requests per second Jagger shall perform
     * @param maxLoadThreads - The maximum number of threads, which Jagger engine can create to provide the requested load
     * @param warmUpTimeInSeconds - The warm up time value in seconds. Jagger increases load from 0 to @b requestPerSecond by @b warmUpTimeInSeconds
     */
    public static JLoadProfileRps of(RequestsPerSecond requestsPerSecond, MaxLoadThreads maxLoadThreads, WarmUpTimeInSeconds warmUpTimeInSeconds) {
        return new JLoadProfileRps(requestsPerSecond, maxLoadThreads, warmUpTimeInSeconds);
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
