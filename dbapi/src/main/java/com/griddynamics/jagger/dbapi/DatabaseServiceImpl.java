package com.griddynamics.jagger.dbapi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.dto.DecisionPerMetricDto;
import com.griddynamics.jagger.dbapi.dto.DecisionPerSessionDto;
import com.griddynamics.jagger.dbapi.dto.DecisionPerTestDto;
import com.griddynamics.jagger.dbapi.dto.DecisionPerTestGroupDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.NodeInfoDto;
import com.griddynamics.jagger.dbapi.dto.NodeInfoPerSessionDto;
import com.griddynamics.jagger.dbapi.dto.PlotIntegratedDto;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.SummaryIntegratedDto;
import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.dto.TaskDecisionDto;
import com.griddynamics.jagger.dbapi.dto.TestInfoDto;
import com.griddynamics.jagger.dbapi.entity.DecisionPerMetricEntity;
import com.griddynamics.jagger.dbapi.entity.DecisionPerSessionEntity;
import com.griddynamics.jagger.dbapi.entity.DecisionPerTaskEntity;
import com.griddynamics.jagger.dbapi.entity.NodeInfoEntity;
import com.griddynamics.jagger.dbapi.entity.NodePropertyEntity;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.dbapi.fetcher.CustomMetricPlotFetcher;
import com.griddynamics.jagger.dbapi.fetcher.CustomMetricSummaryFetcher;
import com.griddynamics.jagger.dbapi.fetcher.CustomTestGroupMetricPlotFetcher;
import com.griddynamics.jagger.dbapi.fetcher.CustomTestGroupMetricSummaryFetcher;
import com.griddynamics.jagger.dbapi.fetcher.MetricDataFetcher;
import com.griddynamics.jagger.dbapi.fetcher.PlotsDbMetricDataFetcher;
import com.griddynamics.jagger.dbapi.fetcher.SessionScopeTestGroupMetricPlotFetcher;
import com.griddynamics.jagger.dbapi.fetcher.ValidatorSummaryFetcher;
import com.griddynamics.jagger.dbapi.model.DetailsNode;
import com.griddynamics.jagger.dbapi.model.LegendNode;
import com.griddynamics.jagger.dbapi.model.MetricGroupNode;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.MetricRankingProvider;
import com.griddynamics.jagger.dbapi.model.NameTokens;
import com.griddynamics.jagger.dbapi.model.PlotNode;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.model.SessionInfoNode;
import com.griddynamics.jagger.dbapi.model.SummaryNode;
import com.griddynamics.jagger.dbapi.model.TestDetailsNode;
import com.griddynamics.jagger.dbapi.model.TestInfoNode;
import com.griddynamics.jagger.dbapi.model.TestNode;
import com.griddynamics.jagger.dbapi.model.rules.LegendTreeViewGroupRuleProvider;
import com.griddynamics.jagger.dbapi.model.rules.TreeViewGroupMetricsToNodeRule;
import com.griddynamics.jagger.dbapi.model.rules.TreeViewGroupMetricsToNodeRuleProvider;
import com.griddynamics.jagger.dbapi.model.rules.TreeViewGroupRule;
import com.griddynamics.jagger.dbapi.model.rules.TreeViewGroupRuleProvider;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.dbapi.provider.CustomMetricNameProvider;
import com.griddynamics.jagger.dbapi.provider.CustomMetricPlotNameProvider;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProvider;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProviderImpl;
import com.griddynamics.jagger.dbapi.provider.ValidatorNamesProvider;
import com.griddynamics.jagger.dbapi.util.CommonUtils;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;
import com.griddynamics.jagger.dbapi.util.FetchUtil;
import com.griddynamics.jagger.dbapi.util.LegendProvider;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.TEST_GROUP_METRIC;
import static java.util.stream.Collectors.toList;

/**
 * Created by kgribov on 4/2/14.
 */
