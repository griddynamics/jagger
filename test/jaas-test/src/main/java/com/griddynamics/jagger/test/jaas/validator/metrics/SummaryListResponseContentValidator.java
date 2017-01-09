package com.griddynamics.jagger.test.jaas.validator.metrics;

import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Validates response of /sessions/{sessionId}/tests/{testName}/metrics/summary.
 * Expected:
 * - list of records is of size 1 and greater;
 * - the list's size is the same as the one's available via DataService;
 * - expected and actual sets are the same.
 */
public class SummaryListResponseContentValidator extends BaseHttpResponseValidator<Map<MetricEntity, MetricSummaryValueEntity>> {

    @Override
    public String getName() {
        return "SummaryListResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<Map<MetricEntity, MetricSummaryValueEntity>> result) {
        Map<MetricEntity, MetricSummaryValueEntity> actualEntities = result.getBody();

        Map<MetricEntity, MetricSummaryValueEntity> expectedEntities = TestContext.getMetricSummaries();
        int actlSize = actualEntities.size();
        int expctdSize = expectedEntities.size();
        assertTrue("At least one record is expected. Check returned list's size", 0 < actlSize);
        assertEquals("Actual list's size is not the same as expected one's.", actlSize, expctdSize);

        //TODO: Un-comment when JFG-943 is ready.
        //assertEquals("Actual data is not the same as expected one.", actualEntities, expectedEntities);

        return true;
    }
}