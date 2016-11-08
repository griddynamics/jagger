package com.griddynamics.jagger.test.jaas.validator.dbs;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.http.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.util.entity.DbConfigEntity;
import junit.framework.Assert;

import java.util.Objects;

/**
 * Validates response of /jaas/dbs/{id} where  {id} is an id of just created DB Config record (by previous tes).
 * Expected:
 * - response contains one DB config record only (done implicitly by casting the response entity to corresponding type);
 * - actual DB config record is the same as expected one (id value is not to be checked).
 */
public class CreatedDBContentValidator extends DBsListResponseContentValidator {

    public CreatedDBContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "CreatedDBContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result)  {
        DbConfigEntity actualEntity = (DbConfigEntity)result.getBody();
        DbConfigEntity prototypeEntity = TestContext.getDbConfigPrototype();

        Assert.assertTrue("Prototype and actually created DB Configs are not equal.",
                equalsIgnoreId(prototypeEntity,actualEntity));

        return true;
    }

    public boolean equalsIgnoreId(DbConfigEntity expected, DbConfigEntity actual) {
        if (expected == actual) return true;
        if (!(actual instanceof DbConfigEntity)) return false;
        return  Objects.equals(expected.getDesc(), actual.getDesc()) &&
                Objects.equals(expected.getUrl(), actual.getUrl()) &&
                Objects.equals(expected.getUser(), actual.getUser()) &&
                Objects.equals(expected.getPass(), actual.getPass()) &&
                Objects.equals(expected.getJdbcDriver(), actual.getJdbcDriver()) &&
                Objects.equals(expected.getHibernateDialect(), actual.getHibernateDialect());
    }
}