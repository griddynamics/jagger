package com.griddynamics.jagger.test.jaas.provider.sessions;

import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.provider.QueryProvider_Abstract;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;

/**
 * Provides a query for /jaas/sessions resource which shall return list of available sessions.
 */
public class QueryProvider_GET_SessionsList extends QueryProvider_Abstract {
    @Value( "${jaas.rest.base.sessions}" )
    protected String uri;

    public QueryProvider_GET_SessionsList() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            JHttpQuery<String> q = new JHttpQuery<String>().get().responseBodyType(SessionEntity[].class).path(uri);
            queries.add(q);
        }

        return queries.iterator();
    }
}