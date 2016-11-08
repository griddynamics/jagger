package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.http.v2.JHttpResponse;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * Validates 400 responses.
 *
 * Expected:
 * - response entity contains some error explanation text.
  */
public class BadRequest_ResponseContentValidator extends BaseHttpResponseValidator {

    public BadRequest_ResponseContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "BadRequest_ResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result)  {
        String actualEntity = (String)result.getBody();
        Assert.assertTrue(actualEntity.toLowerCase().contains("error page"));
        Assert.assertTrue(actualEntity.contains("NumberFormatException"));

        return true;
    }
}