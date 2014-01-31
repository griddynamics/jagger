package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.webclient.client.components.control.model.*;
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
import java.util.concurrent.*;

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


    public Set<MetricNameDto> getCustomMetricsNames(List<TaskDataDto> tests){
        Set<MetricNameDto> metrics;

        Set<Long> taskIds = new HashSet<Long>();
        for (TaskDataDto tdd : tests) {
            taskIds.addAll(tdd.getIds());
        }

        long temp = System.currentTimeMillis();
        List<Object[]> metricNames = entityManager.createNativeQuery(
                    "select dre.name, selected.taskdataID from DiagnosticResultEntity dre join (" +
                            "  select wd.id as workloaddataID, td.taskdataID from WorkloadData wd join   " +
                            "      ( " +
                            "        SELECT td.id as taskdataID, td.taskId, td.sessionId from TaskData td where td.id in (:ids)" +
                            "      ) as td on wd.sessionId=td.sessionId and wd.taskId=td.taskId" +
                            ") as selected on dre.workloadData_id=selected.workloaddataID")
                    .setParameter("ids", taskIds)
                    .getResultList();

        log.debug("{} ms spent for fetching {} metrics", System.currentTimeMillis() - temp, metricNames.size());
        temp = System.currentTimeMillis();

        List<Object[]> validatorNames = entityManager.createNativeQuery(
                "select v.validator, selected.taskdataID from ValidationResultEntity v join " +
                "  (" +
                "    select wd.id as workloaddataID, td.taskdataID from WorkloadData wd join   " +
                "        ( " +
                "          SELECT td.id as taskdataID, td.taskId, td.sessionId from TaskData td where td.id in (:ids)" +
                "        ) as td on wd.taskId=td.taskId and wd.sessionId=td.sessionId" +
                "  ) as selected on v.workloadData_id=selected.workloaddataID")
                .setParameter("ids", taskIds).getResultList();
        log.debug("{} ms spent for fetching {} metrics", System.currentTimeMillis() - temp, validatorNames.size());

        metrics = new HashSet<MetricNameDto>(metricNames.size()+validatorNames.size());

        for (Object[] name : metricNames){
            if (name == null || name[0] == null) continue;
            for (TaskDataDto td : tests) {
                if (td.getIds().contains(((BigInteger)name[1]).longValue())) {
                    MetricNameDto metric = new MetricNameDto();
                    metric.setTests(td);
                    metric.setName((String)name[0]);
                    metrics.add(metric);
                    break;
                }
            }
        }

        for (Object[] name : validatorNames){
            if (name == null || name[0] == null) continue;
            for (TaskDataDto td : tests) {
                if (td.getIds().contains(((BigInteger)name[1]).longValue())) {
                    MetricNameDto metric = new MetricNameDto();
                    metric.setTests(td);
                    metric.setName((String)name[0]);
                    metrics.add(metric);
                    break;
                }
            }
        }

        return metrics;
    }

    public Set<MetricNameDto> getLatencyMetricsNames(List<TaskDataDto> tests){
        Set<MetricNameDto> latencyNames;

        Set<Long> testIds = new HashSet<Long>();
        for (TaskDataDto tdd : tests) {
            testIds.addAll(tdd.getIds());
        }

        long temp = System.currentTimeMillis();
        List<WorkloadProcessLatencyPercentile> latency = entityManager.createQuery(
                "select s from  WorkloadProcessLatencyPercentile as s where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) ")
                .setParameter("taskIds", testIds)
                .getResultList();


        log.debug("{} ms spent for Latency Percentile fetching (size ={})", System.currentTimeMillis() - temp, latency.size());

        latencyNames = new HashSet<MetricNameDto>(latency.size());

        if (!latency.isEmpty()){


            for(WorkloadProcessLatencyPercentile percentile : latency) {
                for (TaskDataDto tdd : tests) {

                    if (tdd.getIds().contains(percentile.getWorkloadProcessDescriptiveStatistics().getTaskData().getId())) {
                        MetricNameDto dto = new MetricNameDto();
                        dto.setName("Latency "+Double.toString(percentile.getPercentileKey())+" %");
                        dto.setTests(tdd);
                        latencyNames.add(dto);
                        break;
                    }
                }
            }
        }
        return latencyNames;
    }


    /**
     * one db call method
     * @param sessionIds
     * @param taskDataDtos
     * @return
     */
    @Override
    public Map<TaskDataDto, List<MonitoringPlotNode>> getMonitoringPlotNodes(Set<String> sessionIds, List<TaskDataDto> taskDataDtos) {
        try {
            Map<TaskDataDto, List<BigInteger>>  monitoringIds = getMonitoringIds(sessionIds, taskDataDtos);
            if (monitoringIds.isEmpty()) {
                return Collections.EMPTY_MAP;
            }



            Map<TaskDataDto, List<MonitoringPlotNode>> result = getMonitoringPlotNames(sessionIds, monitoringPlotGroups.entrySet(), monitoringIds);

            if (result.isEmpty()) {
                return Collections.EMPTY_MAP;
            }

            log.debug("For sessions {} are available these plots: {}", sessionIds, result);
            return result;

        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds + ", tasks  " + taskDataDtos, e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Fetch all Monitoring tasks ids for all tests
     * @param sessionIds all sessions
     * @param taskDataDtos all tests
     * @return list of monitoring task ids
     */
    private Map<TaskDataDto, List<BigInteger>> getMonitoringIds(Set<String> sessionIds, List<TaskDataDto> taskDataDtos) {

        List<Long> taskIds = new ArrayList<Long>();
        for (TaskDataDto tdd : taskDataDtos) {
            taskIds.addAll(tdd.getIds());
        }


        long temp = System.currentTimeMillis();
        List<Object[]> monitoringTaskIds = entityManager.createNativeQuery(
                "select test.id, some.testId from TaskData as test inner join" +
                        "  (" +
                        "   select some.id as testId, some.parentId, pm.monitoringId from PerformedMonitoring as pm join " +
                        "            (" +
                        "                select td2.id, wd.parentId from WorkloadData as wd join TaskData as td2" +
                        "                      on td2.id in (:ids) and wd.sessionId in (:sessionIds) and wd.taskId=td2.taskId" +
                        "            ) as some on pm.sessionId in (:sessionIds) and pm.parentId=some.parentId" +
                        "  ) as some  on test.sessionId in (:sessionIds) and test.taskId=some.monitoringId"
        )
                .setParameter("ids", taskIds)
                .setParameter("sessionIds", sessionIds)
                .getResultList();
        log.debug("db call to fetch all monitoring tasks ids in {} ms (size : {})", System.currentTimeMillis() - temp, monitoringTaskIds.size());

        Map<TaskDataDto, List<BigInteger>> result = new HashMap<TaskDataDto, List<BigInteger>>();
        if (monitoringTaskIds.isEmpty()) {
            return Collections.EMPTY_MAP;
        }


        for (Object[] ids : monitoringTaskIds) {
            for (TaskDataDto tdd : taskDataDtos) {
                if (tdd.getIds().contains(((BigInteger)ids[1]).longValue())) {
                    if (!result.containsKey(tdd)) {
                        result.put(tdd, new ArrayList<BigInteger>());
                    }
                    result.get(tdd).add(((BigInteger)ids[0]));
                    break;
                }
            }
        }

        return result;
    }


    @Override
    public List<MonitoringSessionScopePlotNode> getMonitoringPlotNodesNew(Set<String> sessionIds) {

        List<MonitoringSessionScopePlotNode> monitoringPlotNodes;
        try {

            monitoringPlotNodes = getMonitoringPlotNamesNew(sessionIds);
            log.debug("For sessions {} are available these plots: {}", sessionIds, monitoringPlotNodes);
        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds, e);
            throw new RuntimeException(e);
        }

        if (monitoringPlotNodes == null) {
            return Collections.EMPTY_LIST;
        }
        return monitoringPlotNodes;
    }


    @Override
    public Map<TaskDataDto, List<MetricNode>> getTestMetricsMap(final List<TaskDataDto> tddos, ExecutorService treadPool) {

        Long time = System.currentTimeMillis();
        List<MetricNameDto> list = new ArrayList<MetricNameDto>();
        for (TaskDataDto taskDataDto : tddos){
            for (String standardMetricName : standardMetrics.keySet()){
                MetricNameDto metric = new MetricNameDto();
                metric.setName(standardMetricName);
                metric.setTests(taskDataDto);
                list.add(metric);
            }
        }

        try {

            Future<Set<MetricNameDto>> latencyMetricNamesFuture = treadPool.submit(
                    new Callable<Set<MetricNameDto>>(){

                        @Override
                        public Set<MetricNameDto> call() throws Exception {
                            return getLatencyMetricsNames(tddos);
                        }
                    }
            );

            Future<Set<MetricNameDto>> customMetricNamesFuture = treadPool.submit(
                    new Callable<Set<MetricNameDto>>(){

                        @Override
                        public Set<MetricNameDto> call() throws Exception {
                            return getCustomMetricsNames(tddos);
                        }
                    }
            );

            list.addAll(latencyMetricNamesFuture.get());
            list.addAll(customMetricNamesFuture.get());
        } catch (Exception e) {
            log.error("Exception occurs while fetching MetricNames for tests : ", e);
            throw new RuntimeException(e);
        }

        log.info("For tasks {} was found {} metrics names for {} ms", new Object[]{tddos, list.size(), System.currentTimeMillis() - time});

        Map<TaskDataDto, List<MetricNode>> result = new HashMap<TaskDataDto, List<MetricNode>>();

        for (MetricNameDto mnd : list) {
            for (TaskDataDto tdd : tddos) {
                if (tdd.getIds().containsAll(mnd.getTaskIds())) {
                    if (!result.containsKey(tdd)) {
                        result.put(tdd, new ArrayList<MetricNode>());
                    }
                    MetricNode mn = new MetricNode();
                    mn.setMetricName(mnd);
                    mn.setId(SUMMARY_PREFIX + tdd.getTaskName() + mnd.getName());
                    mn.setDisplayName(mnd.getDisplay());
                    result.get(tdd).add(mn);
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public Map<TaskDataDto, List<PlotNode>> getTestPlotsMap(Set<String> sessionIds, List<TaskDataDto> taskList) {

        Map<TaskDataDto, List<PlotNode>> result = new HashMap<TaskDataDto, List<PlotNode>>();

        List<PlotNameDto> plotNameDtoSet = new ArrayList<PlotNameDto>();
        try {

            Map<TaskDataDto, Boolean> isWorkloadMap = isWorkloadStatisticsAvailable(taskList);
            for (Map.Entry<TaskDataDto, Boolean> entry: isWorkloadMap.entrySet()) {
                if (entry.getValue()) {
                    for (Map.Entry<GroupKey, DefaultWorkloadParameters[]> monitoringPlot : workloadPlotGroups.entrySet()) {
                        plotNameDtoSet.add(new PlotNameDto(entry.getKey(), monitoringPlot.getKey().getUpperName()));
                    }
                }
            }

            List<PlotNameDto> customMetrics = customMetricPlotDataProvider.getPlotNames(taskList);

            plotNameDtoSet.addAll(customMetrics);

            log.debug("For sessions {} are available these plots: {}", sessionIds, plotNameDtoSet);

            for (PlotNameDto pnd : plotNameDtoSet) {
                for (TaskDataDto tdd : taskList) {
                    if (tdd.getIds().containsAll(pnd.getTaskIds())) {
                        if (!result.containsKey(tdd)) {
                            result.put(tdd, new ArrayList<PlotNode>());
                        }
                        PlotNode pn = new PlotNode();
                        pn.setPlotName(pnd);
                        pn.setId(METRICS_PREFIX + tdd.getTaskName() + pnd.getPlotName());
                        pn.setDisplayName(pnd.getDisplay());
                        result.get(tdd).add(pn);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds + ", tasks : " + taskList, e);
            throw new RuntimeException(e);
        }

        return result;
    }


    private List<MonitoringSessionScopePlotNode> getMonitoringPlotNamesNew(Set<String> sessionIds) {

        long temp = System.currentTimeMillis();
        List<Object[]> agentIdentifierObjects =
                entityManager.createNativeQuery("select ms.boxIdentifier, ms.systemUnderTestUrl, ms.description from MonitoringStatistics as ms" +
                        "  where ms.sessionId in (:sessionId)" +
                        " group by ms.description, ms.boxIdentifier, ms.systemUnderTestUrl")
                        .setParameter("sessionId", sessionIds)
                        .getResultList();
        log.debug("db call to fetch session scope monitoring in {} ms (size: {})", System.currentTimeMillis() - temp, agentIdentifierObjects.size());

        if (agentIdentifierObjects.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        Map<String, MonitoringSessionScopePlotNode> tempMap = new HashMap<String, MonitoringSessionScopePlotNode>();

        Set<Map.Entry<GroupKey, DefaultMonitoringParameters[]>> set = monitoringPlotGroups.entrySet();
        for (Object[] objects : agentIdentifierObjects) {

            String groupKey = findMonitoringKey((String)objects[2], set);
            if (groupKey == null) {

                continue;
            }

            if (!tempMap.containsKey(groupKey)) {

                MonitoringSessionScopePlotNode monitoringPlotNode = new MonitoringSessionScopePlotNode();
                monitoringPlotNode.setId(MONITORING_PREFIX + groupKey);
                monitoringPlotNode.setDisplayName(groupKey);
                monitoringPlotNode.setPlots(new ArrayList<SessionPlotNode>());
                tempMap.put(groupKey, monitoringPlotNode);
            }

            MonitoringSessionScopePlotNode monitoringPlotNode = tempMap.get(groupKey);

            SessionPlotNode plotNode = new SessionPlotNode();
            String agentIdenty = objects[0] == null ? objects[1].toString() : objects[0].toString();
            plotNode.setPlotNameDto(new SessionPlotNameDto(sessionIds, groupKey + AGENT_NAME_SEPARATOR + agentIdenty));
            plotNode.setDisplayName(agentIdenty);
            String id = METRICS_PREFIX + groupKey + agentIdenty;
            plotNode.setId(id);

            if (!monitoringPlotNode.getPlots().contains(plotNode))
                monitoringPlotNode.getPlots().add(plotNode);

        }

        ArrayList<MonitoringSessionScopePlotNode> result = new ArrayList<MonitoringSessionScopePlotNode>(tempMap.values());
        for (MonitoringSessionScopePlotNode ms : result) {
            MetricRankingProvider.sortPlotNodes(ms.getPlots());
        }
        MetricRankingProvider.sortPlotNodes(result);

        return result;
    }


    private Map<TaskDataDto, List<MonitoringPlotNode>> getMonitoringPlotNames(Set<String> sessionIds, Set<Map.Entry<GroupKey, DefaultMonitoringParameters[]>> monitoringParameters, Map<TaskDataDto, List<BigInteger>> monitoringIdsMap) {

        List<BigInteger> monitoringIds = new ArrayList<BigInteger>();
        for (List<BigInteger> mIds : monitoringIdsMap.values()) {
            monitoringIds.addAll(mIds);
        }

        long temp = System.currentTimeMillis();
        List<Object[]> agentIdentifierObjects =
                entityManager.createNativeQuery("select ms.boxIdentifier, ms.systemUnderTestUrl, ms.taskData_id, ms.description  from MonitoringStatistics as ms" +
                        "  where " +
                        " ms.taskData_id in (:taskIds) " +
                        " group by ms.taskData_id, ms.description, boxIdentifier, systemUnderTestUrl")
                        .setParameter("taskIds", monitoringIds)
                        .getResultList();
        log.debug("db call to fetch all MonitoringPlotNames for tests in {} ms (size: {})", System.currentTimeMillis() - temp, agentIdentifierObjects.size());

        Map<TaskDataDto, List<MonitoringPlotNode>> resultMap = new HashMap<TaskDataDto, List<MonitoringPlotNode>>();

        Set<TaskDataDto> taskSet = monitoringIdsMap.keySet();

        for (Object[] objects : agentIdentifierObjects) {
            BigInteger testId = (BigInteger)objects[2];
            for (TaskDataDto tdd : taskSet) {
                if (monitoringIdsMap.get(tdd).contains(testId)) {
                    if (!resultMap.containsKey(tdd)) {
                        resultMap.put(tdd, new ArrayList<MonitoringPlotNode>());
                    }

                    List<MonitoringPlotNode> mpnList = resultMap.get(tdd);
                    String monitoringKey = findMonitoringKey((String)objects[3], monitoringParameters);
                    if (monitoringKey == null) {
                        log.warn("Could not find monitoing key for description: '{}' and monitoing task id: '{}'", objects[3], objects[2]);
                        break;
                    }
                    String identy = objects[0] == null ? objects[1].toString() : objects[0].toString();

                    PlotNode plotNode = new PlotNode();
                    plotNode.setPlotName(new PlotNameDto(tdd, monitoringKey + AGENT_NAME_SEPARATOR + identy));
                    plotNode.setDisplayName(identy);
                    String id = METRICS_PREFIX + tdd.getTaskName() + monitoringKey + identy;
                    plotNode.setId(id);

                    boolean present = false;
                    for (MonitoringPlotNode mpn : mpnList) {
                        if (mpn.getDisplayName().equals(monitoringKey)) {
                            if (!mpn.getPlots().contains(plotNode))
                                mpn.getPlots().add(plotNode);
                            present = true;
                            break;
                        }
                    }

                    if (!present) {
                        MonitoringPlotNode monitoringPlotNode = new MonitoringPlotNode();
                        monitoringPlotNode.setId(MONITORING_PREFIX + tdd.getTaskName() + monitoringKey);
                        monitoringPlotNode.setDisplayName(monitoringKey);
                        resultMap.get(tdd).add(monitoringPlotNode);
                        monitoringPlotNode.setPlots(new ArrayList<PlotNode>());
                        if (!monitoringPlotNode.getPlots().contains(plotNode))
                            monitoringPlotNode.getPlots().add(plotNode);
                    }
                    break;
                }
            }
        }


        // sorting
        for (TaskDataDto tdd : taskSet) {
            List<MonitoringPlotNode> mpnList = resultMap.get(tdd);
            if (mpnList == null) continue;
            MetricRankingProvider.sortPlotNodes(mpnList);
            for (MonitoringPlotNode mpn : mpnList) {
                MetricRankingProvider.sortPlotNodes(mpn.getPlots());
            }
        }
        return resultMap;
    }

    private String findMonitoringKey(String description, Set<Map.Entry<GroupKey, DefaultMonitoringParameters[]>> monitoringParameters) {
        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> entry : monitoringParameters) {
            for (DefaultMonitoringParameters dmp : entry.getValue()) {
                if (dmp.getDescription().equals(description)) {
                    return entry.getKey().getUpperName();
                }
            }
        }
        return null;
    }


    private Map<TaskDataDto, Boolean> isWorkloadStatisticsAvailable(List<TaskDataDto> tests) {

        List<Long> testsIds = new ArrayList<Long>();
        for (TaskDataDto tdd : tests) {
            testsIds.addAll(tdd.getIds());
        }

        long temp = System.currentTimeMillis();
        List<Object[]> objects  = entityManager.createQuery("select tis.taskData.id, count(tis.id) from TimeInvocationStatistics as tis where tis.taskData.id in (:tests)")
                .setParameter("tests", testsIds)
                .getResultList();
        log.debug("db call to check if WorkloadStatisticsAvailable in {} ms (size: {})", System.currentTimeMillis() - temp, objects.size());


        if (objects.isEmpty()) {
            return Collections.EMPTY_MAP;
        }

        Map<TaskDataDto, Integer> tempMap = new HashMap<TaskDataDto, Integer>(tests.size());
        for (TaskDataDto tdd : tests) {
            tempMap.put(tdd, 0);
        }

        for (Object[] object : objects) {
            for (TaskDataDto tdd : tests) {
                if (tdd.getIds().contains((Long) object[1])) {
                    int value = tempMap.get(tdd);
                    tempMap.put(tdd, ++value);
                }
            }
        }

        Map<TaskDataDto, Boolean> resultMap = new HashMap<TaskDataDto, Boolean>(tests.size());
        for (Map.Entry<TaskDataDto, Integer> entry : tempMap.entrySet()) {
            resultMap.put(entry.getKey(), entry.getValue() < entry.getKey().getIds().size());
        }

        return resultMap;
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
        return result;
    }
}
