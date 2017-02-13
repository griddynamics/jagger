package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This {@link com.griddynamics.jagger.invoker.LoadBalancer} implementation has the same semantics
 * as {@link com.griddynamics.jagger.invoker.SimpleCircularLoadBalancer} and in addition it guarantees
 * that each query and endpoint pair will be in exclusive access, i.e. once it is acquired by one thread
 * it won't be acquired by any other thread (virtual user).
 * @n
 * Created by Andrey Badaev
 * Date: 01/02/17
 */
public class ExclusiveAccessCircularLoadBalancer<Q, E> extends PairSupplierFactoryLoadBalancer<Q, E> {
    
    private final static Logger log = LoggerFactory.getLogger(ExclusiveAccessCircularLoadBalancer.class);
    
    public ExclusiveAccessCircularLoadBalancer(PairSupplierFactory<Q, E> pairSupplierFactory) {
        super(pairSupplierFactory);
    }
    
    private volatile ArrayBlockingQueue<Pair<Q, E>> pairQueue;
    
    private Pair<Q, E> pollNext() {
        final int timeout = 10;
        final TimeUnit timeUnit = TimeUnit.MINUTES;
    
        try {
            return pairQueue.poll(timeout, timeUnit);
        } catch (InterruptedException e) {
            throw new IllegalStateException(String.format("Didn't manage to poll the next pair during %s %s",
                                                          String.valueOf(timeout),
                                                          timeUnit), e);
        }
    }
    
    @Override
    public Iterator<Pair<Q, E>> provide() {
        return new AbstractIterator<Pair<Q, E>>() {
            
            Pair<Q, E> current = null;
            
            @Override
            protected Pair<Q, E> computeNext() {
                if (current != null) {
                    log.debug("Returning pair - {}", current);
                    pairQueue.add(current);
                }
                current = pollNext();
                
                log.debug("Providing pair - {}", current);
                return current;
            }
            
            @Override
            public String toString() {
                return "ExclusiveAccessCircularLoadBalancer iterator";
            }
        };
    }
    
    @Override
    public void init() {
        synchronized (lock) {
            if (initialized) {
                log.debug("already initialized. returning...");
                return;
            }
            
            super.init();
    
            PairSupplier<Q, E> pairSupplier = getPairSupplier();
            pairQueue = new ArrayBlockingQueue<>(pairSupplier.size(), true);
            for (int i = 0; i < pairSupplier.size(); ++i) {
                pairQueue.add(pairSupplier.get(i));
            }
        }
    }
}
