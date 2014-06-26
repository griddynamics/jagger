package com.griddynamics.jagger.dbapi;


import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProvider;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.util.Decision;

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
    Map<MetricNode, PlotIntegratedDto> getPlotDataByMetricNode(Set<MetricNode> plots) throws IllegalArgumentException;

    /** Returns map <metricNameDto, plot values> for specific metric names
     * @param metricNames - set of metric names
     * @return plot values for metric names */
    Map<MetricNameDto, List<PlotSingleDto>> getPlotDataByMetricNameDto(Set<MetricNameDto> metricNames) throws IllegalArgumentException;

    /** Returns map <metricNode, plot values> for specific metric nodes from control tree
     * @param metricNodes - set of metric nodes
     * @return plot values for metric nodes */
    Map<MetricNode, SummaryIntegratedDto> getSummaryByMetricNodes(Set<MetricNode> metricNodes) throws IllegalArgumentException;

    /** Returns summary values for current metrics
     * @param metricNames - metric names
     * @return list of summary values */
    Map<MetricNameDto, SummarySingleDto> getSummaryByMetricNameDto(Set<MetricNameDto> metricNames);

    /** Returns test info for specified tests
     * @param taskDataDtos - selected tests
     * @return map <taskDataDto, map <sessionId, test info>> */
    Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfoByTaskDataDto(Collection<TaskDataDto> taskDataDtos) throws RuntimeException;

    /** Returns test info for specified tests ids
     * @param taskIds - selected test ids
     * @return map <testId, map <sessionId, test info>> */
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
     * @return list of test info */
    List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup);

    /** Returns list of session ids corresponding to given task ids
     * @param taskIds TaskData ids
     * @return list of session Ids */
    List<String> getSessionIdsByTaskIds(Set<Long> taskIds);

    /** Returns list of test group task ids corresponding to given test task ids
     * @param taskIds TaskData ids
     * @return map <test-group id, set<tests ids>>
     */
    Map<Long, Set<Long>> getTestGroupIdsByTestIds(Set<Long> taskIds);

    /** Returns list of decisions (per test, test group) corresponding to given task ids
     * @param taskIds TaskData ids
     * @return set of decisions
     */
    Set<TaskDecisionDto> getDecisionsPerTask(Set<Long> taskIds);

    /** Returns map of decisions per metric corresponding to given metricId, sessionId
     * @param metricNames - set of metric names
     * @return map <metric, map <sessionId, decision>> of decisions
     */
    Map<MetricNameDto,Map<String,Decision>> getDecisionsPerMetric(Set<MetricNameDto> metricNames);

    /** Returns map of decisions per session corresponding to given sessionIds
     * @param sessionIds - selected sessions
     * @return map <sessionId, decision> of decisions
     */
    Map<String,Decision> getDecisionsPerSession(Set<String> sessionIds);

}
