package com.griddynamics.jagger.test.jaas.invoker;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.util.entity.ExecutionEntity;
import org.springframework.http.HttpMethod;

/**
 * Perform POST and DELETE requests for executions.
 * Manage list of available execution ids which used for validation and composing requests.
 */
public class ExecutionManipulateInvoker extends InvokerWithoutStatusCodeValidation {
    @Override
    public JHttpResponse invoke(JHttpQuery query, JHttpEndpoint endpoint) throws InvocationException {
        if (query.getMethod().equals(HttpMethod.DELETE)) {
            String path = query.getPath();
            Long id = Long.valueOf(path.substring(path.lastIndexOf("/") + 1));
            TestContext.getCreatedExecutionIds().remove(id);
            TestContext.setFirstRemovedExecution(id);
        }

        JHttpResponse result = super.invoke(query, endpoint);

        if (query.getMethod().equals(HttpMethod.POST) && result.getStatus().is2xxSuccessful()) {
            TestContext.addCreatedExecutionId(((ExecutionEntity) result.getBody()).getId());
        }
        return result;
    }
}
