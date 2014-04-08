package com.griddynamics.jagger.dbapi;


import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProvider;

import java.util.*;

/**
 * Created by kgribov on 4/4/14.
 */
public interface DatabaseService {

    RootNode getControlTreeForSessions(Set<String> sessionIds) throws RuntimeException;

    Map<MetricNode, PlotSeriesDto> getPlotData(Set<MetricNode> plots) throws IllegalArgumentException;

    List<MetricDto> getMetrics(List<MetricNameDto> metricNames);

    Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfos(Collection<TaskDataDto> taskDataDtos) throws RuntimeException;

    List<NodeInfoPerSessionDto> getNodeInfo(Set<String> sessionIds) throws RuntimeException;

    Map<String,Set<String>> getDefaultMonitoringParameters();
    WebClientProperties getWebClientProperties();

    SessionInfoProvider getSessionInfoService();

}
