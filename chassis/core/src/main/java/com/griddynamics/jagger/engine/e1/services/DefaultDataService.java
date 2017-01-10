package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.model.TestDetailsNode;
import com.griddynamics.jagger.dbapi.model.TestNode;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.engine.e1.services.data.service.*;

import java.util.*;

/**
 * Implementation of the @ref DataService
 *
 * @par Details:
 * @details  Provides access to the tests results in the Jagger DB. You can get a full information about sessions, tests, metrics. @n
 * Where this service is available you can find in chapter: @ref section_listeners_services @n
 * @n
 * @ingroup Main_Services_group
 */

public class DefaultDataService implements DataService {

    private DatabaseService databaseService;

    // For all user operations  - get all test results without matching
    private final SessionMatchingSetup SESSION_MATCHING_SETUP = new SessionMatchingSetup(false, Collections.<SessionMatchingSetup.MatchBy>emptySet());

    public DefaultDataService(NodeContext context) {
        databaseService = context.getService(DatabaseService.class);
    }

    public DefaultDataService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public SessionEntity getSession(String sessionId) {
        Set<SessionEntity> sessions = getSessions(Arrays.asList(sessionId));
        if (sessions.isEmpty()) {
            return null;
        }
        return sessions.iterator().next();
    }

    @Override
    public DecisionPerSessionDto getSessionDecisions(String sessionId) {
        return databaseService.getDecisionPerSession(sessionId);
    }

    @Override
    public Set<SessionEntity> getSessions(Collection<String> sessionIds) {

        List<SessionDataDto> sessionDataDtoList = databaseService.getSessionInfoService().getBySessionIds(0, sessionIds.size(), new HashSet<String>(sessionIds));

        if (sessionDataDtoList.isEmpty()) {
            return Collections.emptySet();
        }

        Set<SessionEntity> entities = new TreeSet<>(new SessionEntity.IdComparator());
        for (SessionDataDto sessionDataDto : sessionDataDtoList) {
            SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.setId(sessionDataDto.getSessionId());
            sessionEntity.setStartDate(sessionDataDto.getStartDate());
            sessionEntity.setEndDate(sessionDataDto.getEndDate());
            sessionEntity.setKernels(sessionDataDto.getActiveKernelsCount());
            sessionEntity.setComment(sessionDataDto.getComment());

            entities.add(sessionEntity);
        }

        return entities;
    }

    @Override
    public Set<TestEntity> getTests(SessionEntity session) {
        return getTests(session.getId());
    }

    @Override
    public Set<TestEntity> getTests(String sessionId) {
        Map<String, Set<TestEntity>> map = getTests(Arrays.asList(sessionId));

        Set<TestEntity> result = map.get(sessionId);
        if (result != null) {
            return result;
        }

        return Collections.emptySet();
    }

    @Override
    public Map<String, Set<TestEntity>> getTests(Collection<String> sessionIds) {
        return getTestsWithName(sessionIds, null, SESSION_MATCHING_SETUP);
    }

    @Override
    public TestEntity getTestByName(SessionEntity session, String testName) {
        return getTestByName(session.getId(), testName);
    }

    @Override
    public TestEntity getTestByName(String sessionId, String testName) {
        Map<String, TestEntity> map = getTestsByName(Collections.singletonList(sessionId), testName);

        TestEntity result = map.get(sessionId);
        if (result != null) {
            return result;
        }

        return null;
    }

    @Override
    public Map<String, TestEntity> getTestsByName(Collection<String> sessionIds, String testName) {
        Map<String, Set<TestEntity>> tests = getTestsWithName(sessionIds, testName, SESSION_MATCHING_SETUP);

        Map<String, TestEntity> result = new HashMap<String, TestEntity>(tests.size());

        for (Map.Entry<String, Set<TestEntity>> entry : tests.entrySet()) {
            Set<TestEntity> testEntities = entry.getValue();
            if (!testEntities.isEmpty()) {
                result.put(entry.getKey(), testEntities.iterator().next());
            }
        }

        return result;
    }

