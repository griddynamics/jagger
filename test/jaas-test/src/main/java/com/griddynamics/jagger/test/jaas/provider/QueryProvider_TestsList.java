package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a query for /jaas/sessions/{sessionId}/tests resource which shall return list of available tests.
 */
public class QueryProvider_TestsList extends QueryProvider_SessionsList {
    protected List<HttpRequestBase> queries = new LinkedList<>();

    @Value( "${jaas.rest.sub.sessions.tests}" )
    protected String testsSubPath;

    private String targetSessionId=null;

    public QueryProvider_TestsList() {}

    @Override
    public Iterator iterator() {
        queries.add(new HttpGet(getTestsPath()));
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