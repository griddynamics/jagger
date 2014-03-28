package com.griddynamics.jagger.webclient.server.fetch.implementation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import com.griddynamics.jagger.webclient.client.dto.*;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

import static com.griddynamics.jagger.util.MonitoringIdUtils.*;

public class MonitoringMetricPlotFetcher extends AbstractMetricPlotFetcher {

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;
    private BiMap<String, String> newToOldMonitoringIds;
    private BiMap<String, String> oldToNewMonitoringIds;

    @Required
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    // Needs to create newToOld/oldToNew MonitoringIds BiMaps. Calls via spring.
    private void init() {
        initNewToOldMonitoringIds(monitoringPlotGroups);
    }

    private void initNewToOldMonitoringIds(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        newToOldMonitoringIds = HashBiMap.create();
        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> entry : monitoringPlotGroups.entrySet()) {
            for (DefaultMonitoringParameters defaultMonitoringParameter : entry.getValue()) {
                if (!newToOldMonitoringIds.containsKey(defaultMonitoringParameter.getId())) {
                    newToOldMonitoringIds.put(defaultMonitoringParameter.getId(), defaultMonitoringParameter.getDescription());
                }
            }
        }
        oldToNewMonitoringIds = newToOldMonitoringIds.inverse();
    }


    @Override
    protected Collection<MetricRawData> getAllRawData(List<MetricNameDto> metricNames) {

        Set<String> agentNames = new HashSet<String>();
        Set<String> monitoringDescriptions = new HashSet<String>();
        Set<String> sessionIds = new HashSet<String>();
        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto metricName : metricNames) {

            MonitoringIdUtils.MonitoringId monitoringId = splitMonitoringMetricId(metricName.getMetricName());
            if(monitoringId == null) {
                log.error("Could not split metricName of {} to MonitoringId. Will skip.", metricName);
                continue;
            }
            agentNames.add(monitoringId.getAgentName());
            String description = newToOldMonitoringIds.get(monitoringId.getMonitoringName());
            monitoringDescriptions.add(description == null ? monitoringId.getMonitoringName() : description);
            taskIds.addAll(metricName.getTaskIds());
            sessionIds.addAll(metricName.getTest().getSessionIds());
        }

        List<Object[]> rawDatas = getRawDataDbCall(sessionIds, taskIds, agentNames, monitoringDescriptions);

        if (rawDatas.isEmpty()) {
            log.warn("No plot data found for {}", metricNames);
            return Collections.emptyList();
        }

        List<MetricRawData> result = new ArrayList<MetricRawData>(rawDatas.size());
        for (Object[] raw : rawDatas) {
            MetricRawData metricRawData = new MetricRawData();

            metricRawData.setTime(((Number) raw[2]).longValue());
            metricRawData.setValue((Double) raw[3]);

            String monitoringName = (String) raw[0];
            if (oldToNewMonitoringIds.containsKey(monitoringName)) {
                monitoringName = oldToNewMonitoringIds.get(monitoringName);
            }
            String agentName = raw[4] == null ? (String) raw[5] : (String) raw[4];
            metricRawData.setMetricId(MonitoringIdUtils.getMonitoringMetricId(monitoringName, agentName));

            metricRawData.setWorkloadTaskDataId(((Number) raw[6]).longValue());
            metricRawData.setSessionId((String)raw[1]);

            result.add(metricRawData);
        }
        return result;
    }

    private List<Object[]> getRawDataDbCall(
            Collection<String> sessionIds, Collection<Long> workloadTaskDataIds,
            Collection<String> agentNames, Collection<String> monitoringDescriptions) {

        return entityManager.createNativeQuery(
                "select ms.description, ms.sessionId, ms.time, ms.averageValue, ms.boxIdentifier, ms.systemUnderTestUrl, ids.taskDataId  from " +
                        "  (" +
                        "    select * from MonitoringStatistics as ms " +
                        "    where ms.sessionId in (:sessionIds)" +
                        "    and ms.description in (:descriptions)" +
                        "    and (ms.boxIdentifier in (:agentNames) or ms.systemUnderTestUrl in (:agentNames))" +
                        "  ) as ms join " +
                        "  ( " +
                        "        select test.id, some.taskDataId from " +
                        "          ( " +
                        "            select test.id, test.sessionId, test.taskId from TaskData as test where test.sessionId in (:sessionIds)" +
                        "          ) as test join " +
                        "          (" +
                        "            select some.parentId, pm.monitoringId, some.taskDataId, pm.sessionId from" +
                        "              (" +
                        "                select pm.monitoringId, pm.sessionId, pm.parentId from PerformedMonitoring as pm where pm.sessionId in (:sessionIds) " +
                        "              ) as pm join " +
                        "              (" +
                        "                select td2.sessionId, td2.id as taskDataId, wd.parentId from" +
                        "                  ( " +
                        "                    select wd.parentId, wd.sessionId, wd.taskId from WorkloadData as wd where wd.sessionId in (:sessionIds)" +
                        "                  ) as wd join " +
                        "                    TaskData as td2" +
                        "                    on td2.id in (:taskIds)" +
                        "                    and wd.sessionId = td2.sessionId" +
                        "                    and wd.taskId=td2.taskId" +
                        "              ) as some on pm.sessionId = some.sessionId and pm.parentId=some.parentId" +
                        "          ) as some on test.sessionId = some.sessionId and test.taskId=some.monitoringId" +
                        "  ) as ids on ms.taskData_id = ids.id")
                .setParameter("sessionIds", sessionIds)
                .setParameter("taskIds", workloadTaskDataIds)
                .setParameter("agentNames", agentNames)
                .setParameter("descriptions", monitoringDescriptions)
                .getResultList();
    }
}