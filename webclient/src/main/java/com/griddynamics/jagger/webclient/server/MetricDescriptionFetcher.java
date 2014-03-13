package com.griddynamics.jagger.webclient.server;

import java.util.List;
import java.util.Set;

/**
 * Created by kgribov on 3/7/14.
 */
public interface MetricDescriptionFetcher {
    List<Object[]> getTestsMetricDescriptions(Set<Long> ids);
    List<Object[]> getTestGroupsMetricDescriptions(Set<Long> ids);
}
