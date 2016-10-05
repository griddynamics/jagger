package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.apache.http.client.methods.HttpGet;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides queries like /jaas/sessions/{id}.
 */
public class QueryProvider_SessionIds extends QueryProvider_SessionsList {

    public QueryProvider_SessionIds() {
        getQueries().addAll(TestContext.getSessions().stream().map(s -> new HttpGet(uri + "/" + s.getId())).collect(Collectors.toList()));
    }

    @Override
    public Iterator iterator() {
        return getQueries().iterator();
    }
}
