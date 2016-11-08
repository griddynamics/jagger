package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.util.entity.DbConfigEntity;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides queries like GET /jaas/dbs/{id} where {id} is an id of just created DB config (by previous test).
 */
public class QueryProvider_GET_CreatedDBIds extends QueryProvider_GET_DBsList {

    public QueryProvider_GET_CreatedDBIds() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.addAll(TestContext
                            .getCreatedDbConfigIds()
                            .stream()
                            .map(id -> new JHttpQuery<String>().get().responseBodyType(DbConfigEntity.class).path(uri + "/" + id))
                            .collect(Collectors.toList()));
        }

        return queries.iterator();
    }
}