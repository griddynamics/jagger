package com.griddynamics.jagger.user.test.configurations.auxiliary;

/**
 * Represents a Seed for Randomized Load Balancer (look at {@link LoadBalancerProvider#ofRandomized(LoadBalancerProvider.DefaultLoadBalancer, Seed)}).
 */
public final class Seed {
    public static final Seed DEFAULT_SEED = new Seed(31L);

    private final Long seed;

    public Seed(Long seed) {
        this.seed = seed;
    }

    public static Seed of(Long seed) {
        return new Seed(seed);
    }

    public Long value() {
        return seed;
    }
}
