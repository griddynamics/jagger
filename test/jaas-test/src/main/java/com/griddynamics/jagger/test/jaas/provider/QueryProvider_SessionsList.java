package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a query for /jaas/sessions resource which shall return list of available sessions.
 */
public class QueryProvider_SessionsList implements Iterable {
    protected List<JHttpQuery<String>> queries = new LinkedList<>();;

    @Value( "${jaas.rest.base.sessions}" )
    protected String uri;

    public QueryProvider_SessionsList() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            JHttpQuery<String> q = new JHttpQuery<String>().get().responseBodyType(SessionEntity[].class).path(uri);
            queries.add(q);
        }

        return queries.iterator();
    }
}