package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.http.HttpResponse;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

public class ResponseStatusValidator<Q, E> extends BaseHttpResponseValidator<Q, E> {

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
    public boolean validate(Q query, E endpoint, JHttpResponse result, long duration)  {
        int statusCode = result.getStatus().value();

        if (statusCode != 200) { //TODO: Generify, make the expected value configurable.
            logResponseAsFailed(endpoint, result);
            return  false;
        }

        return true;
    }
}