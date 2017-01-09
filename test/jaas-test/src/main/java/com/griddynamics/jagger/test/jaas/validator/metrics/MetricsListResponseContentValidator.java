package com.griddynamics.jagger.test.jaas.validator.metrics;

import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.test.jaas.validator.BaseHttpResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Validates response of /sessions/{sessionId}/tests/{testName}/metrics.
 * Expected:
 * - list of records is of size 1 and greater;
 * - the list's size is the same as the one's available via DataService;
 * - the list contains no duplicates;
 * - expected and actual sets are the same.
 */
public class MetricsListResponseContentValidator extends BaseHttpResponseValidator<MetricEntity[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsListResponseContentValidator.class);

    @Override
    public String getName() {
        return "MetricsListResponseContentValidator";
    }

    @Override
    public boolean isValid(JHttpQuery<String> query, JHttpEndpoint endpoint, JHttpResponse<MetricEntity[]> result) {
        List<MetricEntity> actualEntities = Arrays.asList(result.getBody());

        String sessionId = getSessionIdFromQuery(query);
        Set<MetricEntity> expectedEntities = TestContext.getMetricsBySessionIdAndTestName(sessionId, getTestNameFromQuery(query));
        int actlSize = actualEntities.size();
        int expctdSize = expectedEntities.size();
        assertTrue("At least one metrics record is expected. Check returned list's size", 0 < actlSize);
        List<MetricEntity> noDuplicatesActualList = actualEntities.stream().distinct().collect(Collectors.toList());
        assertEquals("Response contains duplicate records", actlSize, noDuplicatesActualList.size());
        assertEquals("Actual list's size is not the same as expected one's.", actlSize, expctdSize);

        //TODO: Refactor when JFG-943  is ready.
        assertTrue("Actual list is not the same as expected set.", compareIgnoringTestDto(expectedEntities, actualEntities));

        return true;
    }

    private String getSessionIdFromQuery(JHttpQuery query) {
        // ${jaas.rest.root}/sessions/{sessionId}/tests/{testName}/metrics => ${jaas.rest.root} + sessions + {sessionId} + tests + {testName} + metrics
        String[] parts = query.getPath().split("/");

        return parts[parts.length - 4];
    }

    private String getTestNameFromQuery(JHttpQuery query) {
        // ${jaas.rest.root}/sessions/{sessionId}/tests/{testName}/metrics => ${jaas.rest.root} + sessions + {sessionId} + tests + {testName} + metrics
        String[] parts = query.getPath().split("/");

        return parts[parts.length - 2];
    }

    private boolean compareIgnoringTestDto(Set<MetricEntity> expectedSet, List<MetricEntity> actualList) {
        boolean result = true;
        for (MetricEntity expected : expectedSet) {
            MetricEntity actual = actualList.stream().filter(m -> m.getMetricId().equals(expected.getMetricId())).findFirst().orElse(null);
            if (!areEqualIgnoringTestDto(expected, actual)) {
                result = false;
                LOGGER.warn("Metric entities are not equal. \nExp: {} \nAct: {}", expected, actual);
                break;
            }
        }

        return result;
    }

    /**
     * Compares MetricEntity ignoring internal "test" instances (to avoid comparison of uniqueIds which are not equal in our case).
     * See JFG-943.
     */
    private boolean areEqualIgnoringTestDto(MetricEntity ent1, MetricEntity ent2) {
        return ent1 == ent2 || !(ent2 == null || ent1.getClass() != ent2.getClass())
                && ent1.isPlotAvailable() == ent2.isPlotAvailable()
                && ent1.isSummaryAvailable() == ent2.isSummaryAvailable()
                && ent1.getMetricId().equals(ent2.getMetricId())
                && ent1.getDisplayName().equals(ent2.getDisplayName());


    }
}