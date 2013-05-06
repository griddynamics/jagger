package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public interface MetricDataServiceAsync {
    void getMetricsNames(Set<TaskDataDto> tests,AsyncCallback<Set<MetricNameDto>> async);
    void getMetric(MetricNameDto metricName, AsyncCallback<MetricDto> async);
    void getMetrics(List<MetricNameDto> metricNames, AsyncCallback<List<MetricDto>> async);
}
