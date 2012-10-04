package com.griddynamics.jagger.engine.e1.collector;

import com.google.common.base.Equivalence;

/**
 * @author imamontov
 */
public abstract class EquivalenceAdapter<T> extends Equivalence<T> {

    @Override
    public abstract boolean doEquivalent(T a, T b);

    @Override
    public abstract int doHash(T o);
}
