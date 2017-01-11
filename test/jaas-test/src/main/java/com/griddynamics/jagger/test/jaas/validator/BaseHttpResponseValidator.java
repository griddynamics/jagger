package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import junit.framework.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertTrue;

public abstract class BaseHttpResponseValidator<T>  implements ResponseValidatorProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHttpResponseValidator.class);

    @Override
    public ResponseValidator<?, ?, ?> provide(String sessionId, String taskId, NodeContext kernelContext) {
        String name = getName();
        return new ResponseValidator<JHttpQuery<String>, JHttpEndpoint, JHttpResponse<T>>(sessionId, taskId, kernelContext) {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean validate(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<T> result, long duration) {
                boolean isValid;
                try {
                    assertTrue("The response is not valid.", isValid(query, endpoint, result));
                    isValid = true;
                } catch (AssertionFailedError e) {
                    isValid = false;
                    LOGGER.warn("{}'s query response content is not valid, due to [{}].", query.toString(), e.getMessage());
                    LOGGER.warn(String.format
                            ("------> Failed response:\n%s \n%s",
                                    endpoint.toString(), result.toString()));
                }

                return isValid;
            }
        };

    }

    public abstract String getName();
    protected abstract boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<T> result);
}
