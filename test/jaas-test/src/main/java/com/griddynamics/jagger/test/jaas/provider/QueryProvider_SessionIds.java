package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.apache.http.client.methods.HttpGet;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides queries like /jaas/sessions/{id}.
 */
public class QueryProvider_SessionIds extends QueryProvider_SessionsList {

    public QueryProvider_SessionIds() {}

    @Override
    public Iterator iterator() {
        queries.addAll(TestContext.getSessions().stream().map(s -> new HttpGet(uri + "/" + s.getId())).collect(Collectors.toList()));

        return queries.iterator();
    }
}
