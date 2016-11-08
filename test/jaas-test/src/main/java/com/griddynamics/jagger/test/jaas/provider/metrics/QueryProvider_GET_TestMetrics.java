package com.griddynamics.jagger.test.jaas.provider.metrics;

import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.provider.tests.QueryProvider_GET_TestsList;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Provides a query for /jaas/sessions/{sessionId}/tests/{tesName}/metrics resource which shall return list of available metrics.
 */
@SuppressWarnings("unused")
public class QueryProvider_GET_TestMetrics extends QueryProvider_GET_TestsList {

    @Value("${jaas.rest.sub.tests.metrics}")
    private String metricsSubPath;

    private String targetSessionId = null;

    public QueryProvider_GET_TestMetrics() {
    }

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.add(new JHttpQuery<String>()
                    .get().responseBodyType(MetricEntity[].class)
                    .path(getMetricsPath()));
        }

        return queries.iterator();
    }

    protected String getMetricsPath() {
        return getTestsPath() + "/" + getTargetTestName() + metricsSubPath;
    }

    protected String getTargetTestName() {
        String sessionId = getTargetSessionId();
        return TestContext.getMetrics().get(sessionId).keySet().toArray(new String[]{})[0];
    }
}