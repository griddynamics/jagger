package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.CollectorDescription;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.webclient.client.components.control.model.MonitoringPlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.MonitoringSessionScopePlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.SessionPlotNode;
import com.griddynamics.jagger.webclient.client.data.MetricRankingProvider;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.SessionPlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.server.plot.CustomMetricPlotDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

import static com.griddynamics.jagger.webclient.client.mvp.NameTokens.*;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class CommonDataProviderImpl implements CommonDataProvider {

    private EntityManager entityManager;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private CustomMetricPlotDataProvider customMetricPlotDataProvider;
    private Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups;
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    public Map<GroupKey, DefaultMonitoringParameters[]> getMonitoringPlotGroups() {
        return monitoringPlotGroups;
    }

    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public Map<GroupKey, DefaultWorkloadParameters[]> getWorkloadPlotGroups() {
        return workloadPlotGroups;
    }

    public void setWorkloadPlotGroups(Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups) {
        this.workloadPlotGroups = workloadPlotGroups;
    }

    public CustomMetricPlotDataProvider getCustomMetricPlotDataProvider() {
        return customMetricPlotDataProvider;
    }

    public void setCustomMetricPlotDataProvider(CustomMetricPlotDataProvider customMetricPlotDataProvider) {
        this.customMetricPlotDataProvider = customMetricPlotDataProvider;
    }

    private HashMap<String, Pair<String, String>> standardMetrics;

    @Required
    public void setStandardMetrics(HashMap<String, Pair<String, String>> standardMetrics) {
        this.standardMetrics = standardMetrics;
    }

    @Override
    public Set<MetricNameDto> getMetricNames(Set<TaskDataDto> tests) {
        Long time = System.currentTimeMillis();
        HashSet<MetricNameDto> set = new HashSet<MetricNameDto>();
        for (TaskDataDto taskDataDto : tests){
            for (String standardMetricName : standardMetrics.keySet()){
                MetricNameDto metric = new MetricNameDto();
                metric.setName(standardMetricName);
                metric.setTests(taskDataDto);
                set.add(metric);
            }
            set.addAll(getLatencyMetricsNames(taskDataDto));
            set.addAll(getCustomMetricsNames(taskDataDto));
        }
        log.info("For tasks {} was found {} metrics names for {} ms", new Object[]{tests, set.size(), System.currentTimeMillis() - time});
        return set;
    }

    public Set<MetricNameDto> getCustomMetricsNames(TaskDataDto tests){

        Set<MetricNameDto> metrics = new HashSet<MetricNameDto>();

        System.out.println("task_id : " + tests.getTaskName() + " : " + tests.getIds());

        // check new model
        List<CollectorDescription> collectorDescriptions = entityManager.createQuery("select c from CollectorDescription c where c.taskData.id in (:ids)")
                .setParameter("ids", tests.getIds()).getResultList();

        System.out.println("colDescrs size : " + collectorDescriptions.size());
        for (CollectorDescription name : collectorDescriptions){
            if (name == null) continue;

            MetricNameDto metric = new MetricNameDto();
            metric.setTests(tests);
            metric.setName(name.getName());
            metric.setDisplayName(name.getDisplayName());

            metrics.add(metric);
        }
        // check old model
        List<String> metricNames = entityManager.createNativeQuery("select metric.name from DiagnosticResultEntity as metric " +
                "where metric.workloadData_id in " +
                "(select workloadData.id from WorkloadData as workloadData " +
                "inner join (select id, taskId, sessionId from TaskData where id in (:ids)) as taskData on " +
                "workloadData.taskId=taskData.taskId and workloadData.sessionId=taskData.sessionId)")
                .setParameter("ids", tests.getIds()).getResultList();

        for (String name : metricNames){
            if (name == null) continue;

            MetricNameDto metric = new MetricNameDto();
            metric.setTests(tests);
            metric.setName(name);

            if (metrics.contains(metric)) {
                continue;
            }
            metrics.add(metric);
        }

        List<Object[]> validatorNames = entityManager.createNativeQuery("select metric.validator, metric.displayName from ValidationResultEntity as metric " +
                "where metric.workloadData_id in " +
                "(select workloadData.id from WorkloadData as workloadData " +
                "inner join (select id, taskId, sessionId from TaskData where id in (:ids)) as taskData on " +
                "workloadData.taskId=taskData.taskId and workloadData.sessionId=taskData.sessionId)")
                .setParameter("ids", tests.getIds()).getResultList();

        for (Object[] name : validatorNames){
            if (name == null) continue;

            MetricNameDto validator = new MetricNameDto();
            validator.setTests(tests);
            validator.setName((String)name[0]);
            validator.setDisplayName((String)name[1]);

            if (metrics.contains(validator)) {
                if (validator.getDisplayName() != null) {
                    metrics.remove(validator);
                    metrics.add(validator);
                }
            } else {

                metrics.add(validator);
            }
        }

        return metrics;
    }

    public Set<MetricNameDto> getLatencyMetricsNames(TaskDataDto tests){
        Set<MetricNameDto> latencyNames;

        List<WorkloadProcessLatencyPercentile> latency = entityManager.createQuery(
                "select s from  WorkloadProcessLatencyPercentile as s where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                        "group by s.percentileKey " +
                        "having count(s.id)=:size")
                .setParameter("taskIds", tests.getIds())
                .setParameter("size", (long) tests.getIds().size())
                .getResultList();

        latencyNames = new HashSet<MetricNameDto>(latency.size());
        if (!latency.isEmpty()){
            for(WorkloadProcessLatencyPercentile percentile : latency) {
                MetricNameDto dto = new MetricNameDto();
                dto.setName("Latency "+Double.toString(percentile.getPercentileKey())+" %");
                dto.setTests(tests);
                latencyNames.add(dto);
            }
        }
        return latencyNames;
    }


    @Override
    public Set<PlotNameDto> getPlotNames(Set<String> sessionIds, TaskDataDto taskDataDto) {
        Set<PlotNameDto> plotNameDtoSet = new LinkedHashSet<PlotNameDto>();
        try {
            if (isWorkloadStatisticsAvailable(sessionIds, taskDataDto)) {
                for (Map.Entry<GroupKey, DefaultWorkloadParameters[]> monitoringPlot : workloadPlotGroups.entrySet()) {
                    plotNameDtoSet.add(new PlotNameDto(taskDataDto, monitoringPlot.getKey().getUpperName()));
                }
            }

            List<PlotNameDto> customMetrics = customMetricPlotDataProvider.getPlotNames(taskDataDto);
            plotNameDtoSet.addAll(customMetrics);

            log.debug("For sessions {} are available these plots: {}", sessionIds, plotNameDtoSet);
        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds + ", task name " + taskDataDto.getTaskName(), e);
            throw new RuntimeException(e);
        }

        return plotNameDtoSet;
    }

    @Override
    public List<MonitoringPlotNode> getMonitoringPlotNodes(Set<String> sessionIds, TaskDataDto taskDataDto) {
        Set<MonitoringPlotNode> monitoringPlotNodes = new LinkedHashSet<MonitoringPlotNode>();
        try {
            List<Long> monitoringIds = getMonitoringIds(sessionIds, taskDataDto);
            if (monitoringIds.isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : monitoringPlotGroups.entrySet()) {

                List<PlotNode> plotNodes = new ArrayList<PlotNode>();

                plotNodes.addAll(getMonitoringPlotNames(sessionIds, monitoringPlot, taskDataDto, monitoringIds));

                if (plotNodes.isEmpty()) {
                    continue;
                }

                MonitoringPlotNode monitoringPlotNode = new MonitoringPlotNode();
                monitoringPlotNode.setId(MONITORING_PREFIX + taskDataDto.getTaskName() + monitoringPlot.getKey().getUpperName());
                monitoringPlotNode.setDisplayName(monitoringPlot.getKey().getUpperName());
                MetricRankingProvider.sortPlotNodes(plotNodes);
                monitoringPlotNode.setPlots(plotNodes);
                monitoringPlotNodes.add(monitoringPlotNode);
            }
            log.debug("For sessions {} are available these plots: {}", sessionIds, monitoringPlotNodes);
        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds + ", task name " + taskDataDto.getTaskName(), e);
            throw new RuntimeException(e);
        }

        List<MonitoringPlotNode> resultList = new ArrayList<MonitoringPlotNode>(monitoringPlotNodes);
        MetricRankingProvider.sortPlotNodes(resultList);
        return resultList;
    }



    private List<Long> getMonitoringIds(Set<String> sessionIds, TaskDataDto taskDataDto) {

        List<Long> monitoringTaskIds = (List<Long>) entityManager.createNativeQuery(
                "select td.id from TaskData as td where td.sessionId in (:sessionIds) and td.taskId in " +
                    "(" +
                         "select pm.monitoringId from PerformedMonitoring as pm where pm.sessionId in (:sessionIds) and parentId in " +
                            "(" +
                                "select wd.parentId from WorkloadData as wd where wd.sessionId in (:sessionIds) and wd.taskId in " +
                                    "(" +
                                        "select td2.taskId from TaskData as td2 where td2.sessionId in (:sessionIds) and td2.id in (:ids)" +
                                    ")" +
                            ")" +
                    ")")
                .setParameter("ids", taskDataDto.getIds())
                .setParameter("sessionIds", sessionIds)
                .getResultList();

        return monitoringTaskIds;
    }

    @Override
    public List<MonitoringSessionScopePlotNode> getMonitoringPlotNodes(Set<String> sessionIds) {

        Set<MonitoringSessionScopePlotNode> monitoringPlotNodes = new LinkedHashSet<MonitoringSessionScopePlotNode>();
        try {
            for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : monitoringPlotGroups.entrySet()) {

                List<SessionPlotNode> plotNodes = new ArrayList<SessionPlotNode>();
                plotNodes.addAll(getMonitoringPlotNames(sessionIds, monitoringPlot));

                if (plotNodes.isEmpty()) {
                    continue;
                }

                MonitoringSessionScopePlotNode monitoringPlotNode = new MonitoringSessionScopePlotNode();
                monitoringPlotNode.setId(MONITORING_PREFIX + monitoringPlot.getKey().getUpperName());
                monitoringPlotNode.setDisplayName(monitoringPlot.getKey().getUpperName());
                MetricRankingProvider.sortPlotNodes(plotNodes);
                monitoringPlotNode.setPlots(plotNodes);
                monitoringPlotNodes.add(monitoringPlotNode);
            }
            log.debug("For sessions {} are available these plots: {}", sessionIds, monitoringPlotNodes);
        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds, e);
            throw new RuntimeException(e);
        }

        List<MonitoringSessionScopePlotNode> resultList = new ArrayList<MonitoringSessionScopePlotNode>(monitoringPlotNodes);
        MetricRankingProvider.sortPlotNodes(resultList);
        return resultList;
    }

    private List<SessionPlotNode> getMonitoringPlotNames(Set<String> sessionIds, Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringParameters) {

        List<SessionPlotNode> resultList = new ArrayList<SessionPlotNode>();

        List<String> parametersList = new ArrayList<String>();
        for (DefaultMonitoringParameters mp: monitoringParameters.getValue()) {
            parametersList.add(mp.getDescription());
        }

        List<Object[]> agentIdentifierObjects =
                entityManager.createNativeQuery("select ms.boxIdentifier, ms.systemUnderTestUrl from MonitoringStatistics as ms" +
                        "  where ms.sessionId in (:sessionId)" +
                        " and ms.description in (:parametersList)" +
                        " group by ms.boxIdentifier, ms.systemUnderTestUrl")
                        .setParameter("sessionId", sessionIds)
                        .setParameter("parametersList", parametersList)
                        .getResultList();

        Set<String> differentAgentIdentifiers = new LinkedHashSet<String>();
        for (Object[] object: agentIdentifierObjects) {
            String identy = object[0] == null ? object[1].toString() : object[0].toString();
            differentAgentIdentifiers.add(identy);
        }

        for (String agentIdenty: differentAgentIdentifiers) {
            SessionPlotNode plotNode = new SessionPlotNode();
            plotNode.setPlotNameDto(new SessionPlotNameDto(sessionIds, monitoringParameters.getKey().getUpperName() + AGENT_NAME_SEPARATOR + agentIdenty));
            plotNode.setDisplayName(agentIdenty);
            String id = METRICS_PREFIX + monitoringParameters.getKey().getUpperName() + agentIdenty;
            plotNode.setId(id);
            resultList.add(plotNode);
        }

        MetricRankingProvider.sortPlotNodes(resultList);
        return resultList;
    }

    private List<PlotNode> getMonitoringPlotNames(Set<String> sessionIds, Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringParameters, TaskDataDto taskDataDto, List<Long> monitoringIds) {
        List<PlotNode> resultList = new ArrayList<PlotNode>();

        List<String> parametersList = new ArrayList<String>();
        for (DefaultMonitoringParameters mp: monitoringParameters.getValue()) {
            parametersList.add(mp.getDescription());
        }

        List<Object[]> agentIdentifierObjects =
                entityManager.createNativeQuery("select ms.boxIdentifier, ms.systemUnderTestUrl from MonitoringStatistics as ms" +
                "  where ms.sessionId in (:sessionIds) " +
                        "and ms.taskData_id in (:taskIds) and ms.description in (:parametersList)" +
                " group by ms.boxIdentifier, ms.systemUnderTestUrl")
        .setParameter("sessionIds", sessionIds)
        .setParameter("parametersList", parametersList)
        .setParameter("taskIds", monitoringIds)
                .getResultList();

        Set<String> differentAgentIdentifiers = new LinkedHashSet<String>();
        for (Object[] object: agentIdentifierObjects) {
            String identy = object[0] == null ? object[1].toString() : object[0].toString();
            differentAgentIdentifiers.add(identy);
        }

        for (String agentIdenty: differentAgentIdentifiers) {
            PlotNode plotNode = new PlotNode();
            plotNode.setPlotName(new PlotNameDto(taskDataDto, monitoringParameters.getKey().getUpperName() + AGENT_NAME_SEPARATOR + agentIdenty));
            plotNode.setDisplayName(agentIdenty);
            String id = METRICS_PREFIX + taskDataDto.getTaskName() + monitoringParameters.getKey().getUpperName() + agentIdenty;
            plotNode.setId(id);
            resultList.add(plotNode);
        }

        MetricRankingProvider.sortPlotNodes(resultList);
        return resultList;
    }

    private boolean isWorkloadStatisticsAvailable(Set<String> sessionIds, TaskDataDto tests) {
        long timestamp = System.currentTimeMillis();
        long workloadStatisticsCount = (Long) entityManager.createQuery("select count(tis.id) from TimeInvocationStatistics as tis where tis.taskData.sessionId in (:sessionIds) and tis.taskData.id in (:tests)")
                .setParameter("tests", tests.getIds())
                .setParameter("sessionIds", sessionIds)
                .getSingleResult();

        if (workloadStatisticsCount < tests.getIds().size()) {
            log.info("For task ID {} workload statistics were not found in DB for {} ms", tests.getTaskName(), System.currentTimeMillis() - timestamp);
            return false;
        }

        return true;
    }

    private boolean isMonitoringStatisticsAvailable(String sessionId) {
        long timestamp = System.currentTimeMillis();
        long monitoringStatisticsCount = (Long) entityManager.createQuery("select count(ms.id) from MonitoringStatistics as ms where ms.sessionId=:sessionId")
                .setParameter("sessionId", sessionId)
                .getSingleResult();

        if (monitoringStatisticsCount == 0) {
            log.info("For session {} monitoring statistics were not found in DB for {} ms", sessionId, System.currentTimeMillis() - timestamp);
            return false;
        }

        return true;
    }

    @Override
    public List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds) {

        long timestamp = System.currentTimeMillis();
        List<Object[]> list = entityManager.createNativeQuery
                (
                        "select taskData.id, commonTests.name, commonTests.description, taskData.taskId , commonTests.clock, commonTests.clockValue, commonTests.termination" +
                                " from "+
                                "( "+
                                "select test.name, test.description, test.version, test.sessionId, test.taskId, test.clock, test.clockValue, test.termination from " +
                                "( "+
                                "select " +
                                "l.*, s.name, s.description, s.version " +
                                "from "+
                                "(select * from WorkloadTaskData where sessionId in (:sessions)) as l "+
                                "left outer join "+
                                "(select * from WorkloadDetails) as s "+
                                "on l.scenario_id=s.id "+
                                ") as test " +
                                "inner join " +
                                "( " +
                                "select t.* from "+
                                "( "+
                                "select " +
                                "l.*, s.name, s.description, s.version " +
                                "from "+
                                "(select * from WorkloadTaskData where sessionId in (:sessions)) as l "+
                                "left outer join "+
                                "(select * from WorkloadDetails) as s "+
                                "on l.scenario_id=s.id " +
                                ") as t "+
                                "group by "+
                                "t.termination, t.clock, t.clockValue, t.name, t.version "+
                                "having count(t.id)>=:sessionCount" +

                                ") as testArch " +
                                "on "+
                                "test.clock=testArch.clock and "+
                                "test.clockValue=testArch.clockValue and "+
                                "test.termination=testArch.termination and "+
                                "test.name=testArch.name and "+
                                "test.version=testArch.version "+
                                ") as commonTests "+
                                "left outer join "+
                                "(select * from TaskData where sessionId in (:sessions)) as taskData "+
                                "on "+
                                "commonTests.sessionId=taskData.sessionId and "+
                                "commonTests.taskId=taskData.taskId "
                ).setParameter("sessions", sessionIds)
                .setParameter("sessionCount", (long) sessionIds.size()).getResultList();

        //group tests by description
        HashMap<String, TaskDataDto> map = new HashMap<String, TaskDataDto>(list.size());
        HashMap<String, Integer> mapIds = new HashMap<String, Integer>(list.size());
        for (Object[] testData : list){
            BigInteger id = (BigInteger)testData[0];
            String name = (String) testData[1];
            String description = (String) testData[2];
            String taskId = (String)testData[3];
            String clock = testData[4] + " (" + testData[5] + ")";
            String termination = (String) testData[6];


            int taskIdInt = Integer.parseInt(taskId.substring(5));
            String key = description+name;
            if (map.containsKey(key)){
                map.get(key).getIds().add(id.longValue());

                Integer oldValue = mapIds.get(key);
                mapIds.put(key, (oldValue==null ? 0 : oldValue)+taskIdInt);
            }else{
                TaskDataDto taskDataDto = new TaskDataDto(id.longValue(), name, description);
                taskDataDto.setClock(clock);
                taskDataDto.setTerminationStrategy(termination);
                //merge
                if (map.containsKey(name)){
                    taskDataDto.getIds().addAll(map.get(name).getIds());

                    taskIdInt = taskIdInt + mapIds.get(name);
                }
                map.put(key, taskDataDto);
                mapIds.put(key, taskIdInt);
            }
        }

        if (map.isEmpty()){
            return Collections.EMPTY_LIST;
        }

        PriorityQueue<Object[]> priorityQueue= new PriorityQueue<Object[]>(mapIds.size(), new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                return ((Comparable)o1[0]).compareTo(o2[0]);
            }
        });

        for (String key : map.keySet()){
            TaskDataDto taskDataDto = map.get(key);
            if (taskDataDto.getIds().size() == sessionIds.size()){
                priorityQueue.add(new Object[]{mapIds.get(key), taskDataDto});
            }
        }

        ArrayList<TaskDataDto> result = new ArrayList<TaskDataDto>(priorityQueue.size());
        while (!priorityQueue.isEmpty()){
            result.add((TaskDataDto)priorityQueue.poll()[1]);
        }

        log.info("For sessions {} was loaded {} tasks for {} ms", new Object[]{sessionIds, result.size(), System.currentTimeMillis() - timestamp});
        System.out.println(result);
        return result;
    }
}
