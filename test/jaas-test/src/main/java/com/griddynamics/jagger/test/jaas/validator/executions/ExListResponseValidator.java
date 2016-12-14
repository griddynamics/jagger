package com.griddynamics.jagger.test.jaas.validator.executions;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.util.entity.ExecutionEntity;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;
import junit.framework.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates response of GET /executions
 * Expected:
 * - there are more then one record
 * - there are no duplicates
 * - there are all expected records
 * - a randomly picked record is the same as corresponding expected one.
 */
public class ExListResponseValidator extends BaseHttpResponseValidator<ExecutionEntity[]> {

    public ExListResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "ExListResponseValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<ExecutionEntity[]> result) {
        List<ExecutionEntity> actualEntities = Arrays.asList(result.getBody());
        Set<Long> expectedIds = TestContext.getCreatedExecutionIds();

        int actlSize = actualEntities.size();

        Assert.assertTrue("Several execution records are expected. Check returned list's size", 1 < actlSize);

        Set<Long> noDuplicatesIds = actualEntities.stream().map(ExecutionEntity::getId).collect(Collectors.toSet());
        Assert.assertEquals("Response contains duplicate execution records", actlSize, noDuplicatesIds.size());
        Assert.assertTrue("Size of actual list should be not less than expected one.", expectedIds.size() <= actlSize);

        List<Long> actualIds = actualEntities.stream().map(ExecutionEntity::getId).collect(Collectors.toList());
        Assert.assertTrue("Actual set of executions should contains all expected ids.",
                expectedIds.stream().allMatch(actualIds::contains));

        Long randomId = (Long) expectedIds.toArray()[new Random().nextInt(expectedIds.size())];

        ExecutionEntity randomActualEntity = actualEntities.stream().filter(e -> e.getId().equals(randomId)).findFirst().orElse(null);
        ExecutionEntity expected = ExecutionEntity.getDefault();

        Assert.assertEquals("Randomly selected actual execution has unexpected value", expected.getEnvId(), randomActualEntity.getEnvId());
        Assert.assertEquals("Randomly selected actual execution has unexpected value", ExecutionEntity.TestExecutionStatus.PENDING, randomActualEntity.getStatus());
        Assert.assertEquals("Randomly selected actual execution has unexpected value", expected.getExecutionStartTimeoutInSeconds(), randomActualEntity.getExecutionStartTimeoutInSeconds());
        Assert.assertEquals("Randomly selected actual execution has unexpected value", expected.getLoadScenarioId(), randomActualEntity.getLoadScenarioId());

        return true;
    }
}