package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import junit.framework.Assert;

/**
 * Validates 400 responses.
 * <p>
 * Expected:
 * - response entity contains some error explanation text.
 */
public class BadRequest_ResponseContentValidator extends BaseHttpResponseValidator<String> {
    @Override
    public String getName() {
        return "BadRequest_ResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<String> result) {
        String actualEntity = result.getBody();
        Assert.assertTrue(actualEntity.contains("NumberFormatException"));
        return true;
    }
}