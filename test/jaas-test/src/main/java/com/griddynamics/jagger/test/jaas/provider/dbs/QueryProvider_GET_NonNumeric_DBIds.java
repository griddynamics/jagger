package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;

import java.util.Iterator;

/**
 * Provides queries like GET /jaas/dbs/{id} where {id} is invalid (a string).
 */
public class QueryProvider_GET_NonNumeric_DBIds extends QueryProvider_GET_DBsList {

    public QueryProvider_GET_NonNumeric_DBIds() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.add(new JHttpQuery<String>().get().responseBodyType(String.class).path(uri + "/abvgdeyka"));
            queries.add(new JHttpQuery<String>().get().responseBodyType(String.class).path(uri + "/ABVGD"));
        }

        return queries.iterator();
    }
}