package com.griddynamics.jagger.test.jaas.validator.dbs;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.http.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;
import junit.framework.Assert;

/**
 * Validates response of POST  /jaas/dbs/ .
 * Expected:
 * - Created record's id in the Location Header.
 */
public class CreateDBResponseValidator extends BaseHttpResponseValidator {

    public CreateDBResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "CreateDBResponseValidator";
    }

    @Override
    public boolean isValid(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result)  {
        String locationHdr = result.getHeaders().getFirst(HDR_LOCATION);
        Assert.assertNotNull("Location header shall not be null.", locationHdr);
        Assert.assertTrue("Location header's value shall be longer than query path's ",
                locationHdr.replace(endpoint.getURI().getPath(), "").length() > query.getPath().length());
        Assert.assertTrue("Location header's value shall contain original query's path.", locationHdr.contains(query.getPath()));

        String[] parts = locationHdr.split("/"); //Get Id from the query path.
        String theLast = parts[parts.length - 1];
        Assert.assertTrue("Location header's value shall end with a positive numeric id of a created record.",
                Integer.parseInt(theLast) > 0);

        TestContext.addCreatedDbConfigId(theLast); //Store it for further clean-up.

        return true;
    }
}