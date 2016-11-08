package com.griddynamics.jagger.test.jaas.provider.metrics;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;
import java.util.Map;

/**
 * Provides a query for /jaas/sessions/{sessionId}/tests/{tesName}/metrics/plot-data resource which shall return list of available plots.
 */
@SuppressWarnings("unused")
public class QueryProvider_GET_MetricPlotData extends QueryProvider_GET_TestMetrics {

    @Value("${jaas.rest.sub.tests.metrics_plot_data}")
    private String plotSubPath;

    public QueryProvider_GET_MetricPlotData() {
    }

    @Override
    public Iterator iterator() {
        if (queries.isEmpty()) {
            queries.add(new JHttpQuery<String>()
                    .get().responseBodyType(Map.class)
                    .path(getPlotsPath()));
        }

        return queries.iterator();
    }

    private String getPlotsPath() {
        return getMetricsPath() + plotSubPath;
    }
}