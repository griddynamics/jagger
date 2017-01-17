package com.griddynamics.jagger.user.test.configurations.loadbalancer;

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
public class JLoadBalancer implements Serializable {
    /**
     * Default load balancers.
     *
     * @see RoundRobinLoadBalancer
     * @see OneByOneLoadBalancer
     */
    public enum DefaultLoadBalancer {
        ROUND_ROBIN,
        ONE_BY_ONE
    }

    /**
     * Creates {@link Builder} of load balancer
     *
     * @param loadBalancer load balancer to use (look at {@link DefaultLoadBalancer})
     */
    public static Builder builder(DefaultLoadBalancer loadBalancer) {
        return new Builder(loadBalancer);
    }

    public static class Builder {
        private final DefaultLoadBalancer loadBalancer;
        private Long seed;

        /**
         * Creates {@link Builder} of load balancer
         *
         * @param loadBalancer load balancer to use (look at {@link DefaultLoadBalancer})
         */
        public Builder(DefaultLoadBalancer loadBalancer) {
            this.loadBalancer = loadBalancer;
        }

        /**
         * Optional: Calling this setter will produce Randomized load balancer (look at {@link RandomLoadBalancer})
         * with random seed specified by parameter <b>seed<b/>.
         *
         * @param seed the initial seed of {@link Random} (look at {@link Random#Random(long)})
         * @return {@link Builder} this
         */
        public Builder withRandomSeed(long seed) {
            this.seed = seed;
            return this;
        }

        /**
         * @return Load balancer (subtype of {@link QueryPoolLoadBalancer})
         */
        public QueryPoolLoadBalancer build() {
            switch (loadBalancer) {
                case ONE_BY_ONE:
                    if (seed == null) {
                        return new OneByOneLoadBalancer();
                    } else {
                        RandomLoadBalancer randomLoadBalancer = new RandomLoadBalancer();
                        randomLoadBalancer.setRandomSeed(seed);
                        randomLoadBalancer.setPairSupplierFactory(new OneByOnePairSupplierFactory());
                        return randomLoadBalancer;
                    }
                case ROUND_ROBIN:
                default:
                    if (seed == null) {
                        return new RoundRobinLoadBalancer();
                    } else {
                        RandomLoadBalancer randomLoadBalancer = new RandomLoadBalancer();
                        randomLoadBalancer.setRandomSeed(seed);
                        randomLoadBalancer.setPairSupplierFactory(new RoundRobinPairSupplierFactory());
                        return randomLoadBalancer;
                    }
            }
        }
    }
}
