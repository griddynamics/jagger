package com.griddynamics.jagger.user.test.configurations.auxiliary;

import com.griddynamics.jagger.invoker.OneByOneLoadBalancer;
import com.griddynamics.jagger.invoker.OneByOnePairSupplierFactory;
import com.griddynamics.jagger.invoker.QueryPoolLoadBalancer;
import com.griddynamics.jagger.invoker.RandomLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;

import java.io.Serializable;
import java.util.Random;

/**
 * Provides load balancer aka distributor (how to pair endpoints and queries) (subtypes of {@link QueryPoolLoadBalancer}).
 */
public class LoadBalancerProvider implements Serializable {
    private final QueryPoolLoadBalancer loadBalancer;

    private LoadBalancerProvider(QueryPoolLoadBalancer loadBalancer) {this.loadBalancer = loadBalancer;}

    public QueryPoolLoadBalancer provide() {
        return loadBalancer;
    }

    /**
     * Default load balancers.
     * @see RoundRobinLoadBalancer
     * @see OneByOneLoadBalancer
     */
    public enum DefaultLoadBalancer {
        ROUND_ROBIN,
        ONE_BY_ONE
    }

    /**
     * @param loadBalancer load balancer to use (look at {@link DefaultLoadBalancer})
     * @return {@link LoadBalancerProvider} with load balancer specified by <b>loadBalancer<b/> parameter
     */
    public static LoadBalancerProvider of(DefaultLoadBalancer loadBalancer) {
        QueryPoolLoadBalancer balancer;
        switch (loadBalancer) {
            case ONE_BY_ONE:
                balancer = new OneByOneLoadBalancer();
                break;
            case ROUND_ROBIN:
            default:
                balancer = new RoundRobinLoadBalancer();
                break;
        }
        return new LoadBalancerProvider(balancer);
    }

    /**
     * @param loadBalancer load balancer to use (look at {@link DefaultLoadBalancer})
     * @param seed         the initial seed of {@link Random} (look at {@link Random#Random(long)})
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with load balancer specified by loadBalancer parameter
     * @see LoadBalancerProvider#ofRandomized(DefaultLoadBalancer)
     */
    public static LoadBalancerProvider ofRandomized(DefaultLoadBalancer loadBalancer, Seed seed) {
        RandomLoadBalancer randomLoadBalancer = new RandomLoadBalancer();
        randomLoadBalancer.setRandomSeed(seed.value());

        switch (loadBalancer) {
            case ONE_BY_ONE:
                randomLoadBalancer.setPairSupplierFactory(new OneByOnePairSupplierFactory());
            case ROUND_ROBIN:
                randomLoadBalancer.setPairSupplierFactory(new RoundRobinPairSupplierFactory());
        }
        return new LoadBalancerProvider(randomLoadBalancer);
    }

    /**
     * @param loadBalancer load balancer to use (look at {@link DefaultLoadBalancer})
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with load balancer specified by loadBalancer parameter and default seed ({@link Seed#DEFAULT_SEED})
     * @see LoadBalancerProvider#ofRandomized(DefaultLoadBalancer, Seed)
     */
    public static LoadBalancerProvider ofRandomized(DefaultLoadBalancer loadBalancer) {
        return ofRandomized(loadBalancer, Seed.DEFAULT_SEED);
    }

    /**
     * @param customLoadBalancer custom load balancer (subtype of {@link QueryPoolLoadBalancer})
     * @return {@link LoadBalancerProvider} of custom {@link QueryPoolLoadBalancer}
     */
    public static LoadBalancerProvider ofСustom(QueryPoolLoadBalancer customLoadBalancer) {
        return new LoadBalancerProvider(customLoadBalancer);
    }
}
