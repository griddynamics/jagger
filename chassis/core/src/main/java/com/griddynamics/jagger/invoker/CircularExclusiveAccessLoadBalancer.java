package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;

import java.util.concurrent.TimeUnit;

/**
 * Subclass of {@link ExclusiveAccessLoadBalancer} that circularly selects pairs of Q and E.
 * As a result it simulates infinite iteration over the limited sequence of pairs.
 * @n
 * Created by Andrey Badaev
 * Date: 07/02/17
 */
public class CircularExclusiveAccessLoadBalancer<Q, E> extends ExclusiveAccessLoadBalancer<Q, E> {
    
    public CircularExclusiveAccessLoadBalancer(PairSupplierFactory<Q, E> pairSupplierFactory) {
        super(pairSupplierFactory);
    }
    
    @Override
    protected boolean isToCircleAnIteration() {
        return true;
    }
    
    protected Pair<Q, E> pollNext() {
        final int timeout = 10;
        final TimeUnit timeUnit = TimeUnit.MINUTES;
        
        Pair<Q, E> next = null;
        try {
            next = getPairQueue().poll(timeout, timeUnit);
        } catch (InterruptedException ignored) {
        }
        
        if (next == null) {
            throw new IllegalStateException(String.format("Didn't manage to poll the next pair during %s %s",
                                                          String.valueOf(timeout),
                                                          timeUnit));
        }
        return next;
    }
}
