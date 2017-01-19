package com.griddynamics.jagger.invoker.batch;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class EndpointBatch<E> {
    private List<E> endpoints;

    public EndpointBatch(List<E> endpoints) {
        this.endpoints = Objects.requireNonNull(endpoints);
    }

    public List<E> getEndpoints() {
        return newArrayList(endpoints);
    }

    public int size() {
        return endpoints.size();
    }
}
