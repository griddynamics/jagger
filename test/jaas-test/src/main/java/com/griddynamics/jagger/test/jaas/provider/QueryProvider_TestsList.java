package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;

/**
 * Provides a query for /jaas/sessions/{sessionId}/tests resource which shall return list of available tests.
 */
public class QueryProvider_TestsList extends QueryProvider_SessionsList {

    @Value( "${jaas.rest.sub.sessions.tests}" )
    protected String testsSubPath;

    private String targetSessionId=null;

    public QueryProvider_TestsList() {}

    @Override
    public Iterator iterator() {
        if (getQueries().isEmpty()) {
            getQueries().add(new HttpGet(getTestsPath())); //Have to that here since Spring's @Value does not provide the value upon constructing.
        }
        return getQueries().iterator();
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