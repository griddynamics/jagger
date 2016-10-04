package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.apache.http.client.methods.HttpGet;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides queries like /jaas/sessions/{sessioId}/tests/{testName}.
 */
public class QueryProvider_TestNames extends QueryProvider_TestsList {

    public QueryProvider_TestNames() {
        queries.addAll(TestContext.getTestsBySessionId(getTargetSessionId())
                .stream().map(t -> new HttpGet(getTestsPath() + "/" + t.getName()))
                .collect(Collectors.toList()));

    }

    @Override
    public Iterator iterator() {
        return queries.iterator();
    }
}