package com.griddynamics.jagger.test.jaas.provider.tests;

import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.provider.sessions.QueryProvider_GET_SessionsList;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;

/**
 * Provides a query for /jaas/sessions/{sessionId}/tests resource which shall return list of available tests.
 */
@SuppressWarnings("unused")
public class QueryProvider_GET_TestsList extends QueryProvider_GET_SessionsList {

    @Value( "${jaas.rest.sub.sessions.tests}" )
    private String testsSubPath;

    private String targetSessionId=null;

    public QueryProvider_GET_TestsList() {}

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            JHttpQuery<String> q = new JHttpQuery<String>().get().responseBodyType(TestEntity[].class).path(getTestsPath());
            queries.add(q);
        }

        return queries.iterator();
    }

    protected String getTestsPath(){
        return uri + "/" + getTargetSessionId() + testsSubPath;
    }

    protected String getTargetSessionId(){
        if (null == targetSessionId){
            targetSessionId = (TestContext.getTests().keySet().toArray(new String[]{}))[0];
        }

        return targetSessionId;
    }
}