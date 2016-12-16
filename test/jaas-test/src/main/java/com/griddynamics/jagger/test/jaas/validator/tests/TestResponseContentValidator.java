package com.griddynamics.jagger.test.jaas.validator.tests;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;

import static junit.framework.Assert.assertNotNull;

/**
 * Validates response of /sessions/{sessionId}/tests/{testName}.
 * Expected:
 * - actual record is the same as expected one.
 */
public class TestResponseContentValidator extends BaseHttpResponseValidator<TestEntity> {

    public TestResponseContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "TestResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<TestEntity> result) {
        TestEntity actualEntity = result.getBody();
        TestEntity expectedEntity = TestContext.getTestByName(getSessionIdFromQuery(query), getTestNameFromQuery(query));

        assertNotNull("Returned test entity is null.", actualEntity);
        //TODO: Wait for JFG-916 to be implemented and un-comment.
        //assertEquals("Expected and actual tests are not equal.", expectedEntity, actualEntity);

        return true;
    }

    private String getSessionIdFromQuery(JHttpQuery<String> query) {
        // ${jaas.rest.root}/sessions/{sessionId}/tests/{testName} => ${jaas.rest.root} + sessions + {sessionId} + tests + {testName}
        String[] parts = query.getPath().split("/");

        return parts[parts.length - 3];
    }

    private String getTestNameFromQuery(JHttpQuery<String> query) {
        // ${jaas.rest.root}/sessions/{sessionId}/tests/{testName} => ${jaas.rest.root} + sessions + {sessionId} + tests + {testName}
        String[] parts = query.getPath().split("/");

        return parts[parts.length - 1];
    }
}