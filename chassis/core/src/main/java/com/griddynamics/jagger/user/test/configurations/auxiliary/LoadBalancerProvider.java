package com.griddynamics.jagger.user.test.configurations.auxiliary;

import com.griddynamics.jagger.invoker.QueryPoolLoadBalancer;

import java.io.Serializable;

/**
 * Provides load balancer aka distributor (how to pair endpoints and queries) (subtypes of {@link QueryPoolLoadBalancer}).
 */
public class LoadBalancerProvider implements Serializable {
    private final QueryPoolLoadBalancer loadBalancer;

    public LoadBalancerProvider(QueryPoolLoadBalancer loadBalancer) {this.loadBalancer = loadBalancer;}

    public QueryPoolLoadBalancer provide() {
        return loadBalancer;
    }

}