@SuppressWarnings({"unchecked", "SpellCheckingInspection", "DefaultFileTemplate"})
@Service("databaseService")
public class DatabaseServiceImpl implements DatabaseService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    @Qualifier("executorService")
    private ExecutorService threadPool;

    @Autowired
    private LegendProvider legendProvider;

    @Resource
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    private Map<String, Set<String>> defaultMonitoringParams = new HashMap<>();

    @Autowired
    private CustomMetricPlotNameProvider customMetricPlotNameProvider;

    @Autowired
    private CustomMetricNameProvider customMetricNameProvider;

    @Autowired
    private ValidatorNamesProvider validatorNamesProvider;

    @Autowired
    private SessionInfoProviderImpl sessionInfoServiceImpl;

    @Autowired
    private TreeViewGroupRuleProvider treeViewGroupRuleProvider;

    @Autowired
    private LegendTreeViewGroupRuleProvider legendTreeViewGroupRuleProvider;

    @Autowired
    private TreeViewGroupMetricsToNodeRuleProvider treeViewGroupMetricsToNodeRuleProvider;

    @Autowired
    private CustomMetricPlotFetcher customMetricPlotFetcher;

    @Autowired
    private CustomTestGroupMetricPlotFetcher customTestGroupMetricPlotFetcher;

    @Autowired
    private SessionScopeTestGroupMetricPlotFetcher sessionScopeTestGroupMetricPlotFetcher;

    @Autowired
    private CustomMetricSummaryFetcher customMetricSummaryFetcher;

    @Autowired
    private CustomTestGroupMetricSummaryFetcher customTestGroupMetricSummaryFetcher;

    @Autowired
    private ValidatorSummaryFetcher validatorSummaryFetcher;

    @Autowired
    private FetchUtil fetchUtil;

    @PostConstruct
    public void postConstruct() {
        this.defaultMonitoringParams = getDefaultMonitoringParametersMap(monitoringPlotGroups);
        this.sessionInfoServiceImpl.setIsTagsStorageAvailable(checkIfTagsStorageAvailable());
        this.sessionInfoServiceImpl.setIsUserCommentStorageAvailable(checkIfUserCommentStorageAvailable());
    }

    //===========================
    //=======Get plot data=======
    //===========================
    @Override
    public Map<MetricNode, PlotIntegratedDto> getPlotDataByMetricNode(Set<MetricNode> metricNodes) {

        if (metricNodes.isEmpty()) {
            return Collections.emptyMap();
        }
        long startTime = System.currentTimeMillis();

        Set<MetricNameDto> metricNameDtoSet = MetricNameUtil.getMetricNameDtoSet(metricNodes);
        Map<MetricNameDto, List<PlotSingleDto>> resultMap = getPlotDataByMetricNameDto(metricNameDtoSet);
        Multimap<MetricNode, PlotSingleDto> tempMultiMap = ArrayListMultimap.create();
        for (Map.Entry<MetricNameDto, List<PlotSingleDto>> entry : resultMap.entrySet()) {
            for (MetricNode metricNode : metricNodes) {
                if (metricNode.getMetricNameDtoList().contains(entry.getKey())) {
                    tempMultiMap.putAll(metricNode, entry.getValue());
                    break;
                }
            }
        }

        Map<MetricNode, PlotIntegratedDto> result = new HashMap<>();
        for (MetricNode metricNode : metricNodes) {
            List<PlotSingleDto> plotDatasetDtoList = new ArrayList<>(tempMultiMap.get(metricNode));

            // Sort lines by legend
            Collections.sort(plotDatasetDtoList, (o1, o2) -> {
                String param1 = o1.getLegend();
                String param2 = o2.getLegend();
                int res = String.CASE_INSENSITIVE_ORDER.compare(param1, param2);
                return (res != 0) ? res : param1.compareTo(param2);
            });

            result.put(metricNode, createPlotIntegratedDto(metricNode, plotDatasetDtoList, "Time, sec"));
        }

        log.debug("Total time of plots for metricNodes retrieving : {}", System.currentTimeMillis() - startTime);
        return result;
    }


    /**
     * Creates plot for given MetricNode and lines referred to it
     *
     * @param metricNode metric node for witch plot should be created
     * @param curves     lines of plot
     * @param xAxisLabel x axis label
     * @return plot for given MetricNode
     */
    private PlotIntegratedDto createPlotIntegratedDto(MetricNode metricNode, List<PlotSingleDto> curves, String xAxisLabel) {
        String taskName = metricNode.getMetricNameDtoList().get(0).getTest().getTaskName();

        MetricNameDto firstMetricNameDto = metricNode.getMetricNameDtoList().get(0);
        String plotHeader;
        if (isSessionScopeMetric(firstMetricNameDto))
            plotHeader = legendProvider.generateSessionScopePlotHeader(metricNode.getDisplayName());
        else
            plotHeader = legendProvider.generatePlotHeader(taskName, metricNode.getDisplayName());
        return new PlotIntegratedDto(createLegendTree(metricNode, curves), xAxisLabel, "", plotHeader);
    }


    /**
     * Creates legend as tree with LegendNode as leafs
     *
     * @param metricNode metricNode for witch legend tree should be created
     * @param curves     lines of plot
     * @return legend tree
     */
    private MetricGroupNode<LegendNode> createLegendTree(MetricNode metricNode, List<PlotSingleDto> curves) {
        Map<String, List<LegendNode>> legendGroupsMap = new HashMap<>();
        Set<String> legendGroups = new HashSet<>();

        // used to allow grouping identical legends
        int i = 1;
        for (PlotSingleDto curve : curves) {

            LegendNode mn = new LegendNode();
            String legend = curve.getLegend();

            mn.setId((i++) + legend);
            mn.setDisplayName(legend);
            mn.setLine(curve);

            // dummy metricNameDto is needed only to use same method of grouping nodes (TreeViewGroupRule.filter())
            MetricNameDto metricNameDto = new MetricNameDto(null, mn.getId(), mn.getDisplayName());
            mn.setMetricNameDtoList(Collections.singletonList(metricNameDto));

            String metricName = LegendProvider.parseMetricName(legend);
            if (!legendGroupsMap.containsKey(metricName)) {
                legendGroupsMap.put(metricName, new ArrayList<>());
            }

            legendGroupsMap.get(metricName).add(mn);
        }

        List<LegendNode> metricNodeList = new ArrayList<>();
        for (Map.Entry<String, List<LegendNode>> entry : legendGroupsMap.entrySet()) {
            metricNodeList.addAll(entry.getValue());
            if (entry.getValue().size() > 1) {
                for (MetricNode mn : entry.getValue()) {
                    String sessionId = LegendProvider.parseSessionId(mn.getDisplayName());
                    if (sessionId != null) {
                        mn.setDisplayName(sessionId);
                    }
                }
                legendGroups.add(entry.getKey());
            }
        }

        // only legends with sessions should be grouped
        // first '[0-9]+' used to escape first number, used to enable grouping identical legends.
        String legendFormat = "[0-9]+" + legendProvider.generatePlotLegend("[0-9]+", "%s", true);

        // rules to create legend tree view
        TreeViewGroupRule groupedNodesRule = legendTreeViewGroupRuleProvider.provide(metricNode.getId(), legendGroups, legendFormat);

        // tree with metrics distributed by groups
        return groupedNodesRule.filter(null, metricNodeList);
    }

    @Override
    public Map<MetricNameDto, List<PlotSingleDto>> getPlotDataByMetricNameDto(Set<MetricNameDto> metricNames) throws IllegalArgumentException {

        if (metricNames.isEmpty()) {
            return Collections.emptyMap();
        }
        final long temp = System.currentTimeMillis();

        final Multimap<PlotsDbMetricDataFetcher, MetricNameDto> fetchMap = ArrayListMultimap.create();
        for (MetricNameDto metricNameDto : metricNames) {
            switch (metricNameDto.getOrigin()) {
                case METRIC:
                    fetchMap.put(customMetricPlotFetcher, metricNameDto);
                    break;
                case TEST_GROUP_METRIC:
                    fetchMap.put(customTestGroupMetricPlotFetcher, metricNameDto);
                    break;
                case SESSION_SCOPE_TG:
                    fetchMap.put(sessionScopeTestGroupMetricPlotFetcher, metricNameDto);
                    break;
                default:  // if anything else
                    log.error("MetricNameDto with origin : {} appears in metric name list for plot retrieving ({})",
                            metricNameDto.getOrigin(), metricNameDto);
                    throw new RuntimeException(String.format(
                            "Unable to get plot for metric %s with origin: %s",
                            metricNameDto.getMetricName(), metricNameDto.getOrigin().toString()
                    ));
            }
        }

        List<Future<Set<Pair<MetricNameDto, List<PlotSingleDto>>>>> futures = new ArrayList<>();
        for (final PlotsDbMetricDataFetcher fetcher : fetchMap.keySet()) {
            futures.add(threadPool.submit(() -> fetcher.getResult(new ArrayList<>(fetchMap.get(fetcher)))));
        }

        Set<Pair<MetricNameDto, List<PlotSingleDto>>> resultSet = new HashSet<>();
        try {
            for (Future<Set<Pair<MetricNameDto, List<PlotSingleDto>>>> future : futures) {
                resultSet.addAll(future.get());
            }
        } catch (Throwable th) {
            log.error("Exception while plots retrieving", th);
            throw new RuntimeException("Exception while plots retrieving", th);
        }

        Map<MetricNameDto, List<PlotSingleDto>> result = new HashMap<>();
        for (Pair<MetricNameDto, List<PlotSingleDto>> pair : resultSet) {
            result.put(pair.getFirst(), pair.getSecond());
        }

        log.debug("Total time of plots for metricNameDtos retrieving: {}", System.currentTimeMillis() - temp);
        return result;
    }

    //===========================
    //=====Get control tree======
    //===========================

    @Override
    public RootNode getControlTreeForSessions(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup) throws RuntimeException {

        try {

            long temp = System.currentTimeMillis();

            RootNode rootNode = new RootNode();

            List<TaskDataDto> taskList = fetchTaskDatas(sessionIds, sessionMatchingSetup);

            Future<SummaryNode> summaryFuture = threadPool.submit(new SummaryNodeFetcherThread(taskList));
            Future<DetailsNode> detailsNodeFuture = threadPool.submit(new DetailsNodeFetcherThread(sessionIds, taskList));

            SummaryNode summaryNode = summaryFuture.get();
            DetailsNode detailsNode = detailsNodeFuture.get();

            rootNode.setSummaryNode(summaryNode);
            rootNode.setDetailsNode(detailsNode);

            log.info("Total time fetching all data for control tree : {} ms", (System.currentTimeMillis() - temp));

            return rootNode;
        } catch (Throwable th) {
            log.error("Error while creating Control Tree", th);
            th.printStackTrace();
            throw new RuntimeException(th);
        }
    }

    //===========================
    //=====Get summary data======
    //===========================

    @Override
    public Map<MetricNode, SummaryIntegratedDto> getSummaryByMetricNodes(Set<MetricNode> metricNodes, boolean isEnableDecisionsPerMetricFetching) {

        if (metricNodes.isEmpty()) {
            return Collections.emptyMap();
        }

        long temp = System.currentTimeMillis();
        Set<MetricNameDto> metricNameDtoSet = MetricNameUtil.getMetricNameDtoSet(metricNodes);

        Collection<SummarySingleDto> allMetricDto = getSummaryByMetricNameDto(metricNameDtoSet, isEnableDecisionsPerMetricFetching).values();

        // filter results by MetricNode
        Multimap<MetricNode, SummarySingleDto> tempMap = ArrayListMultimap.create();
        for (SummarySingleDto singleSumDto : allMetricDto) {
            for (MetricNode metricNode : metricNodes) {
                if (metricNode.getMetricNameDtoList().contains(singleSumDto.getMetricName())) {
                    tempMap.put(metricNode, singleSumDto);
                    break;
                }
            }
        }

        // generate result map
        Map<MetricNode, SummaryIntegratedDto> resultMap = new HashMap<>(tempMap.size());
        for (MetricNode metricNode : tempMap.keySet()) {

            List<SummarySingleDto> sumCollection = new ArrayList<>(tempMap.get(metricNode));
            List<PlotSingleDto> plotSingleDtos = new ArrayList<>(sumCollection.size());
            MetricRankingProvider.sortMetrics(sumCollection);

            plotSingleDtos.addAll(sumCollection.stream().map(DataProcessingUtil::generatePlotSingleDto).collect(toList()));

            SummaryIntegratedDto summaryDto = new SummaryIntegratedDto();
            summaryDto.setSummarySingleDtoList(sumCollection);
            summaryDto.setPlotIntegratedDto(createPlotIntegratedDto(metricNode, plotSingleDtos, "Sessions"));
            resultMap.put(metricNode, summaryDto);
        }

        log.debug("Total time of Summary Data retrieving for " + metricNodes.size() + " metric nodes : " + (System.currentTimeMillis() - temp));

        return resultMap;
    }


    @Override
    public Map<MetricNameDto, SummarySingleDto> getSummaryByMetricNameDto(Set<MetricNameDto> metricNames, boolean
            isEnableDecisionsPerMetricFetching) {

        final long temp = System.currentTimeMillis();

        final Multimap<MetricDataFetcher<SummarySingleDto>, MetricNameDto> fetchMap = ArrayListMultimap.create();

        for (MetricNameDto metricName : metricNames) {
            switch (metricName.getOrigin()) {
                case METRIC:
                    fetchMap.put(customMetricSummaryFetcher, metricName);
                    break;
                case TEST_GROUP_METRIC:
                    fetchMap.put(customTestGroupMetricSummaryFetcher, metricName);
                    break;
                case VALIDATOR:
                    fetchMap.put(validatorSummaryFetcher, metricName);
                    break;
                case SESSION_SCOPE_TG:
                    break;
                default:  // if anything else
                    log.error("MetricNameDto with origin : {} appears in metric name list for summary retrieving ({})", metricName.getOrigin(),
                            metricName);
                    throw new RuntimeException("Unable to get summary data for metric " + metricName.getMetricName() +
                            " with origin: " + metricName.getOrigin());
            }
        }

        List<Future<Set<SummarySingleDto>>> futures = new ArrayList<>();
        for (final MetricDataFetcher<SummarySingleDto> fetcher : fetchMap.keySet()) {
            futures.add(threadPool.submit(() -> fetcher.getResult(new ArrayList<>(fetchMap.get(fetcher)))));
        }
        Set<SummarySingleDto> result = new HashSet<>(metricNames.size());
        try {
            for (Future<Set<SummarySingleDto>> future : futures) {
                result.addAll(future.get());
            }
        } catch (Throwable th) {
            th.printStackTrace();
            log.error("Exception while summary retrieving", th);
            throw new RuntimeException("Exception while summary retrieving" + th.getMessage());
        }

        // Find what decisions were taken for metrics
        if (isEnableDecisionsPerMetricFetching) {
            Map<MetricNameDto, Map<String, Decision>> metricDecisions = getDecisionsPerMetric(metricNames);
            if (!metricDecisions.isEmpty()) {
                for (SummarySingleDto metricDto : result) {
                    MetricNameDto metricName = metricDto.getMetricName();

                    if (metricDecisions.containsKey(metricName)) {
                        Map<String, Decision> decisionPerSession = metricDecisions.get(metricName);
                        for (SummaryMetricValueDto metricValueDto : metricDto.getValues()) {
                            String sessionId = Long.toString(metricValueDto.getSessionId());

                            if (decisionPerSession.containsKey(sessionId)) {
                                metricValueDto.setDecision(decisionPerSession.get(sessionId));
                            }
                        }
                    }
                }
            }
        }

        Map<MetricNameDto, SummarySingleDto> resultMap = new HashMap<>(result.size());
        for (SummarySingleDto ssd : result) {
            resultMap.put(ssd.getMetricName(), ssd);
        }
        log.debug("{} ms spent for fetching summary data for {} metrics", System.currentTimeMillis() - temp, metricNames.size());

        return resultMap;
    }

    @Override
    public Map<String, Set<String>> getDefaultMonitoringParameters() {
        return defaultMonitoringParams;
    }

    private Map<String, Set<String>> getDefaultMonitoringParametersMap(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        // relation of old monitoring names from Groupkey (were used in hyperlinks) to
        // new monitoring metric ids from DefaultMonitoringParameters
        // necessary to process old hyperlinks by new client
        Map<String, Set<String>> result = new HashMap<>();

        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> groupKeyEntry : monitoringPlotGroups.entrySet()) {
            String key = groupKeyEntry.getKey().getUpperName();
            if (!result.containsKey(key)) {
                result.put(key, new HashSet<>());
            }

            for (DefaultMonitoringParameters defaultMonitoringParameters : groupKeyEntry.getValue()) {
                result.get(key).add(defaultMonitoringParameters.getId());
            }
        }

        return result;
    }

    private Map<TaskDataDto, List<MetricNode>> getTestMetricsMap(final List<TaskDataDto> dtos) {
        Long time = System.currentTimeMillis();
        List<MetricNameDto> list = new ArrayList<>();

        try {
            Future<Set<MetricNameDto>> customMetricNamesFuture = threadPool.submit(() -> customMetricNameProvider.getMetricNames(dtos));
            Future<Set<MetricNameDto>> validatorsNamesFuture = threadPool.submit(() -> validatorNamesProvider.getMetricNames(dtos));

            list.addAll(customMetricNamesFuture.get());
            list.addAll(validatorsNamesFuture.get());
        } catch (Exception e) {
            log.error("Exception occurs while fetching MetricNames for tests : ", e);
            throw new RuntimeException(e);
        }

        log.debug("Search metric names for tasks: {}", dtos);
        log.info("For {} tasks were found {} metrics names for {} ms", new Object[]{dtos.size(), list.size(), System.currentTimeMillis() - time});

        Map<TaskDataDto, List<MetricNode>> result = new HashMap<>();

        for (MetricNameDto mnd : list) {
            if ((mnd.getMetricName() == null) || (mnd.getMetricName().equals(""))) {
                log.warn("Metric with undefined id detected. It will be ignored. Details: " + mnd);
            } else {
                for (TaskDataDto tdd : dtos) {
                    if (tdd.getIds().containsAll(mnd.getTaskIds())) {
                        if (!result.containsKey(tdd)) {
                            result.put(tdd, new ArrayList<>());
                        }
                        MetricNode mn = new MetricNode();
                        String id = NameTokens.SUMMARY_PREFIX + tdd.hashCode() + mnd.getMetricName();
                        mn.init(id, mnd.getMetricDisplayName(), Collections.singletonList(mnd));
                        result.get(tdd).add(mn);
                        break;
                    }
                }
            }
        }

        return result;
    }

    private Map<TaskDataDto, List<PlotNode>> getTestPlotsMap(Set<String> sessionIds, List<TaskDataDto> taskList) {
        Map<TaskDataDto, List<PlotNode>> result = new HashMap<>();
        List<MetricNameDto> metricNameDtoList = new ArrayList<>();
        try {
            metricNameDtoList.addAll(customMetricPlotNameProvider.getPlotNames(taskList));

            log.debug("For sessions {} are available these plots: {}", sessionIds, metricNameDtoList);

            for (MetricNameDto pnd : metricNameDtoList) {
                if ((pnd.getMetricName() == null) || (pnd.getMetricName().equals(""))) {
                    log.warn("Metric with undefined id detected. It will be ignored. Details: " + pnd);
                } else {
                    for (TaskDataDto tdd : taskList) {
                        if (!result.containsKey(tdd)) {
                            result.put(tdd, new ArrayList<>());
                        }
                        if (tdd.getIds().containsAll(pnd.getTaskIds())) {

                            PlotNode pn = new PlotNode();
                            String id = NameTokens.METRICS_PREFIX + tdd.hashCode() + pnd.getMetricName() + pnd.getOrigin();
                            pn.init(id, pnd.getMetricDisplayName(), Collections.singletonList(pnd));
                            result.get(tdd).add(pn);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds + ", tasks : " + taskList, e);
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup) {

        final long timestamp = System.currentTimeMillis();

        int havingCount = 0;
        if (sessionMatchingSetup.isShowOnlyMatchedTests()) {
            havingCount = sessionIds.size();
        }

        List<Object[]> list = entityManager.createNativeQuery(
                "select taskData.id, commonTests.name, commonTests.description, taskData.taskId , commonTests.clock, commonTests" +
                        ".clockValue, commonTests.termination, taskData.sessionId" +
                        " from " +
                        "( " +
                        "    select test.name, test.description, test.version, test.sessionId, test.taskId, test.clock, test.clockValue, " +
                        "test.termination from " +
                        "    ( " +
                        "        select " +
                        "        l.*, s.name, s.description, s.version " +
                        "        from " +
                        "        (select * from WorkloadTaskData where sessionId in (:sessions)) as l " +
                        "        left outer join " +
                        "        (select * from WorkloadDetails) as s " +
                        "        on l.scenario_id=s.id " +
                        "    ) as test " +
                        "    inner join " +
                        "    ( " +
                        "        select t.* from " +
                        "        ( " +
                        "            select " +
                        "            l.*, s.name, s.description, s.version " +
                        "            from " +
                        "            (select * from WorkloadTaskData where sessionId in (:sessions)) as l " +
                        "            left outer join " +
                        "            (select * from WorkloadDetails) as s " +
                        "            on l.scenario_id=s.id " +
                        "        ) as t " +
                        "        group by " +
                        "        t.termination, t.clock, t.clockValue, t.name, t.description, t.version " +
                        "        having count(t.id)>=" + havingCount +
                        "    ) as testArch " +
                        "    on " +
                        "    test.clock=testArch.clock and " +
                        "    test.clockValue=testArch.clockValue and " +
                        "    test.termination=testArch.termination and " +
                        "    test.name=testArch.name and " +
                        "    test.version=testArch.version " +
                        ") as commonTests " +
                        "left outer join " +
                        "(select * from TaskData where sessionId in (:sessions)) as taskData " +
                        "on " +
                        "commonTests.sessionId=taskData.sessionId and " +
                        "commonTests.taskId=taskData.taskId "
        )
                .setParameter("sessions", sessionIds)
                .getResultList();

        //group tests by description
        HashMap<String, TaskDataDto> map = new HashMap<>(list.size());
        HashMap<String, Integer> mapIds = new HashMap<>(list.size());
        int i = 0;
        for (Object[] testData : list) {
            BigInteger id = (BigInteger) testData[0];
            String name = (String) testData[1];
            String description = (String) testData[2];
            String taskId = (String) testData[3];

            // we need clock , and termination here is tool of matching test.
            String clock = (String) testData[4];
            Integer clockValue = (Integer) testData[5];
            String termination = (String) testData[6];

            String sessionId = (String) testData[7];

            int taskIdInt = Integer.parseInt(taskId.substring(5));

            // key - defines how to match tests when several sessions are selected
            StringBuilder key = new StringBuilder(255);
            // uniqueIdParams - is used to generate unique Ids for nodes in control tree depending on session matching strategy
            List<String> uniqueIdParams = new ArrayList<>();

            // Define matching setup
            Set<SessionMatchingSetup.MatchBy> matchingSetup = sessionMatchingSetup.getMatchingSetup();
            if (matchingSetup.isEmpty()) {
                // no matching at all

                key.append(i++);

                uniqueIdParams.add(description);
                uniqueIdParams.add(name);
                uniqueIdParams.add(termination);
                uniqueIdParams.add(clock);
                uniqueIdParams.add(clockValue.toString());
                // sessionId is required to display tests with all equal attributes (description, name, etc)
                uniqueIdParams.add(sessionId);
            } else {
                if (matchingSetup.contains(SessionMatchingSetup.MatchBy.DESCRIPTION) || (matchingSetup.contains(SessionMatchingSetup.MatchBy.ALL))) {
                    key.append(description);
                    uniqueIdParams.add(description);
                }
                if (matchingSetup.contains(SessionMatchingSetup.MatchBy.NAME) || (matchingSetup.contains(SessionMatchingSetup.MatchBy.ALL))) {
                    key.append(name);
                    uniqueIdParams.add(name);
                }
                if (matchingSetup.contains(SessionMatchingSetup.MatchBy.TERMINATION) || (matchingSetup.contains(SessionMatchingSetup.MatchBy.ALL))) {
                    key.append(termination);
                    uniqueIdParams.add(termination);
                }
                if (matchingSetup.contains(SessionMatchingSetup.MatchBy.CLOCK) || (matchingSetup.contains(SessionMatchingSetup.MatchBy.ALL))) {
                    key.append(clock);
                    uniqueIdParams.add(clock);
                }
                if (matchingSetup.contains(SessionMatchingSetup.MatchBy.CLOCK_VALUE) || (matchingSetup.contains(SessionMatchingSetup.MatchBy.ALL))) {
                    key.append(clockValue);
                    uniqueIdParams.add(clockValue.toString());
                }
            }

            // Provide matching
            if (map.containsKey(key.toString())) {
                map.get(key.toString()).getIdToSessionId().put(id.longValue(), sessionId);

                Integer oldValue = mapIds.get(key.toString());
                mapIds.put(key.toString(), (oldValue == null ? 0 : oldValue) + taskIdInt);
            } else {
                TaskDataDto taskDataDto = new TaskDataDto(id.longValue(), sessionId, name, description);
                // generate unique id to make difference between tests with different matching parameters.
                taskDataDto.setUniqueId(CommonUtils.generateUniqueId(uniqueIdParams));

                map.put(key.toString(), taskDataDto);
                mapIds.put(key.toString(), taskIdInt);
            }
        }

        if (map.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        PriorityQueue<Object[]> priorityQueue = new PriorityQueue<>(mapIds.size(), (o1, o2) -> ((Comparable) o1[0]).compareTo(o2[0]));

        for (String key : map.keySet()) {
            TaskDataDto taskDataDto = map.get(key);
            priorityQueue.add(new Object[]{mapIds.get(key), taskDataDto});
        }

        ArrayList<TaskDataDto> result = new ArrayList<>(priorityQueue.size());
        while (!priorityQueue.isEmpty()) {
            result.add((TaskDataDto) priorityQueue.poll()[1]);
        }

        log.info("For sessions {} was loaded {} tasks for {} ms", new Object[]{sessionIds, result.size(), System.currentTimeMillis() - timestamp});
        return result;
    }

    public boolean checkIfUserCommentStorageAvailable() {
        try {
            // even if table is empty we can set user comments
            entityManager.createQuery("select count(sm) from SessionMetaDataEntity sm").getSingleResult();
            return true;
        } catch (Exception e) {
            log.warn("Could not access SessionMetaDataTable");
        }
        return false;
    }

    public boolean checkIfTagsStorageAvailable() {
        try {
            entityManager.createQuery("select 1 from TagEntity").getSingleResult();
            return true;
        } catch (Exception e) {
            log.warn("Could not access TagEntity table");
        }
        return false;
    }

    private List<TaskDataDto> fetchTaskDatas(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup) {
        long temp = System.currentTimeMillis();
        List<TaskDataDto> tddos = getTaskDataForSessions(sessionIds, sessionMatchingSetup);
        log.debug("load tests : {} for summary with {} ms", tddos, System.currentTimeMillis() - temp);
        return tddos;
    }

    private DetailsNode getDetailsNode(final Set<String> sessionIds, final List<TaskDataDto> taskList) {
        DetailsNode detailsNode = new DetailsNode(NameTokens.CONTROL_METRICS, NameTokens.CONTROL_METRICS);

        if (taskList.isEmpty())
            return detailsNode;

        List<TestDetailsNode> taskDataDtoList = new ArrayList<>();
        MetricGroupNode<PlotNode> sessionScopeNode = null;

        try {
            Future<Map<TaskDataDto, List<PlotNode>>> metricsPlotsMapFuture = threadPool.submit(
                    () -> getTestPlotsMap(sessionIds, taskList));

            //a first list element is a map with test nodes
            //a second is a map with nodes for session scope
            List<Map<TaskDataDto, List<PlotNode>>> maps = separateTestAndSessionScope(metricsPlotsMapFuture.get());

            Map<TaskDataDto, List<PlotNode>> map = maps.get(0);
            Map<TaskDataDto, List<PlotNode>> mapSs = maps.get(1);

            // get agent names
            Set<PlotNode> plotNodeList = new HashSet<>();
            for (TaskDataDto taskDataDto : map.keySet()) {
                plotNodeList.addAll(map.get(taskDataDto));
            }
            Map<String, Set<String>> agentNames = getAgentNamesForMonitoringParameters(plotNodeList);


            //get nodes for session scope and Session Scope Node
            Set<PlotNode> ssPlotNodes;
            if (sessionIds.size() == 1) {
                String sessionId = sessionIds.iterator().next();
                ssPlotNodes = getSessionScopeNodes(mapSs, sessionId);
                if (ssPlotNodes.size() > 0) {
                    String rootIdSs = NameTokens.SESSION_SCOPE_PLOTS;
                    sessionScopeNode = buildTreeAccordingToRules(rootIdSs, agentNames, null, false, new ArrayList<>(ssPlotNodes));
                }
            }

            // get tree
            for (TaskDataDto tdd : taskList) {
                List<PlotNode> metricNodeList = new ArrayList<>();
                if (map.containsKey(tdd)) {
                    metricNodeList.addAll(map.get(tdd));
                }
                String rootId = NameTokens.METRICS_PREFIX + tdd.hashCode();

                if (metricNodeList.size() > 0) {
                    // apply rules how to build tree
                    MetricGroupNode<PlotNode> testDetailsNodeBase = buildTreeAccordingToRules(rootId, agentNames, null, false, metricNodeList);

                    // full test details node
                    TestDetailsNode testNode = new TestDetailsNode(testDetailsNodeBase);
                    testNode.setTaskDataDto(tdd);

                    taskDataDtoList.add(testNode);
                }
            }

            MetricRankingProvider.sortPlotNodes(taskDataDtoList);
            if (sessionScopeNode != null)
                detailsNode.setSessionScopeNode(sessionScopeNode);
            detailsNode.setTests(taskDataDtoList);
            return detailsNode;

        } catch (Exception e) {
            log.error("Exception occurs while fetching plotNames for sessions {}, and tests {}", sessionIds, taskList);
            throw new RuntimeException(e);
        }
    }

    private List<TestNode> getSummaryTaskNodeList(List<TaskDataDto> tasks) {

        List<TestNode> taskDataDtoList = new ArrayList<>();

        Map<TaskDataDto, List<MetricNode>> map = getTestMetricsMap(tasks);

        // get agent names
        Set<MetricNode> metricNodeListForAgentNames = new HashSet<>();
        for (TaskDataDto taskDataDto : map.keySet()) {
            metricNodeListForAgentNames.addAll(map.get(taskDataDto));
        }
        Map<String, Set<String>> agentNames = getAgentNamesForMonitoringParameters(metricNodeListForAgentNames);

        for (TaskDataDto tdd : map.keySet()) {
            List<MetricNode> metricNodeList = map.get(tdd);
            String rootId = NameTokens.SUMMARY_PREFIX + tdd.hashCode();

            Set<Double> percentiles = getPercentileValuesFromIds(metricNodeList);

            if (metricNodeList.size() > 0) {
                // apply rules how to build tree
                MetricGroupNode<MetricNode> testNodeBase = buildTreeAccordingToRules(rootId, agentNames, percentiles, true, metricNodeList);

                // full test node with info data
                TestNode testNode = new TestNode(testNodeBase);
                testNode.setTaskDataDto(tdd);
                TestInfoNode tin = new TestInfoNode(NameTokens.TEST_INFO + testNode.getId(), NameTokens.TEST_INFO);
                testNode.setTestInfo(tin);

                taskDataDtoList.add(testNode);
            }
        }

        MetricRankingProvider.sortPlotNodes(taskDataDtoList);
        return taskDataDtoList;
    }

    private class SummaryNodeFetcherThread implements Callable<SummaryNode> {
        private List<TaskDataDto> taskList;

        SummaryNodeFetcherThread(List<TaskDataDto> taskList) {
            this.taskList = taskList;
        }

        public SummaryNode call() {
            SummaryNode sn = new SummaryNode(NameTokens.CONTROL_SUMMARY_TRENDS, NameTokens.CONTROL_SUMMARY_TRENDS);
            SessionInfoNode sin = new SessionInfoNode(NameTokens.SESSION_INFO, NameTokens.SESSION_INFO);
            sn.setSessionInfo(sin);
            if (!taskList.isEmpty()) {
                sn.setTests(getSummaryTaskNodeList(taskList));
            }
            return sn;
        }
    }

    private class DetailsNodeFetcherThread implements Callable<DetailsNode> {
        private Set<String> sessionIds;
        private List<TaskDataDto> taskList;

        DetailsNodeFetcherThread(Set<String> sessionIds, List<TaskDataDto> taskList) {
            this.sessionIds = sessionIds;
            this.taskList = taskList;
        }

        public DetailsNode call() {
            return getDetailsNode(sessionIds, taskList);
        }
    }

    private Map<String, Set<String>> getAgentNamesForMonitoringParameters(Set<? extends MetricNode> nodeList) {
        Map<String, Set<String>> agentNames = new HashMap<>();

        for (MetricNode node : nodeList) {
            node.getMetricNameDtoList().stream()
                    .filter(metricNameDto -> metricNameDto.getOrigin() == TEST_GROUP_METRIC)
                    .map(metricNameDto -> MonitoringIdUtils.splitMonitoringMetricId(metricNameDto.getMetricName()))
                    .filter(Objects::nonNull)
                    .forEach(monitoringId ->
                            defaultMonitoringParams.keySet().stream()
                                    .filter(key -> defaultMonitoringParams.get(key).contains(monitoringId.getMonitoringName()))
                                    .forEach(key -> {
                                        agentNames.putIfAbsent(key, new HashSet<>());
                                        agentNames.get(key).add(monitoringId.getAgentName());
                                    }));
        }
        return agentNames;
    }

    private Set<Double> getPercentileValuesFromIds(List<? extends MetricNode> nodeList) {
        Set<Double> percentiles = new HashSet<>();
        for (MetricNode mn : nodeList) {
            mn.getMetricNameDtoList().stream()
                    .filter(mnd -> mnd.getMetricName().matches(StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX))
                    .forEach(mnd -> {
                        Double percentileKey = StandardMetricsNamesUtil.parseLatencyPercentileKey(mnd.getMetricName());
                        percentiles.add(percentileKey);
                    });
        }

        return percentiles;
    }

    /**
     * Build Tree of nodes according to rules
     *
     * @param rootId         id of root Node
     * @param agentNames     map of monitoring parameter -> agent names (null if not required)
     * @param percentiles    list of percentiles (null if not required)
     * @param forSummary     tells what node we are building now @n
     *                       true for Summary&Trends tab view - we show standard metrics as separate metric nodes @n
     *                       false for Metrics tab view - we group metrics (metricnameDto) to single metric node (example Latency, LatencyStdDev ->
     *                       Latency) @n
     * @param metricNodeList list of nodes to build tree
     * @param <M>            Node type that extends MetricNode
     * @return Tree of nodes
     */
    private <M extends MetricNode> MetricGroupNode<M> buildTreeAccordingToRules(String rootId, Map<String, Set<String>> agentNames,
                                                                                Set<Double> percentiles, boolean forSummary,
                                                                                List<M> metricNodeList) {

        // rules to unite metrics in single plot
        TreeViewGroupMetricsToNodeRule unitedMetricsRule = treeViewGroupMetricsToNodeRuleProvider.provide(
                agentNames, percentiles, forSummary
        );
        // unite metrics and add result to original list
        List<M> unitedMetrics = unitedMetricsRule.filter(rootId, metricNodeList);
        if (unitedMetrics != null) {
            metricNodeList.addAll(unitedMetrics);
        }


        Map<String, String> scenarioComponentsIdToDisplayName = new HashMap<>();
        metricNodeList.stream()
                .filter(metricNode -> StandardMetricsNamesUtil.isBelongingToScenario(metricNode.getId()))
                .forEach(metricNode -> {
                    MetricNameDto metricNameDto = metricNode.getMetricNameDtoList().get(0);
                    scenarioComponentsIdToDisplayName.put(metricNameDto.getMetricName(), metricNameDto.getMetricDisplayName());
                });

        // rules to create test tree view
        TreeViewGroupRule groupedNodesRule = treeViewGroupRuleProvider.provide(rootId, rootId, scenarioComponentsIdToDisplayName);
        // tree with metrics distributed by groups

        return groupedNodesRule.filter(null, metricNodeList);
    }

    @Override
    public Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfoByTaskDataDto(Collection<TaskDataDto> taskDataDtos) throws RuntimeException {
        if (taskDataDtos.isEmpty()) {
            return Collections.emptyMap();
        }
        long temp = System.currentTimeMillis();
        Set<Long> taskDataIds = new HashSet<>();
        for (TaskDataDto taskDataDto : taskDataDtos) {
            taskDataIds.addAll(taskDataDto.getIds());
        }
        Map<Long, Map<String, TestInfoDto>> preliminaryResult = getTestInfoByTaskIds(taskDataIds);
        Map<TaskDataDto, Map<String, TestInfoDto>> resultMap = new HashMap<>(taskDataDtos.size());
        for (Map.Entry<Long, Map<String, TestInfoDto>> entry : preliminaryResult.entrySet()) {
            for (TaskDataDto td : taskDataDtos) {
                if (td.getIds().contains(entry.getKey())) {
                    if (!resultMap.containsKey(td)) {
                        resultMap.put(td, new HashMap<>());
                    }
                    resultMap.get(td).putAll(entry.getValue());
                    break;
                }
            }
        }
        log.debug("Time spent for testInfo fetching for {} taskDataDtos : {}ms", taskDataDtos.size(), System.currentTimeMillis() - temp);
        return resultMap;
    }

    @Override
    public Map<Long, Map<String, TestInfoDto>> getTestInfoByTaskIds(Set<Long> taskIds) throws RuntimeException {
        return fetchUtil.getTestInfoByTaskIds(taskIds);
    }

    @Override
    public SessionInfoProvider getSessionInfoService() {
        return sessionInfoServiceImpl;
    }

    @Override
    public List<NodeInfoPerSessionDto> getNodeInfo(Set<String> sessionIds) {
        Long time = System.currentTimeMillis();
        List<NodeInfoPerSessionDto> nodeInfoPerSessionDtoList = new ArrayList<>();

        try {
            List<NodeInfoEntity> nodeInfoEntityList = (List<NodeInfoEntity>)
                    entityManager.createQuery("select nie from NodeInfoEntity as nie where nie.sessionId in (:sessionIds)")
                            .setParameter("sessionIds", new ArrayList<>(sessionIds))
                            .getResultList();

            Map<String, List<NodeInfoDto>> sessions = new HashMap<>();

            for (NodeInfoEntity nodeInfoEntity : nodeInfoEntityList) {
                Map<String, String> parameters = new HashMap<>();

                parameters.put("CPU model", nodeInfoEntity.getCpuModel());
                parameters.put("CPU frequency, MHz", String.valueOf(nodeInfoEntity.getCpuMHz()));
                parameters.put("CPU number of cores", String.valueOf(nodeInfoEntity.getCpuTotalCores()));
                parameters.put("CPU number of sockets", String.valueOf(nodeInfoEntity.getCpuTotalSockets()));
                parameters.put("Jagger Java version", nodeInfoEntity.getJaggerJavaVersion());
                parameters.put("OS name", nodeInfoEntity.getOsName());
                parameters.put("OS version", nodeInfoEntity.getOsVersion());
                parameters.put("System RAM, MB", String.valueOf(nodeInfoEntity.getSystemRAM()));

                List<NodePropertyEntity> nodePropertyEntityList = nodeInfoEntity.getProperties();
                for (NodePropertyEntity nodePropertyEntity : nodePropertyEntityList) {
                    parameters.put("Property '" + nodePropertyEntity.getName() + "'", nodePropertyEntity.getValue());
                }
                NodeInfoDto nodeInfoDto = new NodeInfoDto(nodeInfoEntity.getNodeId(), parameters);

                String sessionId = nodeInfoEntity.getSessionId();
                if (sessions.containsKey(sessionId)) {
                    sessions.get(sessionId).add(nodeInfoDto);
                } else {
                    List<NodeInfoDto> node = new ArrayList<>();
                    node.add(nodeInfoDto);
                    sessions.put(sessionId, node);
                }
            }

            nodeInfoPerSessionDtoList.addAll(sessions.entrySet().stream()
                    .map(session -> new NodeInfoPerSessionDto(session.getKey(), session.getValue())).collect(toList()));

            log.info("For session ids " + sessionIds + " were found node info values in " + (System.currentTimeMillis() - time) + " ms");
        } catch (PersistenceException ex) {
            log.info("No node info data was found for session id " + sessionIds, ex);
        } catch (Exception ex) {
            log.error("Error occurred during loading node info data for session ids " + sessionIds, ex);
            throw new RuntimeException("Error occurred during loading node info data for session ids " + sessionIds, ex);
        }
        return nodeInfoPerSessionDtoList;
    }

    @Override
    public List<String> getSessionIdsByTaskIds(Set<Long> taskIds) {
        return fetchUtil.getSessionIdsByTaskIds(taskIds);
    }

    @Override
    public Map<Long, Set<Long>> getTestGroupIdsByTestIds(Set<Long> taskIds) {
        Map<Long, Set<Long>> result = new HashMap<>();
        Multimap<Long, Long> tempResult = fetchUtil.getTestGroupIdsByTestIds(taskIds);
        for (Long testGroupId : tempResult.keySet()) {
            result.put(testGroupId, new HashSet<>(tempResult.get(testGroupId)));
        }
        return result;
    }

    @Override
    public Set<TaskDecisionDto> getDecisionsPerTask(Set<Long> taskIds) {

        if (taskIds.isEmpty()) {
            return Collections.emptySet();
        }

        Long time = System.currentTimeMillis();
        Set<TaskDecisionDto> taskDecisionDtoSet = new HashSet<>();

        try {
            List<DecisionPerTaskEntity> decisionPerTaskEntityList = (List<DecisionPerTaskEntity>)
                    entityManager.createQuery("select dpt from DecisionPerTaskEntity as dpt where dpt.taskData.id in (:taskIds)")
                            .setParameter("taskIds", taskIds)
                            .getResultList();

            for (DecisionPerTaskEntity decisionPerTaskEntity : decisionPerTaskEntityList) {
                TaskDecisionDto taskDecisionDto = new TaskDecisionDto();
                taskDecisionDto.setId(decisionPerTaskEntity.getTaskData().getId());
                taskDecisionDto.setName(decisionPerTaskEntity.getTaskData().getTaskName());
                taskDecisionDto.setDecision(Decision.valueOf(decisionPerTaskEntity.getDecision()));

                taskDecisionDtoSet.add(taskDecisionDto);
            }

            log.debug("For task ids " + taskIds + " were found decisions in " + (System.currentTimeMillis() - time) + " ms");
        } catch (NoResultException ex) {
            log.debug("No decisions were found for task ids " + taskIds, ex);
            return Collections.emptySet();
        } catch (PersistenceException ex) {
            log.debug("No decisions were found for task ids " + taskIds, ex);
            return Collections.emptySet();
        } catch (Exception ex) {
            log.error("Error occurred during loading decisions for task ids " + taskIds, ex);
            throw new RuntimeException("Error occurred during loading decisions for task ids " + taskIds, ex);
        }
        return taskDecisionDtoSet;
    }

    @Override
    public Map<MetricNameDto, Map<String, Decision>> getDecisionsPerMetric(Set<MetricNameDto> metricNames) {
        if (metricNames.isEmpty()) {
            return Collections.emptyMap();
        }

        Long time = System.currentTimeMillis();
        Map<MetricNameDto, Map<String, Decision>> result = new HashMap<>();
        Set<String> metricIds = new HashSet<>();
        Set<Long> taskIds = new HashSet<>();
        Set<Long> taskIdsWhereParentIdIsRequired = new HashSet<>();

        for (MetricNameDto metricName : metricNames) {
            metricIds.add(metricName.getMetricName());
            taskIds.addAll(metricName.getTaskIds());
            if (metricName.getOrigin().equals(TEST_GROUP_METRIC)) {
                taskIdsWhereParentIdIsRequired.addAll(metricName.getTaskIds());
            }
        }

        // add test group task ids when necessary
        Multimap<Long, Long> testGroupPerTest = null;
        if (!taskIdsWhereParentIdIsRequired.isEmpty()) {
            testGroupPerTest = fetchUtil.getTestGroupIdsByTestIds(taskIdsWhereParentIdIsRequired);
            taskIds.addAll(testGroupPerTest.keySet());
        }

        try {
            List<DecisionPerMetricEntity> decisionPerMetricEntityList = (List<DecisionPerMetricEntity>)
                    entityManager.createQuery("select dpm from DecisionPerMetricEntity as dpm" +
                            " where dpm.metricDescriptionEntity.taskData.id in (:taskIds) and dpm.metricDescriptionEntity.metricId in (:metricIds)")
                            .setParameter("taskIds", taskIds)
                            .setParameter("metricIds", metricIds)
                            .getResultList();


            Map<Long, Map<String, MetricNameDto>> mappedMetricDtos = MetricNameUtil.getMappedMetricDtos(metricNames);

            for (DecisionPerMetricEntity decisionPerMetricEntity : decisionPerMetricEntityList) {
                Set<Long> taskIdsForEntity = new HashSet<>();
                Long taskId = decisionPerMetricEntity.getMetricDescriptionEntity().getTaskData().getId();
                String sessionId = decisionPerMetricEntity.getMetricDescriptionEntity().getTaskData().getSessionId();
                String metricId = decisionPerMetricEntity.getMetricDescriptionEntity().getMetricId();

                if ((testGroupPerTest != null) && (testGroupPerTest.containsKey(taskId))) {
                    // this is test group task id => we will use tests from this test group
                    taskIdsForEntity.addAll(testGroupPerTest.get(taskId));
                } else {
                    taskIdsForEntity.add(taskId);
                }

                for (Long testTaskId : taskIdsForEntity) {
                    MetricNameDto metricNameDto;
                    try {
                        metricNameDto = mappedMetricDtos.get(testTaskId).get(metricId);
                        if (metricNameDto == null) {   // means that we fetched data that we had not wanted to fetch
                            continue;
                        }
                    } catch (NullPointerException e) {
                        throw new IllegalArgumentException("Could not find appropriate MetricDto with taskId: " + testTaskId + ", metricId: " +
                                metricId);
                    }

                    if (!result.containsKey(metricNameDto)) {
                        result.put(metricNameDto, new HashMap<>());
                    }
                    result.get(metricNameDto).put(sessionId, Decision.valueOf(decisionPerMetricEntity.getDecision()));
                }
            }

            log.debug("For metrics " + metricNames + " were found decisions in " + (System.currentTimeMillis() - time) + " ms");
        } catch (NoResultException ex) {
            log.debug("No decisions were found for metrics " + metricNames, ex);
            return Collections.emptyMap();
        } catch (PersistenceException ex) {
            log.debug("No decisions were found for metrics " + metricNames, ex);
            return Collections.emptyMap();
        } catch (Exception ex) {
            log.error("Error occurred during loading decisions for metrics " + metricNames, ex);
            throw new RuntimeException("Error occurred during loading decisions for metrics " + metricNames, ex);
        }
        return result;
    }

    @Override
    public Map<String, Decision> getDecisionsPerSession(Set<String> sessionIds) {
        if (sessionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Long time = System.currentTimeMillis();

        Map<String, Decision> result = new HashMap<>();

        try {
            List<DecisionPerSessionEntity> decisionPerSessionEntityList = (List<DecisionPerSessionEntity>)
                    entityManager.createQuery("select dps from DecisionPerSessionEntity as dps" +
                            " where dps.sessionId in (:sessionIds)")
                            .setParameter("sessionIds", sessionIds)
                            .getResultList();


            for (DecisionPerSessionEntity decisionPerSessionEntity : decisionPerSessionEntityList) {
                result.put(decisionPerSessionEntity.getSessionId(),
                        Decision.valueOf(decisionPerSessionEntity.getDecision()));
            }

            log.debug("For session ids " + sessionIds + " were found decisions in " + (System.currentTimeMillis() - time) + " ms");
        } catch (NoResultException ex) {
            log.debug("No decisions were found for session ids " + sessionIds, ex);
            return Collections.emptyMap();
        } catch (PersistenceException ex) {
            log.debug("No decisions were found for session ids " + sessionIds, ex);
            return Collections.emptyMap();
        } catch (Exception ex) {
            log.error("Error occurred during loading decisions for session ids " + sessionIds, ex);
            throw new RuntimeException("Error occurred during loading decisions for session ids " + sessionIds, ex);
        }
        return result;
    }

    @Override
    public DecisionPerSessionDto getDecisionPerSession(String sessionId) {
        DecisionPerSessionDto decisionPerSessionDto =
                new DecisionPerSessionDto((DecisionPerSessionEntity) entityManager.createQuery(
                        "select dps from DecisionPerSessionEntity as dps where dps.sessionId = :sessionId")
                                                                                  .setParameter("sessionId", sessionId)
                                                                                  .getSingleResult());

        List<DecisionPerTaskEntity> taskDecisions = (List<DecisionPerTaskEntity>)
                entityManager.createQuery("select dpt from DecisionPerTaskEntity as dpt where dpt.taskData.sessionId = :sessionId")
                        .setParameter("sessionId", sessionId).getResultList();
        if (!CollectionUtils.isEmpty(taskDecisions)) {
            decisionPerSessionDto.setTestGroupDecisions(getDecisionPerTestGroup(taskDecisions));
        }

        return decisionPerSessionDto;
    }

    private List<DecisionPerTestGroupDto> getDecisionPerTestGroup(List<DecisionPerTaskEntity> taskDecisions) {

        Set<Long> taskIds =
                taskDecisions.stream().map(decision -> decision.getTaskData().getId()).collect(Collectors.toSet());
        Map<Long, Set<Long>> testIdsByTestGroupIds = getTestGroupIdsByTestIds(taskIds);

        List<DecisionPerTestGroupDto> decisionPerTestGroupDtos = new ArrayList<>();
        List<DecisionPerTestDto> decisionPerTestDtos = new ArrayList<>();
        for (DecisionPerTaskEntity taskDecision : taskDecisions) {
            // get metric decisions for task
            List<DecisionPerMetricEntity> metricDecisions = (List<DecisionPerMetricEntity>)
                    entityManager.createQuery(
                    "select dpm from DecisionPerMetricEntity as dpm where dpm.metricDescriptionEntity.taskData.id = :taskId")
                                 .setParameter("taskId", taskDecision.getTaskData().getId())
                                 .getResultList();
            List<DecisionPerMetricDto> decisionPerMetricDtos =
                    metricDecisions.stream().map(DecisionPerMetricDto::new).collect(toList());

            // if task is test group - create new DecisionPerTestGroupDto and add it to list
            if (testIdsByTestGroupIds.containsKey(taskDecision.getTaskData().getId())) {
                DecisionPerTestGroupDto decisionPerTestGroupDto = new DecisionPerTestGroupDto(taskDecision);
                decisionPerTestGroupDto.setMetricDecisions(decisionPerMetricDtos);
                decisionPerTestGroupDtos.add(decisionPerTestGroupDto);
            } else {
                // otherwise create DecisionPerTestDto and add it to list
                DecisionPerTestDto decisionPerTestDto = new DecisionPerTestDto(taskDecision);
                decisionPerTestDto.setMetricDecisions(decisionPerMetricDtos);
                decisionPerTestDtos.add(decisionPerTestDto);
            }
        }

        // fill test group decisions with task decisions
        for (Map.Entry<Long, Set<Long>> entry : testIdsByTestGroupIds.entrySet()) {
            Long testGroupId = entry.getKey();
            Set<Long> tasks = entry.getValue();

            DecisionPerTestGroupDto testGroupDecision = decisionPerTestGroupDtos
                    .stream().filter(decision -> decision.getTaskData().getId().equals(testGroupId)).findFirst().get();

            List<DecisionPerTestDto> testGroupTestDecisions = decisionPerTestDtos
                    .stream().filter(decision -> tasks.contains(decision.getTaskData().getId())).collect(toList());
            testGroupDecision.setTestDecisions(testGroupTestDecisions);
        }

        return decisionPerTestGroupDtos;
    }

    @Override
    public List<DecisionPerSessionDto> getAllDecisions() {
        List<String> sessions = (List<String>) entityManager.createQuery("select dps.sessionId from DecisionPerSessionEntity as dps")
                .getResultList();
        return sessions.stream().map(this::getDecisionPerSession).collect(toList());
    }

    @Override
    public TaskData getTaskData(String taskId, String sessionId) {
        return fetchUtil.getTaskData(taskId, sessionId);
    }

    @Override
    public Map<Long, TaskData> getTaskData(Collection<Long> ids) {
        return fetchUtil.getTaskData(ids);
    }


    private Set<PlotNode> getSessionScopeNodes(Map<TaskDataDto, List<PlotNode>> mapAfterFiltration, String sessionId) {
        List<String> metricNameList = new ArrayList<>();
        Map<String, List<Long>> nameId = new HashMap<>();
        Set<PlotNode> ssPlotNodes = new HashSet<>();

        for (TaskDataDto taskDataDto : mapAfterFiltration.keySet()) {
            for (PlotNode plotNode : mapAfterFiltration.get(taskDataDto)) {
                for (MetricNameDto metricNameDto : plotNode.getMetricNameDtoList()) {

                    // we want to have every metric only one time
                    if (!metricNameList.contains(metricNameDto.getMetricName())) {
                        metricNameList.add(metricNameDto.getMetricName());
                        PlotNode ssPlotNode = new PlotNode();

                        TaskDataDto tempTaskDataDto = new TaskDataDto(taskDataDto.getIdToSessionId(),
                                taskDataDto.getTaskName(),
                                taskDataDto.getDescription());

                        MetricNameDto tempMetricNameDto = new MetricNameDto(tempTaskDataDto,
                                metricNameDto.getMetricName(),
                                metricNameDto.getMetricDisplayName(),
                                metricNameDto.getOrigin());

                        ssPlotNode.init(NameTokens.SESSION_SCOPE_PREFIX + metricNameDto.getMetricName(),
                                plotNode.getDisplayName(),
                                Collections.singletonList(tempMetricNameDto));

                        ssPlotNodes.add(ssPlotNode);
                    }
                    //we looking for all Id of TaskDataDto for every MetricNameDto
                    if (!nameId.containsKey(metricNameDto.getMetricName()))
                        nameId.put(metricNameDto.getMetricName(), new ArrayList<>());
                    nameId.get(metricNameDto.getMetricName()).addAll(metricNameDto.getTaskIds());
                }
            }
        }

        for (PlotNode plotNode : ssPlotNodes)
            for (MetricNameDto metricNameDto : plotNode.getMetricNameDtoList())
                for (Long taskId : nameId.get(metricNameDto.getMetricName()))
                    metricNameDto.getTest().getIdToSessionId().put(taskId, sessionId);

        return ssPlotNodes;
    }

    private List<Map<TaskDataDto, List<PlotNode>>> separateTestAndSessionScope(Map<TaskDataDto, List<PlotNode>> map) {
        Map<TaskDataDto, List<PlotNode>> mapForTests = new HashMap<>();
        Map<TaskDataDto, List<PlotNode>> mapForSessionScope = new HashMap<>();

        for (TaskDataDto taskDataDto : map.keySet()) {
            for (PlotNode plotNode : map.get(taskDataDto)) {
                for (MetricNameDto metricNameDto : plotNode.getMetricNameDtoList()) {
                    if (isSessionScopeMetric(metricNameDto)) {
                        mapForSessionScope.putIfAbsent(taskDataDto, new ArrayList<>());
                        mapForSessionScope.get(taskDataDto).add(plotNode);
                    } else {
                        mapForTests.putIfAbsent(taskDataDto, new ArrayList<>());
                        mapForTests.get(taskDataDto).add(plotNode);
                    }
                }
            }
        }
        return newArrayList(mapForTests, mapForSessionScope);
    }

    private boolean isSessionScopeMetric(MetricNameDto metricNameDto) {
        return metricNameDto.getOrigin() == MetricNameDto.Origin.SESSION_SCOPE_TG;
    }
}

