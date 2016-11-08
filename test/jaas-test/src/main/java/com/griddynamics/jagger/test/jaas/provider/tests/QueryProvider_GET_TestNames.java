package com.griddynamics.jagger.test.jaas.provider.tests;

import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides queries like /jaas/sessions/{sessioId}/tests/{testName}.
 */
public class QueryProvider_GET_TestNames extends QueryProvider_GET_TestsList {

    public QueryProvider_GET_TestNames() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.addAll(TestContext.getTestsBySessionId(getTargetSessionId())
                                .stream().map(t -> new JHttpQuery<String>().get().responseBodyType(TestEntity.class).path(getTestsPath() + "/" + t.getName()))
                                .collect(Collectors.toList()));
        }

        return queries.iterator();
    }
}