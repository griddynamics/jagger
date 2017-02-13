package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;

/**
 * Subclass of {@link ExclusiveAccessLoadBalancer} that provides each pair of Q and E only once (does not circle an iteration).
 * As a result it works as a finite load balancer over a predefined sequence of pairs.
 * @n
 * Created by Andrey Badaev
 * Date: 06/02/17
 */
public class NonCircularExclusiveAccessLoadBalancer<Q, E> extends ExclusiveAccessLoadBalancer<Q, E> {
    
    public NonCircularExclusiveAccessLoadBalancer(PairSupplierFactory<Q, E> pairSupplierFactory) {
        super(pairSupplierFactory);
    }
    
    @Override
    protected boolean isToCircleAnIteration() {
        return false;
    }
    
    @Override
    protected Pair<Q, E> pollNext() {
        return getPairQueue().poll();
    }
}
