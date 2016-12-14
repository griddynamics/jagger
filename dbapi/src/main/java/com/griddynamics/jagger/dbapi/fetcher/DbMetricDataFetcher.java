package com.griddynamics.jagger.dbapi.fetcher;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class DbMetricDataFetcher<R> extends ConcurrentMetricDataFetcher<R> {

    protected EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
