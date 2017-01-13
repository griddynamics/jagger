package com.griddynamics.jagger.user.test.configurations.auxiliary;

import com.griddynamics.jagger.invoker.OneByOneLoadBalancer;
import com.griddynamics.jagger.invoker.OneByOnePairSupplierFactory;
import com.griddynamics.jagger.invoker.PairSupplierFactory;
import com.griddynamics.jagger.invoker.QueryPoolLoadBalancer;
import com.griddynamics.jagger.invoker.RandomLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;

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
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with {@link RoundRobinPairSupplierFactory}
     */
    public static LoadBalancerProvider roundRobinRandomized() {
        return new LoadBalancerProvider(new RandomLoadBalancer() {{
            setPairSupplierFactory(new RoundRobinPairSupplierFactory());
            setRandomSeed(31);
        }});
    }

    /**
     * @return {@link LoadBalancerProvider} of {@link OneByOneLoadBalancer}
     */
    public static LoadBalancerProvider oneByOne() {
        return new LoadBalancerProvider(new OneByOneLoadBalancer());
    }

    /**
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with {@link OneByOnePairSupplierFactory}
     */
    public static LoadBalancerProvider oneByOneRandomized() {
        return new LoadBalancerProvider(new RandomLoadBalancer() {{
            setPairSupplierFactory(new OneByOnePairSupplierFactory());
            setRandomSeed(31);
        }});
    }

    /**
     * @return {@link LoadBalancerProvider} of custom {@link QueryPoolLoadBalancer}
     */
    public static LoadBalancerProvider custom(QueryPoolLoadBalancer customLoadBalancer) {
        return new LoadBalancerProvider(customLoadBalancer);
    }

    /**
     * @return {@link LoadBalancerProvider} of {@link RandomLoadBalancer} with custom {@link PairSupplierFactory}
     */
    public static LoadBalancerProvider customRandomized(PairSupplierFactory customPairSupplierFactory) {
        return new LoadBalancerProvider(new RandomLoadBalancer() {{
            setPairSupplierFactory(customPairSupplierFactory);
            setRandomSeed(31);
        }});
    }
}
