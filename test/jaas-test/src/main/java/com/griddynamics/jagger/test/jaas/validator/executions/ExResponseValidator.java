package com.griddynamics.jagger.test.jaas.validator.executions;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.entity.ExecutionEntity;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;

/**
 * Validates response of GET /executions/{testExecutionId}
 * Expected:
 * - actual execution record is the same as expected one
 */
public class ExResponseValidator extends BaseHttpResponseValidator<ExecutionEntity> {

    @Override
    public String getName() {
        return "getExecutionResponseValidator";
    }

    @Override
    protected boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<ExecutionEntity> result) {
        ExecutionEntity actual = result.getBody();

        String[] parts = query.getPath().split("/");
        ExecutionEntity expected = ExecutionEntity.getDefault();
        expected.setId(Long.parseLong(parts[parts.length - 1]));
        expected.setStatus(ExecutionEntity.TestExecutionStatus.PENDING);

        return expected.equals(actual);
    }
}
