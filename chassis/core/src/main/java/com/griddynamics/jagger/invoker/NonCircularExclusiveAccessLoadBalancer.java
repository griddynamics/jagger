package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;

/**
 * Subclass of {@link ExclusiveAccessLoadBalancer} that provides each pair of Q and E only once (does not circle an iteration).
 * As a result it works as a finite load balancer over a predefined sequence of pairs.
 * @n
 * Also as a subclass of {@link ExclusiveAccessLoadBalancer} provides guarantees
 * that each query and endpoint pair will be in exclusive access, i.e. once it is acquired by one thread
 * it won't be acquired by any other thread (virtual user) in multi threaded environment.
 * @n
 * If {@link #randomnessSeed} is not {@code null} randomly shuffles the sequence of pairs from {@link #pairSupplierFactory} using it.
 * @n
 * Created by Andrey Badaev
 * Date: 06/02/17
 *
 * @ingroup Main_Distributors_group */
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
