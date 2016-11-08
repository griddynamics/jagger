package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.util.entity.DbConfigEntity;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides queries like GET /jaas/dbs/{id}.
 */
public class QueryProvider_GET_DBIds extends QueryProvider_GET_DBsList {

    public QueryProvider_GET_DBIds() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.addAll(TestContext
                            .getDbConfigs()
                            .stream()
                            .map(c -> new JHttpQuery<String>().get().responseBodyType(DbConfigEntity.class).path(uri + "/" + c.getId()))
                            .collect(Collectors.toList()));
        }

        return queries.iterator();
    }
}
