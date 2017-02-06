package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;

/**
 * * Guarantees that every pair will be provided only once even in multithreaded environment.
 * @n
 * Created by Andrey Badaev
 * Date: 06/02/17
 */
public class ExclusiveAccessOneIterationLoadBalancer<Q, E> extends ExclusiveAccessCircularLoadBalancer<Q, E> {
    
    public ExclusiveAccessOneIterationLoadBalancer(PairSupplierFactory<Q, E> pairSupplierFactory) {
        super(pairSupplierFactory);
    }
    
    @Override
    protected boolean isToLoopAnIteration() {
        return false;
    }
    
    @Override
    protected Pair<Q, E> pollNext() {
        return getPairQueue().poll();
    }
}
