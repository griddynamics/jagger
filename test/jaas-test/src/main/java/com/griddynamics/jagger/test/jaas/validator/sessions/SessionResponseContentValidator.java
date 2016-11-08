package com.griddynamics.jagger.test.jaas.validator.sessions;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.http.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import junit.framework.Assert;

/**
 * Validates response of /jaas/sessions/{id}.
 * Expected:
 * - response contains one session record only;
 * - actual session record is the same as expected one.
 */
public class SessionResponseContentValidator extends SessionsListResponseContentValidator {

    public SessionResponseContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "SessionResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result)  {
        SessionEntity actualSession = (SessionEntity)result.getBody();

        String[] queryParts = query.getPath().split("/"); //Get SessionId from the query path.
        SessionEntity expectedSession = TestContext.getSession(queryParts[queryParts.length - 1]);

        Assert.assertEquals("Expected and actual session are not equal.", expectedSession, actualSession);

        return true;
    }
}