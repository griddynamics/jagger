package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class MetricDataServiceImpl extends RemoteServiceServlet implements MetricDataService {

    private HashMap<String, String> standardListeners = new HashMap<String, String>();

    public MetricDataServiceImpl(){
        standardListeners.put("Throughput", "throughput");
        standardListeners.put("Latency", "avgLatency");
        standardListeners.put("Duration", "totalDuration");
        standardListeners.put("Success rate", "successRate");
        standardListeners.put("Iterations", "samples");
    }

    @Override
    public Set<MetricNameDto> getMetricsNames( Set<TaskDataDto> tests) {
        HashSet<MetricNameDto> set = new HashSet<MetricNameDto>();
        for (TaskDataDto taskDataDto : tests){
            for (String standardMetricName : standardListeners.keySet()){
                MetricNameDto metric = new MetricNameDto();
                metric.setName(standardMetricName);
                metric.setTaskName(taskDataDto.getTaskName());
                set.add(metric);
            }
            set.addAll(getLatencyMetricsNames(taskDataDto));
            set.addAll(getCustomMetricsNames(taskDataDto));
        }
        return set;
    }

    @Override
    public MetricDto getMetric(TaskDataDto tests, MetricNameDto metricName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<MetricNameDto> getCustomMetricsNames(TaskDataDto tests){
        return Collections.EMPTY_SET;
    }

    public Set<MetricNameDto> getLatencyMetricsNames(TaskDataDto tests){
        return Collections.EMPTY_SET;
    }
}