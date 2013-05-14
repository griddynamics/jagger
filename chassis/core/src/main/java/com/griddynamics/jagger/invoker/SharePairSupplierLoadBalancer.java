package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.Pair;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: amikryukov
 * Date: 5/14/13
 */
public abstract class SharePairSupplierLoadBalancer<Q, E> extends QueryPoolLoadBalancer<Q, E> {

    PairSupplier<Q, E> pairSupplier = null;

    public SharePairSupplierLoadBalancer() {
        super();
    }

    public SharePairSupplierLoadBalancer(Iterable<Q> queryProvider, Iterable<E> endpointProvider) {
        super(queryProvider, endpointProvider);
    }

    public void setPairSupplier(PairSupplier<Q, E> pairSupplier) {
        this.pairSupplier = pairSupplier;
    }

    @Override
    public Iterator<Pair<Q, E>> provide() {

        return new Iterator<Pair<Q, E>>() {

            private int size = getPairSupplier().size();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Pair<Q, E> next() {
                if(index >= size) {
                    index = 0;
                }
                return getPairSupplier().get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Read only iterator");
            }

            @Override
            public String toString() {
                return "SharedPairSupplierLoadBalancer iterator";
            }
        };
    }

    abstract public PairSupplier<Q, E> getPairSupplier();
}
