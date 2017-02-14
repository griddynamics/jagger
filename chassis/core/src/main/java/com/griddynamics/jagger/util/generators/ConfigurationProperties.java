package com.griddynamics.jagger.util.generators;

import org.springframework.beans.factory.annotation.Value;

/**
 * A container for configuration specific properties.
 * @n
 * Created by Andrey Badaev
 * Date: 14/02/17
 */
public class ConfigurationProperties {
    
    @Value("${load.balancer.poll.timeout.in.millis:600000}") // 10 minutes by default
    private long loadBalancerPollTimeout;
    
    public long getLoadBalancerPollTimeout() {
        return loadBalancerPollTimeout;
    }
}
