package com.griddynamics.jagger.test.jaas.validator.tests;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * [JFG-879]
 * Validates response of /sessions/{sessionId}/tests/.
 * Expected:
 * - list of tests is of size 1 and greater;
 * - the list's size is the same as the one's available via DataService;
 * - the list contains no duplicates;
 * - a randomly picked records is the same as corresponding expected one.
 */
public class TestsListResponseContentValidator extends BaseHttpResponseValidator<TestEntity[]> {

    public TestsListResponseContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "TestsListResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<TestEntity[]> result) {
        List<TestEntity> actualEntities = Arrays.asList(result.getBody());
        String sessionId = getSessionIdFromQuery(query);
        Set<TestEntity> expectedEntities = TestContext.getTestsBySessionId(sessionId);
        int actlSize = actualEntities.size();
        int expctdSize = expectedEntities.size();
        assertTrue("At least one test record is expected. Check returned list's size", 0 < actlSize);
        List<TestEntity> noDuplicatesActualList = actualEntities.stream().distinct().collect(Collectors.toList());
        assertEquals("Response contains duplicate records", actlSize, noDuplicatesActualList.size());
        assertEquals("Actual list's size is not the same as expected one's.", actlSize, expctdSize);
        //TODO: Wait for JFG-916 to be implemented and un-comment.
        //assertTrue("Actual list is not the same as expected set.", expectedEntities.containsAll(actualEntities));

        return true;
    }

    private String getSessionIdFromQuery(JHttpQuery query) {
        // ${jaas.rest.root}/sessions/{sessionId}/tests => ${jaas.rest.root} + sessions + {sessionId} + tests
        String[] parts = query.getPath().split("/");

        return parts[parts.length - 2];
    }
}