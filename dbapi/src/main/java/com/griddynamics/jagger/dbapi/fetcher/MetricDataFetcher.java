package com.griddynamics.jagger.dbapi.fetcher;


import com.griddynamics.jagger.dbapi.dto.MetricNameDto;

import java.util.List;
import java.util.Set;

/**
 * Abstract class to fetch Data from list of MetricNameDto
 * @param <R>
 */
public abstract class MetricDataFetcher<R> {

    public abstract Set<R> getResult(List<MetricNameDto> metricNames);
}
