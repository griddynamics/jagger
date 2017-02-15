package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.AbstractIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * This {@link com.griddynamics.jagger.invoker.LoadBalancer} implementation provides guarantees
 * that each query and endpoint pair will be in exclusive access, i.e. once it is acquired by one thread
 * it won't be acquired by any other thread (virtual user) in multi threaded environment.
 * @n
 * If {@link #randomnessSeed} is not {@code null} randomly shuffles the sequence of pairs from {@link #pairSupplierFactory} using it.
 * @n
 * Created by Andrey Badaev
 * Date: 01/02/17
 */
public abstract class ExclusiveAccessLoadBalancer<Q, E> extends PairSupplierFactoryLoadBalancer<Q, E> {
    
    private final static Logger log = LoggerFactory.getLogger(ExclusiveAccessLoadBalancer.class);
    
    public ExclusiveAccessLoadBalancer(PairSupplierFactory<Q, E> pairSupplierFactory) {
        super(pairSupplierFactory);
    }
    
    private volatile ArrayBlockingQueue<Pair<Q, E>> pairQueue;
    private volatile Long randomnessSeed;
    
    public void setRandomnessSeed(Long randomnessSeed) {
        this.randomnessSeed = randomnessSeed;
    }
    
    protected abstract boolean isToCircleAnIteration();
    
    protected ArrayBlockingQueue<Pair<Q, E>> getPairQueue() {
        return pairQueue;
    }
    
    protected abstract Pair<Q, E> pollNext();
    
    @Override
    public Iterator<Pair<Q, E>> provide() {
        return new AbstractIterator<Pair<Q, E>>() {
            
            Pair<Q, E> current = null;
            
            @Override
            protected Pair<Q, E> computeNext() {
                if (current != null && isToCircleAnIteration()) {
                    log.debug("Returning pair - {}", current);
                    pairQueue.add(current);
                }
                current = pollNext();
                
                log.debug("Providing pair - {}", current);
                return current;
            }
            
            @Override
            public String toString() {
                return super.getClass() + " iterator";
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
            List<Pair<Q, E>> pairList = new ArrayList<>(pairSupplier.size());

            int index = 0;
            int step = 1;
            if (kernelInfo != null) { // then from all the provided pairs pick only those under this node's index.
                index = kernelInfo.getKernelId();
                step = kernelInfo.getKernelsNumber();
            }
            for (; index < pairSupplier.size(); index += step) {
                pairList.add(pairSupplier.get(index));
            }
            
            if (Objects.nonNull(randomnessSeed)) {
                log.info("'randomnessSeed' value is not null. Going to shuffle the pairs");
                Collections.shuffle(pairList, new Random(randomnessSeed));
            }
            
            log.info("{} pairs on this node to balance", pairList.size());
            log.debug("Pairs to load balance: {}", pairList);
            
            pairQueue = new ArrayBlockingQueue<>(pairSupplier.size(), true, pairList);
        }
    }
}
