package com.griddynamics.jagger.dbapi.provider;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

public interface MetricNameProvider {
    public Set<MetricNameDto> getMetricNames(List<TaskDataDto> tests);
}
