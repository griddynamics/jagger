package com.griddynamics.jagger.test.jaas.validator.executions;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.entity.ExecutionEntity;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;
import junit.framework.Assert;


/**
 * Validates response of POST /executions
 * Expected:
 * - actual execution record is the same as expected one
 * - id from header and from body are equal
 * - header has `Location` and contains path from request
 */
public class CreateExecutionResponseValidator extends BaseHttpResponseValidator<ExecutionEntity> {

    @Override
    public String getName() {
        return "CreateExecutionResponseValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<ExecutionEntity> result) {
        String locationHdr = result.getHeaders().getFirst("Location");
        Assert.assertNotNull("Location header shall not be null.", locationHdr);
        Assert.assertTrue("Location header's value shall contain original query's path.", locationHdr.contains(query.getPath()));

        String[] parts = locationHdr.split("/"); //Get Id from the query path.
        Long theLast = Long.parseLong(parts[parts.length - 1]);
        Assert.assertTrue("Location header's value shall end with a positive numeric id of a created record.",
                theLast > 0);

        ExecutionEntity expected = ExecutionEntity.getDefault();
        expected.setId(theLast);
        expected.setStatus(ExecutionEntity.TestExecutionStatus.PENDING);

        ExecutionEntity actual = result.getBody();

        Assert.assertEquals("Response execution is as expected", expected, actual);

        return true;
    }
}