    // if testName=null => no filtering for test name => all tests for session(s) will be returned
    // @return map <sessionId, set<test>>
    public Map<String, Set<TestEntity>> getTestsWithName(Collection<String> sessionIds, String testName, SessionMatchingSetup sessionMatchingSetup) {
        if (sessionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // basic
        List<TaskDataDto> taskDataDtoList = databaseService.getTaskDataForSessions(new HashSet<String>(sessionIds), sessionMatchingSetup);
        // info
        Map<TaskDataDto, Map<String, TestInfoDto>> testInfoMap = databaseService.getTestInfoByTaskDataDto(taskDataDtoList);
        // decision
        Set<Long> ids = new HashSet<Long>();
        for (TaskDataDto taskDataDto : taskDataDtoList) {
            ids.addAll(taskDataDto.getIds());
        }
        Map<Long, TaskDecisionDto> idToDecisionPerTest = new HashMap<Long, TaskDecisionDto>();
        for (TaskDecisionDto taskDecisionDto : databaseService.getDecisionsPerTask(ids)) {
            idToDecisionPerTest.put(taskDecisionDto.getId(), taskDecisionDto);
        }

        Map<String, Set<TestEntity>> result = new HashMap<String, Set<TestEntity>>();

        for (TaskDataDto taskDataDto : taskDataDtoList) {
            for (Map.Entry<Long, String> entry : taskDataDto.getIdToSessionId().entrySet()) {
                if (((testName != null) && (testName.equals(taskDataDto.getTaskName()))) ||
                        (testName == null)) {
                    Long testId = entry.getKey();
                    String sessionId = entry.getValue();

                    TestEntity testEntity = new TestEntity();
                    testEntity.setId(testId);
                    testEntity.setDescription(taskDataDto.getDescription());
                    testEntity.setName(taskDataDto.getTaskName());
                    testEntity.setTaskDataDto(taskDataDto);

                    if (testInfoMap.containsKey(taskDataDto)) {
                        TestInfoDto testInfoDto = testInfoMap.get(taskDataDto).entrySet().iterator().next().getValue();
                        testEntity.setLoad(testInfoDto.getClock());
                        testEntity.setClockValue(testInfoDto.getClockValue());
                        testEntity.setTerminationStrategy(testInfoDto.getTermination());
                        testEntity.setStartDate(testInfoDto.getStartTime());
                        testEntity.setTestGroupIndex(testInfoDto.getNumber());
                        testEntity.setTestExecutionStatus(testInfoDto.getStatus());
                    }

                    if (idToDecisionPerTest.containsKey(testId)) {
                        testEntity.setDecision(idToDecisionPerTest.get(testId).getDecision());
                    }

                    if (result.containsKey(sessionId)) {
                        result.get(sessionId).add(testEntity);
                    } else {
                        Set<TestEntity> testEntitySet = new HashSet<TestEntity>();
                        testEntitySet.add(testEntity);
                        result.put(sessionId, testEntitySet);
                    }
                }
            }
        }

        // add empty results when not found
        for (String sessionId : sessionIds) {
            if (!result.containsKey(sessionId)) {
                result.put(sessionId, Collections.<TestEntity>emptySet());
            }
        }

        return result;
    }

    @Override
    public Set<MetricEntity> getMetrics(Long testId) {
        Map<Long, Set<MetricEntity>> map = getMetricsByTestIds(Arrays.asList(testId));

        Set<MetricEntity> result = map.get(testId);
        if (result != null) {
            return result;
        }

        return Collections.emptySet();
    }

    @Override
    public Set<MetricEntity> getMetrics(TestEntity test) {
        Map<TestEntity, Set<MetricEntity>> map = getMetricsByTests(Arrays.asList(test));

        Set<MetricEntity> result = map.get(test);
        if (result != null) {
            return result;
        }

        return Collections.emptySet();
    }

    @Override
    public Map<TestEntity, Set<MetricEntity>> getMetricsByTests(Collection<TestEntity> tests) {
        Map<Long, TestEntity> map = new HashMap<Long, TestEntity>(tests.size());
        Set<Long> ids = new HashSet<Long>(tests.size());

        for (TestEntity test : tests) {
            map.put(test.getId(), test);
            ids.add(test.getId());
        }

        Map<Long, Set<MetricEntity>> metrics = getMetricsByTestIds(ids);

        Map<TestEntity, Set<MetricEntity>> result = new HashMap<TestEntity, Set<MetricEntity>>();

        for (Long key : map.keySet()) {
            result.put(map.get(key), metrics.get(key));
        }

        return result;
    }

    @Override
    public Map<Long, Set<MetricEntity>> getMetricsByTestIds(Collection<Long> testIds) {
        if (testIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // Get
        List<String> sessionIds = databaseService.getSessionIdsByTaskIds(new HashSet<Long>(testIds));

        // Get all test results without matching
        SessionMatchingSetup sessionMatchingSetup = new SessionMatchingSetup(false, Collections.<SessionMatchingSetup.MatchBy>emptySet());
        RootNode rootNode = databaseService.getControlTreeForSessions(new HashSet<String>(sessionIds), sessionMatchingSetup);

        // Filter
        List<TestNode> summaryNodeTests = new ArrayList<TestNode>();
        List<TestDetailsNode> detailsNodeTests = new ArrayList<TestDetailsNode>();

        for (TestNode testNode : rootNode.getSummaryNode().getTests()) {
            Long testId = testNode.getTaskDataDto().getId();
            if (testIds.contains(testId)) {
                summaryNodeTests.add(testNode);
            }
        }
        for (TestDetailsNode testDetailsNode : rootNode.getDetailsNode().getTests()) {
            Long testId = testDetailsNode.getTaskDataDto().getId();
            if (testIds.contains(testId)) {
                detailsNodeTests.add(testDetailsNode);
            }
        }

        // Join
        Map<Long, Set<MetricNameDto>> metrics = new HashMap<Long, Set<MetricNameDto>>();
        Set<String> metricsWithSummary = new HashSet<String>();
        Set<String> metricsWithPlots = new HashSet<String>();

        for (TestNode testNode : summaryNodeTests) {
            Long testId = testNode.getTaskDataDto().getId();

            if (!metrics.containsKey(testId)) {
                metrics.put(testId, new HashSet<MetricNameDto>());
            }

            for (MetricNode metricNode : testNode.getMetrics()) {
                for (MetricNameDto metricNameDto : metricNode.getMetricNameDtoList()) {
                    metrics.get(testId).add(metricNameDto);
                    metricsWithSummary.add(metricNameDto.getMetricName());
                }
            }
        }

        for (TestDetailsNode testDetailsNode : detailsNodeTests) {
            Long testId = testDetailsNode.getTaskDataDto().getId();

            if (!metrics.containsKey(testId)) {
                metrics.put(testId, new HashSet<MetricNameDto>());
            }

            for (MetricNode metricNode : testDetailsNode.getMetrics()) {
                for (MetricNameDto metricNameDto : metricNode.getMetricNameDtoList()) {
                    metrics.get(testId).add(metricNameDto);
                    metricsWithPlots.add(metricNameDto.getMetricName());
                }
            }
        }

        // Convert
        Map<Long, Set<MetricEntity>> result = new HashMap<Long, Set<MetricEntity>>();
        for (Long key : metrics.keySet()) {
            result.put(key, new HashSet<MetricEntity>());

            for (MetricNameDto metricNameDto : metrics.get(key)) {
                MetricEntity metricEntity = new MetricEntity();
                metricEntity.setMetricNameDto(metricNameDto);
                if (metricsWithSummary.contains(metricNameDto.getMetricName())) {
                    metricEntity.setSummaryAvailable(true);
                }
                if (metricsWithPlots.contains(metricNameDto.getMetricName())) {
                    metricEntity.setPlotAvailable(true);
                }
                result.get(key).add(metricEntity);
            }
        }

        return result;
    }

    @Override
    public MetricSummaryValueEntity getMetricSummary(MetricEntity metric) {
        Map<MetricEntity, MetricSummaryValueEntity> map = getMetricSummary(Arrays.asList(metric));

        return map.get(metric);
    }

    @Override
    public Map<MetricEntity, MetricSummaryValueEntity> getMetricSummary(Collection<MetricEntity> metrics) {

        Set<MetricNameDto> metricNameDtoSet = new HashSet<MetricNameDto>();
        Map<MetricNameDto, MetricEntity> matchMap = new HashMap<MetricNameDto, MetricEntity>();

        for (MetricEntity metric : metrics) {
            if (metric.isSummaryAvailable()) {
                metricNameDtoSet.add(metric.getMetricNameDto());
                matchMap.put(metric.getMetricNameDto(), metric);
            }
        }

        Collection<SummarySingleDto> metricDtoList = databaseService.getSummaryByMetricNameDto(metricNameDtoSet, true).values();

        Map<MetricEntity, MetricSummaryValueEntity> result = new HashMap<MetricEntity, MetricSummaryValueEntity>();
        for (SummarySingleDto metricDto : metricDtoList) {
            MetricEntity metricEntity = matchMap.get(metricDto.getMetricName());
            MetricSummaryValueEntity value = new MetricSummaryValueEntity();
            value.setValue(Double.parseDouble(metricDto.getValues().iterator().next().getValue()));
            value.setDecision(metricDto.getValues().iterator().next().getDecision());
            result.put(metricEntity, value);
        }

        return result;
    }

    @Override
    public List<MetricPlotPointEntity> getMetricPlotData(MetricEntity metric) {
        Map<MetricEntity, List<MetricPlotPointEntity>> map = getMetricPlotData(Collections.singletonList(metric));

        return map.get(metric);
    }

    @Override
    public Map<MetricEntity, List<MetricPlotPointEntity>> getMetricPlotData(Collection<MetricEntity> metrics) {
        Set<MetricNameDto> metricNameDtoSet = new HashSet<MetricNameDto>();
        Map<MetricNameDto, MetricEntity> matchMap = new HashMap<MetricNameDto, MetricEntity>();

        for (MetricEntity metric : metrics) {
            if (metric.isPlotAvailable()) {
                metricNameDtoSet.add(metric.getMetricNameDto());
                matchMap.put(metric.getMetricNameDto(), metric);
            }
        }

        Map<MetricNameDto, List<PlotSingleDto>> resultMap = databaseService.getPlotDataByMetricNameDto(metricNameDtoSet);

        Map<MetricEntity, List<MetricPlotPointEntity>> result = new HashMap<MetricEntity, List<MetricPlotPointEntity>>();
        for (Map.Entry<MetricNameDto, List<PlotSingleDto>> entry : resultMap.entrySet()) {
            MetricEntity metricEntity = matchMap.get(entry.getKey());
            List<MetricPlotPointEntity> values = new ArrayList<MetricPlotPointEntity>();
            for (PointDto pointDto : entry.getValue().iterator().next().getPlotData()) {
                MetricPlotPointEntity metricPlotPointEntity = new MetricPlotPointEntity();
                metricPlotPointEntity.setTime(pointDto.getX());
                metricPlotPointEntity.setValue(pointDto.getY());
                values.add(metricPlotPointEntity);
            }
            result.put(metricEntity, values);
        }

        return result;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
