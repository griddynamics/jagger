package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.griddynamics.jagger.webclient.client.components.control.model.MonitoringPlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.MonitoringSessionScopePlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public interface CommonDataProvider {

    List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds);

    Map<TaskDataDto, List<MonitoringPlotNode>> getMonitoringPlotNodes(Set<String> sessionIds, List<TaskDataDto> task);

    List<MonitoringSessionScopePlotNode> getSessionScopeMonitoringPlotNodes(Set<String> sessionIds);

    Map<TaskDataDto,List<MetricNode>> getTestMetricsMap(List<TaskDataDto> tddos, ExecutorService threadPool);

    Map<TaskDataDto,List<PlotNode>> getTestPlotsMap(Set<String> sessionIds, List<TaskDataDto> taskList);

    boolean checkIfUserCommentStorageAvailable();
}
