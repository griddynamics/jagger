package com.griddynamics.jagger.test.jaas.validator.dbs;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.http.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.util.entity.DbConfigEntity;
import junit.framework.Assert;

/**
 * Validates response of /jaas/dbs/{id}.
 * Expected:
 * - response contains one DB config record only (done implicitly by casting the response entity to corresponding type);
 * - actual DB config record is the same as expected one.
 */
public class DBResponseContentValidator extends DBsListResponseContentValidator {

    public DBResponseContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "DBResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result)  {
        DbConfigEntity actualEntity = (DbConfigEntity)result.getBody();

        String[] queryParts = query.getPath().split("/"); //Get id from the query path.
        DbConfigEntity expectedEntity = TestContext.getDbConfig(Long.parseLong(queryParts[queryParts.length - 1]));

        Assert.assertEquals("Expected and actual DB Configs are not equal.", expectedEntity, actualEntity);

        return true;
    }
}