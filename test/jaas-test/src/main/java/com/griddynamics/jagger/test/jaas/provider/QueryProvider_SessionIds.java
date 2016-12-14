package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides queries like /jaas/sessions/{id}.
 */
public class QueryProvider_SessionIds extends QueryProvider_SessionsList {

    public QueryProvider_SessionIds() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.addAll(TestContext
                            .getSessions()
                            .stream()
                            .map(s -> new JHttpQuery<String>().get().responseBodyType(SessionEntity.class).path(uri + "/" + s.getId()))
                            .collect(Collectors.toList()));
        }

        return queries.iterator();
    }
}
