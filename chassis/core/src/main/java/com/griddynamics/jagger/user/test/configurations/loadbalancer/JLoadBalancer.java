package com.griddynamics.jagger.user.test.configurations.loadbalancer;

import com.griddynamics.jagger.invoker.ExclusiveAccessCircularLoadBalancer;
import com.griddynamics.jagger.invoker.ExclusiveAccessRandomLoadBalancer;
import com.griddynamics.jagger.invoker.OneByOneLoadBalancer;
import com.griddynamics.jagger.invoker.OneByOnePairSupplierFactory;
import com.griddynamics.jagger.invoker.PairSupplierFactory;
import com.griddynamics.jagger.invoker.QueryPoolLoadBalancer;
import com.griddynamics.jagger.invoker.RandomLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;
import com.griddynamics.jagger.invoker.SimpleCircularLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

/**
 * Provides load balancer aka distributor (how to pair endpoints and queries) (subtypes of {@link QueryPoolLoadBalancer}).
 */
public class JLoadBalancer implements Serializable {
    
    private final static Logger log = LoggerFactory.getLogger(JLoadBalancer.class);
    
    /**
     * Creates {@link Builder} of load balancer
     *
     * @param loadBalancer load balancer to use (look at {@link DefaultLoadBalancer})
     */
    public static Builder builder(DefaultLoadBalancer loadBalancer) {
        return new Builder(loadBalancer);
    }
    
    /**
     * Default load balancers.
     *
     * @see RoundRobinLoadBalancer
     * @see OneByOneLoadBalancer
     */
    public enum DefaultLoadBalancer {
        ROUND_ROBIN, ONE_BY_ONE
    }
    
    public static class Builder {
        private final DefaultLoadBalancer loadBalancer;
        private Long seed;
        private boolean exclusiveAccess;
        
        /**
         * Creates {@link Builder} of load balancer
         *
         * @param loadBalancer load balancer to use (look at {@link DefaultLoadBalancer})
         */
        public Builder(DefaultLoadBalancer loadBalancer) {
            this.loadBalancer = loadBalancer;
        }
        
        /**
         * Optional: Calling this setter will produce a load balancer
         * which randomly picks an endpoint & query pair to provide
         * with random seed specified by parameter <b>seed<b/>.
         *
         * @param seed the initial seed of {@link Random} (look at {@link Random#Random(long)})
         * @return {@link Builder} this
         * @n
         */
        public Builder withRandomSeed(long seed) {
            this.seed = seed;
            return this;
        }
        
        /**
         * Optional: If this flag is true the builder will produce a load balancer with an exclusive access
         * to each endpoint & query pair. That means once a virtual user acquires a pair
         * any other virtual user won't get the same pair until that user comes for the next pair.
         *
         * @return {@link Builder} this
         * @n
         */
        public Builder withExclusiveAccess() {
            this.exclusiveAccess = true;
            return this;
        }
        
        /**
         * @return Load balancer (subtype of {@link QueryPoolLoadBalancer})
         */
        public QueryPoolLoadBalancer build() {
            
            PairSupplierFactory pairSupplierFactory = null;
            switch (loadBalancer) {
                case ONE_BY_ONE:
                    pairSupplierFactory = new OneByOnePairSupplierFactory();
                    break;
                case ROUND_ROBIN:
                default:
                    pairSupplierFactory = new RoundRobinPairSupplierFactory();
                    break;
            }
            
            QueryPoolLoadBalancer loadBalancer = null;
            if (Objects.nonNull(seed)) {
                loadBalancer = exclusiveAccess ? new ExclusiveAccessRandomLoadBalancer(seed, pairSupplierFactory)
                                               : new RandomLoadBalancer(seed, pairSupplierFactory);
            } else {
                loadBalancer = exclusiveAccess ? new ExclusiveAccessCircularLoadBalancer(pairSupplierFactory)
                                               : new SimpleCircularLoadBalancer(pairSupplierFactory);
            }
    
            log.debug("Built a {} load balancer", loadBalancer);
            return loadBalancer;
        }
    }
}
