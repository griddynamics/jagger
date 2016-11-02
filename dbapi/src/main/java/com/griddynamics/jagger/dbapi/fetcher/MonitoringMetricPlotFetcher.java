package com.griddynamics.jagger.dbapi.fetcher;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.griddynamics.jagger.util.MonitoringIdUtils.splitMonitoringMetricId;

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

        Set<String> agentNames = new HashSet<>();
        Set<String> monitoringDescriptions = new HashSet<>();
        Set<String> sessionIds = new HashSet<>();
        Set<Long> taskIds = new HashSet<>();
        for (MetricNameDto metricName : metricNames) {

            MonitoringIdUtils.MonitoringId monitoringId = splitMonitoringMetricId(metricName.getMetricName());
            if (monitoringId == null) {
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

        List<MetricRawData> result = new ArrayList<>(rawDatas.size());
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
            metricRawData.setSessionId((String) raw[1]);

            result.add(metricRawData);
        }
        return result;
    }

    /**
     * @param sessionIds             is a collection of id of sessions
     * @param workloadTaskDataIds    is a collection of workload task id
     * @param agentNames             is a collection of all agent names
     * @param monitoringDescriptions is a collection of monitoring descriptions
     * @return @return collection of objects {a description, a session id, an average value, a box identifier, an url of system under test, a task id}
     */
    private List<Object[]> getRawDataDbCall(
            Collection<String> sessionIds, Collection<Long> workloadTaskDataIds,
            Collection<String> agentNames, Collection<String> monitoringDescriptions) {

        return entityManager.createNativeQuery(
                "SELECT ms.description, ms.sessionId, ms.time, ms.averageValue, ms.boxIdentifier, ms.systemUnderTestUrl, ids.taskDataId  FROM " +
                        "  (" +
                        "    SELECT * FROM MonitoringStatistics AS ms " +
                        "    WHERE ms.sessionId IN (:sessionIds)" +
                        "    AND ms.description IN (:descriptions)" +
                        "    AND (ms.boxIdentifier IN (:agentNames) OR ms.systemUnderTestUrl IN (:agentNames))" +
                        "  ) AS ms JOIN " +
                        "  ( " +
                        "        SELECT test.id, mysome.taskDataId FROM " +
                        "          ( " +
                        "            SELECT test.id, test.sessionId, test.taskId FROM TaskData AS test WHERE test.sessionId IN (:sessionIds)" +
                        "          ) AS test JOIN " +
                        "          (" +
                        "            SELECT mysome.parentId, pm.monitoringId, mysome.taskDataId, pm.sessionId FROM" +
                        "              (" +
                        "                SELECT pm.monitoringId, pm.sessionId, pm.parentId FROM PerformedMonitoring AS pm WHERE pm.sessionId IN " +
                        "(:sessionIds) " +
                        "              ) AS pm JOIN " +
                        "              (" +
                        "                SELECT td2.sessionId, td2.id AS taskDataId, wd.parentId FROM" +
                        "                  ( " +
                        "                    SELECT wd.parentId, wd.sessionId, wd.taskId FROM WorkloadData AS wd WHERE wd.sessionId IN " +
                        "(:sessionIds)" +
                        "                  ) AS wd JOIN " +
                        "                    TaskData AS td2" +
                        "                    ON td2.id IN (:taskIds)" +
                        "                    AND wd.sessionId = td2.sessionId" +
                        "                    AND wd.taskId=td2.taskId" +
                        "              ) AS mysome ON pm.sessionId = mysome.sessionId AND pm.parentId=mysome.parentId" +
                        "          ) AS mysome ON test.sessionId = mysome.sessionId AND test.taskId=mysome.monitoringId" +
                        "  ) AS ids ON ms.taskData_id = ids.id")
                .setParameter("sessionIds", sessionIds)
                .setParameter("taskIds", workloadTaskDataIds)
                .setParameter("agentNames", agentNames)
                .setParameter("descriptions", monitoringDescriptions)
                .getResultList();
    }
}