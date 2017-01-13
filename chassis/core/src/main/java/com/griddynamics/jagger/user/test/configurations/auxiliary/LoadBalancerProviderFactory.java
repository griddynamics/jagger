package com.griddynamics.jagger.user.test.configurations.auxiliary;

import com.griddynamics.jagger.invoker.OneByOneLoadBalancer;
import com.griddynamics.jagger.invoker.OneByOnePairSupplierFactory;
import com.griddynamics.jagger.invoker.PairSupplierFactory;
import com.griddynamics.jagger.invoker.QueryPoolLoadBalancer;
import com.griddynamics.jagger.invoker.RandomLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;

import java.util.Random;

/**
 * Creates entities of type {@link LoadBalancerProvider}.
 */
public class LoadBalancerProviderFactory {

    /**
     * @return {@link LoadBalancerProvider} of {@link RoundRobinLoadBalancer}
     */
    public static LoadBalancerProvider roundRobin() {
        return new LoadBalancerProvider(new RoundRobinLoadBalancer());
    }

    /**
     * @param seed the initial seed of {@link Random} (look at {@link Random#Random(long)})
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with {@link RoundRobinPairSupplierFactory}
     */
    public static LoadBalancerProvider roundRobinRandomized(long seed) {
        RandomLoadBalancer loadBalancer = new RandomLoadBalancer();
        loadBalancer.setRandomSeed(seed);
        loadBalancer.setPairSupplierFactory(new RoundRobinPairSupplierFactory());
        return new LoadBalancerProvider(loadBalancer);
    }

    /**
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with {@link RoundRobinPairSupplierFactory}
     */
    public static LoadBalancerProvider roundRobinRandomized() {
        return roundRobinRandomized(31);
    }

    /**
     * @return {@link LoadBalancerProvider} of {@link OneByOneLoadBalancer}
     */
    public static LoadBalancerProvider oneByOne() {
        return new LoadBalancerProvider(new OneByOneLoadBalancer());
    }

    /**
     * @param seed the initial seed of {@link Random} (look at {@link Random#Random(long)})
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with {@link OneByOnePairSupplierFactory}
     */
    public static LoadBalancerProvider oneByOneRandomized(long seed) {
        RandomLoadBalancer loadBalancer = new RandomLoadBalancer();
        loadBalancer.setRandomSeed(seed);
        loadBalancer.setPairSupplierFactory(new OneByOnePairSupplierFactory());
        return new LoadBalancerProvider(loadBalancer);
    }

    /**
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with {@link OneByOnePairSupplierFactory}
     */
    public static LoadBalancerProvider oneByOneRandomized() {
        return oneByOneRandomized(31);
    }

    /**
     * @return {@link LoadBalancerProvider} of custom {@link QueryPoolLoadBalancer}
     */
    public static LoadBalancerProvider custom(QueryPoolLoadBalancer customLoadBalancer) {
        return new LoadBalancerProvider(customLoadBalancer);
    }

    /**
     * @param seed the initial seed of {@link Random} (look at {@link Random#Random(long)})
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with custom {@link PairSupplierFactory}
     */
    public static LoadBalancerProvider customRandomized(PairSupplierFactory customPairSupplierFactory, long seed) {
        RandomLoadBalancer loadBalancer = new RandomLoadBalancer();
        loadBalancer.setRandomSeed(seed);
        loadBalancer.setPairSupplierFactory(customPairSupplierFactory);
        return new LoadBalancerProvider(loadBalancer);
    }

    /**
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with custom {@link PairSupplierFactory}
     */
    public static LoadBalancerProvider customRandomized(PairSupplierFactory customPairSupplierFactory) {
        return customRandomized(customPairSupplierFactory, 31);
    }
}
