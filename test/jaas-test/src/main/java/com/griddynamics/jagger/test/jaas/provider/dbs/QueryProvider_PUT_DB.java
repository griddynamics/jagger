package com.griddynamics.jagger.test.jaas.provider.dbs;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.util.entity.DbConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;

/**
 * Provides queries like PUT /jaas/dbs/{DB config data}.
 * The queries are supposed to modify just-created-DB Configs, so PUT tests shall go after POST ones.
 */
public class QueryProvider_PUT_DB extends QueryProvider_GET_DBsList {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryProvider_PUT_DB.class);

    public QueryProvider_PUT_DB() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            for (String id : TestContext.getCreatedDbConfigIds()) {
                DbConfigEntity conf = TestContext.provideFakeDbConfig_NoId();
                conf.setId(new Long(id));
                conf.setDesc("MODIFIED " + conf.getDesc());
                TestContext.addDbConfig(conf); //Assumption - POST test(s) passed and there are newly created records.

                queries.add((new JHttpQuery<String>()
                                .put()
                                .body(conf.toJson())
                                .header(HDR_CONTENT_TYPE, Collections.singletonList(HDR_CONTENT_TYPE_VALUE_APP_JSON)) //TODO: re-factor once JFG-934 is ready.
                                .path(uri + "/" + id)));
            }

            if (queries.isEmpty()) {
                LOGGER.warn("Was not able to populate PUT queries for DB configs. Possible reason - there is no just created DBs.");
            }
        }

        return queries.iterator();
    }
}