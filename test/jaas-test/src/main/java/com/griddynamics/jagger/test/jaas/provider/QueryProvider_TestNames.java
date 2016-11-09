package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides queries like /jaas/sessions/{sessioId}/tests/{testName}.
 */
public class QueryProvider_TestNames extends QueryProvider_TestsList {

    public QueryProvider_TestNames() {}

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