package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.entity.DbConfigEntity;

import java.util.Iterator;

/**
 * Provides queries like GET /jaas/dbs/{id} where {id} is invalid (does not exist).
 */
public class QueryProvider_GET_NonExisting_DBIds extends QueryProvider_GET_DBsList {

    public QueryProvider_GET_NonExisting_DBIds() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.add(new JHttpQuery<String>().get().responseBodyType(DbConfigEntity.class).path(uri + "/" + Integer.MAX_VALUE));
            queries.add(new JHttpQuery<String>().get().responseBodyType(DbConfigEntity.class).path(uri + "/" + Integer.MIN_VALUE));
        }

        return queries.iterator();
    }
}
