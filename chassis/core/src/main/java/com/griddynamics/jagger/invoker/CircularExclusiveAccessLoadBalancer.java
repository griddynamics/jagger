package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Subclass of {@link ExclusiveAccessLoadBalancer} that circularly selects pairs of Q and E.
 * As a result it simulates infinite iteration over the limited sequence of pairs.
 * @n
 * Also as a subclass of {@link ExclusiveAccessLoadBalancer} provides guarantees
 * that each query and endpoint pair will be in exclusive access, i.e. once it is acquired by one thread
 * it won't be acquired by any other thread (virtual user) in multi threaded environment.
 * @n
 * If {@link #randomnessSeed} is not {@code null} randomly shuffles the sequence of pairs from {@link #pairSupplierFactory} using it.
 * @n
 * Created by Andrey Badaev
 * Date: 07/02/17
 *
 * @ingroup Main_Distributors_group */
public class CircularExclusiveAccessLoadBalancer<Q, E> extends ExclusiveAccessLoadBalancer<Q, E> {
    
    private final static Logger log = LoggerFactory.getLogger(CircularExclusiveAccessLoadBalancer.class);
    
    public CircularExclusiveAccessLoadBalancer(PairSupplierFactory<Q, E> pairSupplierFactory) {
        super(pairSupplierFactory);
    }
    
    private final static String POLL_TIMEOUT_NAME = "load balancer poll next timeout";
    
    private Timeout pollTimeout = new Timeout(10 * 60 * 1000, POLL_TIMEOUT_NAME); // 10 minutes by default
    
    @Override
    protected boolean isToCircleAnIteration() {
        return true;
    }
    
    protected Pair<Q, E> pollNext() {
        
        Pair<Q, E> next = null;
        long startMillis = System.currentTimeMillis();
        try {
            next = getPairQueue().poll(pollTimeout.getValue(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        
        if (next == null) {
            throw new IllegalStateException(String.format("Didn't manage to poll the next pair. Timeout %s",
                                                          pollTimeout));
        }
        
        long endMillis = System.currentTimeMillis();
        if (endMillis - startMillis > 1000) {
            log.warn(
                    "It took {} ms to poll the next pair for the load balancer. Possible reason: not enough test data for selected load",
                    endMillis - startMillis);
        }
        
        return next;
    }
    
    public void setPollTimeout(long pollTimeout) {
        this.pollTimeout = new Timeout(pollTimeout, POLL_TIMEOUT_NAME);
    }
}
