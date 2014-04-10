package com.griddynamics.jagger.dbapi;


import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProvider;

import java.util.*;

/** This class provide API to work with jagger database
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n */
public interface DatabaseService {

    /** Returns control tree for selected sessions
     * @param sessionIds - selected sessions
     * @return a pointer to the root element of tree */
    RootNode getControlTreeForSessions(Set<String> sessionIds) throws RuntimeException;

    /** Returns map <metricNode, plot values> for specific metric nodes from control tree
     * @param plots - set of metric nodes
     * @return plot values for metric nodes */
    Map<MetricNode, PlotSeriesDto> getPlotData(Set<MetricNode> plots) throws IllegalArgumentException;

    /** Returns summary values for current metrics
     * @param metricNames - metric names
     * @return list of summary values */
    List<MetricDto> getMetrics(List<MetricNameDto> metricNames);

    /** Returns test info for specify tests
     * @param taskDataDtos - selected tests
     * @return map of test infos */
    Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfos(Collection<TaskDataDto> taskDataDtos) throws RuntimeException;

    /** Return information about session nodes
     * @param sessionIds - selected sessions
     * @return a list of NodeInfoPerSessionDto */
    List<NodeInfoPerSessionDto> getNodeInfo(Set<String> sessionIds) throws RuntimeException;

    /** Returns default monitoring parameters. See class DefaultMonitoringParameters */
    Map<String,Set<String>> getDefaultMonitoringParameters();

    /** Returns dbapi properties
     * @return properties */
    WebClientProperties getWebClientProperties();

    /** Returns SessionInfoProvider, which contains information about sessions
     * @return SessionInfoProvider */
    SessionInfoProvider getSessionInfoService();
}
