package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

public class ResponseStatusValidator extends BaseHttpResponseValidator<String> {
    public ResponseStatusValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "ResponseStatusValidator";
    }

    /* Following method will be called after every successful invoke to validate result
     * @author Grid Dynamics
     *
     * @param query    - Query that was sent to endpoint
     * @param endpoint - Endpoint - service under test
     * @param result   - Result returned from endpoint
     * @param duration - Duration of invoke
     * */
    @Override
    public boolean validate(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result, long duration) {
        int statusCode = result.getStatus().value();

        if (statusCode != getExpectedStatusCode()) { //TODO: Need range/RegExp.
            logResponseAsFailed(
                    query,
                    endpoint,
                    result,
                    String.format("Unexpected response status code. %d instead of %d", statusCode, getExpectedStatusCode()));
            return false;
        }

        return true;
    }

    @Override
    protected boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<String> result) {
        return false; //Just a place-holder to re-use the base class.
    }

    /**
     * By default expected status code is 200. Should be changed in child classes.
     */
    protected int getExpectedStatusCode() {
        return 200;
    }
}