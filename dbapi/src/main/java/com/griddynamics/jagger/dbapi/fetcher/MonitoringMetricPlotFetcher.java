package com.griddynamics.jagger.dbapi.fetcher;

import static com.griddynamics.jagger.util.MonitoringIdUtils.splitMonitoringMetricId;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Deprecated
public class MonitoringMetricPlotFetcher extends AbstractMetricPlotFetcher {

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;
    private BiMap<String, String> newToOldMonitoringIds;
    private BiMap<String, String> oldToNewMonitoringIds;

    @Resource
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    @PostConstruct
    private void init() { // Needs to create newToOld/oldToNew MonitoringIds BiMaps. Calls via spring.
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

    /**
     * @param sessionIds is a collection of id of sessions
     * @param workloadTaskDataIds is a collection of workload task id
     * @param agentNames is a collection of all agent names
     * @param monitoringDescriptions is a collection of monitoring descriptions
     * @return  @return collection of objects {a description, a session id, an average value, a box identifier, an url of system under test, a task id}
     */
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
                        "        select test.id, mysome.taskDataId from " +
                        "          ( " +
                        "            select test.id, test.sessionId, test.taskId from TaskData as test where test.sessionId in (:sessionIds)" +
                        "          ) as test join " +
                        "          (" +
                        "            select mysome.parentId, pm.monitoringId, mysome.taskDataId, pm.sessionId from" +
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
                        "              ) as mysome on pm.sessionId = mysome.sessionId and pm.parentId=mysome.parentId" +
                        "          ) as mysome on test.sessionId = mysome.sessionId and test.taskId=mysome.monitoringId" +
                        "  ) as ids on ms.taskData_id = ids.id")
                .setParameter("sessionIds", sessionIds)
                .setParameter("taskIds", workloadTaskDataIds)
                .setParameter("agentNames", agentNames)
                .setParameter("descriptions", monitoringDescriptions)
                .getResultList();
    }
}