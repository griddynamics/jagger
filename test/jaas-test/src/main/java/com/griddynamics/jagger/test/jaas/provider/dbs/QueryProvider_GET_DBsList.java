package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.provider.QueryProvider_Abstract;
import com.griddynamics.jagger.test.jaas.util.entity.DbConfigEntity;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;

/**
 * Provides a query like GET /jaas/dbs which shall return list of known DB configs.
 */
public class QueryProvider_GET_DBsList extends QueryProvider_Abstract {
    @Value( "${jaas.rest.base.dbs}" )
    protected String uri;

    public QueryProvider_GET_DBsList() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.add(new JHttpQuery<String>().get().responseBodyType(DbConfigEntity[].class).path(uri));
        }

        return queries.iterator();
    }
}