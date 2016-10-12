package com.griddynamics.jagger.dbapi;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.NodeInfoPerSessionDto;
import com.griddynamics.jagger.dbapi.dto.PlotIntegratedDto;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.SummaryIntegratedDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.dto.TaskDecisionDto;
import com.griddynamics.jagger.dbapi.dto.TestInfoDto;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProvider;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.util.Decision;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provide API to work with jagger database
 *
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 */
public interface DatabaseService {

    /**
     * Returns control tree for selected sessions
     *
     * @param sessionIds - selected sessions
     * @return a pointer to the root element of tree
     */
    RootNode getControlTreeForSessions(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup)
            throws RuntimeException;

    /**
     * Returns map <metricNode, plot values> for specific metric nodes from control tree
     *
     * @param plots - set of metric nodes
     * @return plot values for metric nodes
     */
    Map<MetricNode, PlotIntegratedDto> getPlotDataByMetricNode(Set<MetricNode> plots) throws IllegalArgumentException;

    /**
     * Returns map <metricNameDto, plot values> for specific metric names
     *
     * @param metricNames - set of metric names
     * @return plot values for metric names
     */
    Map<MetricNameDto, List<PlotSingleDto>> getPlotDataByMetricNameDto(Set<MetricNameDto> metricNames)
            throws IllegalArgumentException;

    /**
     * Returns map <metricNode, plot values> for specific metric nodes from control tree
     *
     * @param metricNodes - set of metric nodes
     * @return plot values for metric nodes
     */
    Map<MetricNode, SummaryIntegratedDto> getSummaryByMetricNodes(Set<MetricNode> metricNodes,
                                                                  boolean isEnableDecisionsPerMetricFetching
    );

    /**
     * Returns summary values for current metrics
     *
     * @param metricNames - metric names
     * @return list of summary values
     */
    Map<MetricNameDto, SummarySingleDto> getSummaryByMetricNameDto(Set<MetricNameDto> metricNames,
                                                                   boolean isEnableDecisionsPerMetricFetching
    );

    /**
     * Returns test info for specified tests
     *
     * @param taskDataDtos - selected tests
     * @return map <taskDataDto, map <sessionId, test info>>
     */
    Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfoByTaskDataDto(Collection<TaskDataDto> taskDataDtos)
            throws RuntimeException;

    /**
     * Returns test info for specified tests ids
     *
     * @param taskIds - selected test ids
     * @return map <testId, map <sessionId, test info>>
     */
    Map<Long, Map<String, TestInfoDto>> getTestInfoByTaskIds(Set<Long> taskIds) throws RuntimeException;

    /**
     * Return information about session nodes
     *
     * @param sessionIds - selected sessions
     * @return a list of NodeInfoPerSessionDto
     */
    List<NodeInfoPerSessionDto> getNodeInfo(Set<String> sessionIds) throws RuntimeException;

    /**
     * Returns default monitoring parameters. See class DefaultMonitoringParameters
     */
    Map<String, Set<String>> getDefaultMonitoringParameters();

    /**
     * Returns SessionInfoProvider, which contains information about sessions
     *
     * @return SessionInfoProvider
     */
    SessionInfoProvider getSessionInfoService();

    /**
     * Returns tests for specified session ids
     *
     * @param sessionIds           - selected sessions
     * @param sessionMatchingSetup - setup how to match sessions and what parameters to use for matching
     * @return list of test info
     */
    List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup);

    /**
     * Returns list of session ids corresponding to given task ids
     *
     * @param taskIds TaskData ids
     * @return list of session Ids
     */
    List<String> getSessionIdsByTaskIds(Set<Long> taskIds);

    /**
     * Returns list of test group task ids corresponding to given test task ids
     *
     * @param taskIds TaskData ids
     * @return map <test-group id, set<tests ids>>
     */
    Map<Long, Set<Long>> getTestGroupIdsByTestIds(Set<Long> taskIds);

    /**
     * Returns list of decisions (per test, test group) corresponding to given task ids
     *
     * @param taskIds TaskData ids
     * @return set of decisions
     */
    Set<TaskDecisionDto> getDecisionsPerTask(Set<Long> taskIds);

    /**
     * Returns map of decisions per metric corresponding to given metricId, sessionId
     *
     * @param metricNames - set of metric names
     * @return map <metric, map <sessionId, decision>> of decisions
     */
    Map<MetricNameDto, Map<String, Decision>> getDecisionsPerMetric(Set<MetricNameDto> metricNames);

    /**
     * Returns map of decisions per session corresponding to given sessionIds
     *
     * @param sessionIds - selected sessions
     * @return map <sessionId, decision> of decisions
     */
    Map<String, Decision> getDecisionsPerSession(Set<String> sessionIds);

    /**
     * Checks the possibility of storing user comments
     *
     * @return true if it is possible to store user comments and false otherwise
     */
    boolean checkIfUserCommentStorageAvailable();

    /**
     * Checks the possibility of storing tags
     *
     * @return true if it is possible to store tags and false otherwise
     */
    boolean checkIfTagsStorageAvailable();

    /**
     * Returns task data, corresponding to defined pair of taskIs and sessionId
     *
     * @param taskId    - TaskData taskId
     * @param sessionId - session id
     * @return TaskData for selected params
     */
    TaskData getTaskData(String taskId, String sessionId);

    /**
     * Returns task data, corresponding to TaskData ids
     *
     * @param ids - TaskData ids
     * @return map <TaskData id, TaskData>
     */
    Map<Long, TaskData> getTaskData(Collection<Long> ids);
}
