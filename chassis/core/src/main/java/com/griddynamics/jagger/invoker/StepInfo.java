package com.griddynamics.jagger.invoker;

/**
 * Created by kgribov on 2/18/14.
 */
public class StepInfo<Q, E> {
    private Q query;
    private E endpoint;

    public StepInfo(Q query, E endpoint) {
        this.query = query;
        this.endpoint = endpoint;
    }

    public Q getQuery() {
        return query;
    }

    public void setQuery(Q query) {
        this.query = query;
    }

    public E getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(E endpoint) {
        this.endpoint = endpoint;
    }
}
