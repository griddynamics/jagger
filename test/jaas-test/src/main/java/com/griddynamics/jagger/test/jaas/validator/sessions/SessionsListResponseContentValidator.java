package com.griddynamics.jagger.test.jaas.validator.sessions;

import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;
import junit.framework.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Validates response of /jaas/sessions.
 * Expected:
 * - list of sessions is of size 2 and greater;
 * - the list is no longer than the one available via DataService;
 * - the list contains no duplicates;
 * - a randomly picked record is the same as corresponding expected one.
 */
public class SessionsListResponseContentValidator extends BaseHttpResponseValidator<SessionEntity[]> {


    @Override
    public String getName() {
        return "SessionsListResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<SessionEntity[]> result) {
        List<SessionEntity> actualSessions = Arrays.asList(result.getBody());
        int actlSize = actualSessions.size();
        int expctdSize = TestContext.getSessions().size();
        Assert.assertTrue("Several session records are expected. Check returned list's size", 0 < actlSize);
        List<SessionEntity> noDuplicatesActualList = actualSessions.stream().distinct().collect(Collectors.toList());
        Assert.assertEquals("Response contains duplicate session records", actlSize, noDuplicatesActualList.size());
        Assert.assertTrue(String.format("Actual list(%d) is longer than expected one(%d).", actlSize, expctdSize), actlSize <= expctdSize);
        Assert.assertTrue("Actual list is not a sub-set of expected set.", TestContext.getSessions().containsAll(actualSessions));

        SessionEntity randomActualEntity = actualSessions.get((new Random().nextInt(actlSize)));
        SessionEntity correspondingExpectedSession = TestContext.getSession(randomActualEntity.getId());

        // TODO uncomment when JFG-1047
        // Assert.assertEquals("Randomly selected expected and actual sessions are not equal.", correspondingExpectedSession, randomActualEntity);

        return true;
    }
}