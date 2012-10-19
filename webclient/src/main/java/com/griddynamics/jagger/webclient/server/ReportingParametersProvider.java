package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;

import java.util.Collection;
import java.util.Map;

/**
 * @author Vladimir Kondrashchenko
 */
public interface ReportingParametersProvider {

    Map<GroupKey, MonitoringParameter[]> getMergedMonitoringParameters(Long taskId);

    Map<GroupKey, MonitoringParameter[]> getMergedMonitoringParameters(Collection<Long> taskIds);

    Map<GroupKey, MonitoringParameter[]> getMergedMonitoringParameters(String sessionId);
}
