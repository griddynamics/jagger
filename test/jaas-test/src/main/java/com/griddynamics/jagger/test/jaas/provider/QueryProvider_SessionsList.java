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
    private List<HttpRequestBase> queries = null;

    @Value( "${jaas.rest.base.sessions}" )
    protected String uri;

    public QueryProvider_SessionsList() {
    }

    @Override
    public Iterator iterator() {
        if (getQueries().isEmpty()) {
            queries.add(new HttpGet(uri)); //Have to that here since Spring's @Value does not provide the value upon constructing.
        }

        return queries.iterator();
    }

    protected List<HttpRequestBase> getQueries(){
        if (null == queries){
            queries = new LinkedList<>();
        }

        return queries;
    }
}