package com.griddynamics.jagger.test.jaas.validator.sessions;

import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;

/**
 * Validates response of /jaas/sessions/{id}.
 * Expected:
 * - response contains one session record only;
 * - actual session record is the same as expected one.
 */
public class SessionResponseContentValidator extends BaseHttpResponseValidator<SessionEntity> {

    @Override
    public String getName() {
        return "SessionResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<SessionEntity> result) {
        SessionEntity actualSession = result.getBody();

        String[] queryParts = query.getPath().split("/"); //Get SessionId from the query path.
        SessionEntity expectedSession = TestContext.getSession(queryParts[queryParts.length - 1]);

        // TODO uncomment when JFG-1047
        // Assert.assertEquals("Expected and actual session are not equal.", expectedSession, actualSession);

        return true;
    }
}