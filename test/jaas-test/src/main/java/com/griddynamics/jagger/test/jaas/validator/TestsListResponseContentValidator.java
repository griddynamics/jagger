package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import junit.framework.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TestsListResponseContentValidator<E> extends BaseHttpResponseValidator<JHttpQuery<String>, E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestsListResponseContentValidator.class);

    public TestsListResponseContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "TestsListResponseContentValidator";
    }

    @Override
    public boolean validate(JHttpQuery<String> query, E endpoint, JHttpResponse result, long duration)  {
        List<TestEntity> actualEntities = Arrays.asList((TestEntity[]) result.getBody());

        boolean isValid = false;

        //Checks.
        try {
            String sessionId = getSessionIdFromQuery(query);
            Set<TestEntity> expectedEntities =  TestContext.getTestsBySessionId(sessionId);
            int actlSize = actualEntities.size();
            int expctdSize = expectedEntities.size();
            assertTrue("At least one test record is expected. Check returned list's size", 0 < actlSize);
            List<TestEntity> noDuplicatesActualList = actualEntities.stream().distinct().collect(Collectors.toList());
            assertEquals("Response contains duplicate records", actlSize, noDuplicatesActualList.size());
            assertEquals("Actual list's size is not the same as expected one's.", actlSize, expctdSize);
            //TODO: Wait for JFG-916 to be implemented and un-comment.
            //assertTrue("Actual list is not the same as expected set.", expectedEntities.containsAll(actualEntities));
            isValid = true;
        } catch (AssertionFailedError e) {
            isValid = false;
            LOGGER.warn("{}'s query response content is not valid, due to [{}].", query.toString(), e.getMessage());
            logResponseAsFailed(endpoint, result);
        }

        return isValid;
    }

    private String getSessionIdFromQuery(JHttpQuery query){
        // ${jaas.rest.root}/sessions/{sessionId}/tests => ${jaas.rest.root} + sessions + {sessionId} + tests
        // TODO : re-factor
        String[] parts = query.getPath().split("/");

        return parts[parts.length - 2];
    }
}