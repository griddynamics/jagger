package com.griddynamics.jagger.test.jaas.provider;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a query for /jaas/sessions resource which shall return list of available sessions.
 */
public class QueryProvider_SessionsList implements Iterable {
    protected List<HttpRequestBase> queries = new LinkedList<>();

    @Value( "${jaas.rest.base.sessions}" )
    protected String uri;

    public QueryProvider_SessionsList() {}

    @Override
    public Iterator iterator() {
        queries.add(new HttpGet(uri));
        return queries.iterator();
    }
}
