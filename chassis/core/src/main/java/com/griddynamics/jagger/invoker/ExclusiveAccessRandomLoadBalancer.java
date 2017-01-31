package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This {@link com.griddynamics.jagger.invoker.LoadBalancer} implementation has the same semantics
 * as {@link com.griddynamics.jagger.invoker.RandomLoadBalancer} and in addition it guarantees
 * that each query and endpoint pair will be in exclusive access, i.e. once it is acquired by one thread
 * it won't be acquired by any other thread (virtual user).
 * @n
 * Created by Andrey Badaev
 * Date: 01/02/17
 */
public class ExclusiveAccessRandomLoadBalancer<Q, E> extends PairSupplierFactoryLoadBalancer<Q, E> {
    
    private final static Logger log = LoggerFactory.getLogger(ExclusiveAccessRandomLoadBalancer.class);
    
    public ExclusiveAccessRandomLoadBalancer(long randomSeed, PairSupplierFactory<Q, E> pairSupplierFactory) {
        this.randomSeed = new AtomicLong(randomSeed);
        setPairSupplierFactory(pairSupplierFactory);
    }
    
    private final AtomicLong randomSeed;
    
    public long getRandomSeed() {
        return randomSeed.get();
    }

    private ConcurrentHashMap<Integer, Pair<Q, E>> pairMap;
    private int pairsNumber;
    
    @Override
    public Iterator<Pair<Q, E>> provide() {
        return new AbstractIterator<Pair<Q, E>>() {
    
            private Random random = new Random(randomSeed.getAndIncrement());
            
            private Pair<Q, E> currentPair;
            private Integer currentIndex;
    
            @Override
            protected Pair<Q, E> computeNext() {
    
                if (currentPair != null && currentIndex != null) {
                    pairMap.put(currentIndex, currentPair);
                }
                
                long startTime = System.currentTimeMillis();
                long maxDurationInMinutes = 10;
                do {
                    currentIndex = random.nextInt(pairsNumber);
                    currentPair = pairMap.remove(currentIndex);
    
                    if (elapsedMinutes(startTime) > maxDurationInMinutes && currentPair == null) {
                        throw new IllegalStateException(String.format(
                                "Didn't manage to get the next pair during %s minutes",
                                String.valueOf(maxDurationInMinutes)));
                    }
                } while (currentPair == null);
    
    
                log.debug("For thread {} providing pair {}", Thread.currentThread(), currentPair);
                return currentPair;
            }
            
            private long elapsedMinutes(long since) {
    
                long seconds = (System.currentTimeMillis() - since) / 1000;
                
                return seconds / 60;
            }
        };
    }
    
    @Override
    public void setPairSupplierFactory(PairSupplierFactory<Q, E> pairSupplierFactory) {
        super.setPairSupplierFactory(pairSupplierFactory);
    
        PairSupplier<Q, E> pairSupplier = getPairSupplier();
        pairMap = new ConcurrentHashMap<>(pairSupplier.size());
        for (int i = 0; i < pairSupplier.size(); ++i) {
            pairMap.put(i, pairSupplier.get(i));
        }
        
        pairsNumber = pairSupplier.size();
        log.debug("{} pairs in total to balance", pairsNumber);
    }
}
