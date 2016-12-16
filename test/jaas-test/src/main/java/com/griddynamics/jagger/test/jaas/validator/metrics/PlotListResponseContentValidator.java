package com.griddynamics.jagger.test.jaas.validator.metrics;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricPlotPointEntity;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Validates response of /sessions/{sessionId}/tests/{testName}/metrics/plot-data.
 * Expected:
 * - list of records is of size 1 and greater;
 * - the list's size is the same as the one's available via DataService;
 * - expected and actual sets are the same.
 */
public class PlotListResponseContentValidator extends BaseHttpResponseValidator<Map<MetricEntity, List<MetricPlotPointEntity>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlotListResponseContentValidator.class);

    public PlotListResponseContentValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "PlotListResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<Map<MetricEntity, List<MetricPlotPointEntity>>> result) {
        Map<MetricEntity, List<MetricPlotPointEntity>> actualEntities = result.getBody();
        if (actualEntities == null) {
            LOGGER.warn("There are no plot data.");
            return false;
        }

        Map<MetricEntity, List<MetricPlotPointEntity>> expectedEntities = TestContext.getMetricPlotData();
        int actlSize = actualEntities.size();
        int expctdSize = expectedEntities.size();
        assertTrue("At least one record is expected. Check returned list's size", 0 < actlSize);
        assertEquals("Actual list's size is not the same as expected one's.", actlSize, expctdSize);

        //TODO: Un-comment when JFG-943 is ready.
        //assertEquals("Actual data is not the same as expected one.", actualEntities, expectedEntities);

        return true;
    }
}