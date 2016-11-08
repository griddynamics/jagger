package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Provides a query like DELETE /jaas/dbs/{DB config id}. {Id} is the first entry one of the previously created records.
 * The queries are supposed to delete just-created-DB Config(s), so DELETE tests shall go after POST and PUT ones.
 */
public class QueryProvider_DELETE_DB extends QueryProvider_GET_DBsList {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryProvider_DELETE_DB.class);

    public QueryProvider_DELETE_DB() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty() && !TestContext.getCreatedDbConfigIds().isEmpty()) {
                queries.add(new JHttpQuery<String>().delete().path(uri + "/" + TestContext.getCreatedDbConfigIds().get(0)));
            } else {
                LOGGER.warn("Was not able to populate DELETE queries for DB configs. Possible reason - there is no just created DBs.");
            }

        return queries.iterator();
    }
}