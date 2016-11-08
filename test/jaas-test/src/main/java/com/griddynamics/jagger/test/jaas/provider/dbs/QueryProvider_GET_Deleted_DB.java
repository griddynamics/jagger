package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Provides a query like GET /jaas/dbs/{id}. {Id} is a just deleted record ID..
 * The queries are supposed to request just-deleted-DB Config(s), so corresponding tests shall go after POST, PUT and DELETE  ones.
 */
public class QueryProvider_GET_Deleted_DB extends QueryProvider_GET_DBsList {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryProvider_GET_Deleted_DB.class);

    public QueryProvider_GET_Deleted_DB() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty() && !TestContext.getCreatedDbConfigIds().isEmpty()) {
                queries.add(new JHttpQuery<String>().get().path(uri + "/" + TestContext.getCreatedDbConfigIds().get(0)));
            } else {
                LOGGER.warn("Was not able to populate GET queries for just deleted DB config(s). Possible reason - there is no just created DBs.");
            }

        return queries.iterator();
    }
}