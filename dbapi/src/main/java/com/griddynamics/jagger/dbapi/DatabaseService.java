package com.griddynamics.jagger.dbapi;


import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProvider;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;

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
    RootNode getControlTreeForSessions(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup) throws RuntimeException;

    /** Returns map <metricNode, plot values> for specific metric nodes from control tree
     * @param plots - set of metric nodes
     * @return plot values for metric nodes */
    Map<MetricNode, PlotSeriesDto> getPlotDataByMetricNode(Set<MetricNode> plots) throws IllegalArgumentException;

    /** Returns map <metricNameDto, plot values> for specific metric names
     * @param metricNames - set of metric names
     * @return plot values for metric names */
    Map<MetricNameDto, List<PlotDatasetDto>> getPlotDataByMetricNameDto(Set<MetricNameDto> metricNames) throws IllegalArgumentException;

    /** Returns summary values for current metrics
     * @param metricNames - metric names
     * @return list of summary values */
    List<MetricDto> getSummaryByMetricNameDto(List<MetricNameDto> metricNames);

    /** Returns test info for specify tests
     * @param taskDataDtos - selected tests
     * @return map of test info */
    Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfoByTaskDataDto(Collection<TaskDataDto> taskDataDtos) throws RuntimeException;

    /** Returns test info for specify tests ids
     * @param taskIds - selected test ids
     * @return map of test info */
    Map<Long, Map<String, TestInfoDto>> getTestInfoByTaskIds(Set<Long> taskIds) throws RuntimeException;

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

    /** Returns tests for specified session ids
     * @param sessionIds - selected sessions
     * @param sessionMatchingSetup - setup how to match sessions and what parameters to use for matching
     * @return SessionInfoProvider */
    List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup);

    /** Returns list of session ids corresponding to given task ids
     * @param taskIds TaskData ids
     * @return list of session Ids */
    List<String> getSessionIdsByTaskIds(Set<Long> taskIds);

    }
