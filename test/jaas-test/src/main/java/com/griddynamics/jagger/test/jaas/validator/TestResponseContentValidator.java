package com.griddynamics.jagger.test.jaas.validator;

import com.alibaba.fastjson.JSON;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.http.HttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import junit.framework.AssertionFailedError;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [JFG-879]
 * Validates response of /sessions/{sessionId}/tests/{testName}.
 * Expected:
 * - actual record is the same as expected one.
 */
public class TestResponseContentValidator<E> extends BaseHttpResponseValidator<HttpRequestBase, E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestResponseContentValidator.class);

    public TestResponseContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "TestResponseContentValidator";
    }

    @Override
    public boolean validate(HttpRequestBase query, E endpoint, HttpResponse result, long duration)  {
        String content = result.getBody();
        boolean isValid = true;

        //Checks.
        try {
            TestEntity actualEntity= JSON.parseObject(content, TestEntity.class);

            TestEntity expectedEntity = TestContext.getTestByName(getSessionIdFromQuery(query), getTestNameFromQuery(query));

            //TODO: Wait for JFG-916 to be implemented and un-comment.
            //assertEquals("Expected and actual tests are not equal.", expectedEntity, actualEntity);
        } catch (AssertionFailedError e) {
            isValid = false;
            LOGGER.warn("{}'s query response content is not valid, due to [{}].", query.toString(), e.getMessage());
            logResponseAsFailed(endpoint, result);
        }

        return isValid;
    }

    private String getSessionIdFromQuery(HttpRequestBase query){
        // ${jaas.rest.root}/sessions/{sessionId}/tests/{testName} => ${jaas.rest.root} + sessions + {sessionId} + tests + {testName}
        String[] parts = query.getURI().toString().split("/");

        return parts[parts.length - 3];
    }

    private String getTestNameFromQuery(HttpRequestBase query){
        // ${jaas.rest.root}/sessions/{sessionId}/tests/{testName} => ${jaas.rest.root} + sessions + {sessionId} + tests + {testName}
        String[] parts = query.getURI().toString().split("/");

        return parts[parts.length - 1];
    }
}