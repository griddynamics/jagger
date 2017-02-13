package com.griddynamics.jagger.invoker.batch;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class QueryBatch<Q> {
    private List<Q> queries;

    public QueryBatch(List<Q> queries) {
        this.queries = Objects.requireNonNull(queries);
    }

    public List<Q> getQueries() {
        return newArrayList(queries);
    }

    public int size() {
        return queries.size();
    }
}
