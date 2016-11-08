package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;

import java.util.Collections;
import java.util.Iterator;

/**
 * Provides queries like POST /jaas/dbs/{DB config data}.
 */
public class QueryProvider_POST_DB extends QueryProvider_GET_DBsList {

    public QueryProvider_POST_DB() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.add((new JHttpQuery<String>()
                        .post()
                        .body(TestContext.getDbConfigPrototype().toJson())
                        .header(HDR_CONTENT_TYPE, Collections.singletonList(HDR_CONTENT_TYPE_VALUE_APP_JSON)) //TODO: re-factor once JFG-934 is ready.
                        .path(uri)));
        }

        return queries.iterator();
    }
}