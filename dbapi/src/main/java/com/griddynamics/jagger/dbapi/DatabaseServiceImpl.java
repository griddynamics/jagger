package com.griddynamics.jagger.dbapi;

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
import com.griddynamics.jagger.dbapi.fetcher.DurationMetricSummaryFetcher;
import com.griddynamics.jagger.dbapi.fetcher.LatencyMetricPlotFetcher;
import com.griddynamics.jagger.dbapi.fetcher.LatencyMetricSummaryFetcher;
import com.griddynamics.jagger.dbapi.fetcher.MetricDataFetcher;
import com.griddynamics.jagger.dbapi.fetcher.MonitoringMetricPlotFetcher;
import com.griddynamics.jagger.dbapi.fetcher.PlotsDbMetricDataFetcher;
import com.griddynamics.jagger.dbapi.fetcher.SessionScopeMonitoringMetricPlotFetcher;
import com.griddynamics.jagger.dbapi.fetcher.SessionScopeTestGroupMetricPlotFetcher;
import com.griddynamics.jagger.dbapi.fetcher.StandardMetricSummaryFetcher;
import com.griddynamics.jagger.dbapi.fetcher.ThroughputMetricPlotFetcher;
import com.griddynamics.jagger.dbapi.fetcher.TimeLatencyPercentileMetricPlotFetcher;
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
import com.griddynamics.jagger.dbapi.parameter.DefaultWorkloadParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.dbapi.provider.CustomMetricNameProvider;
import com.griddynamics.jagger.dbapi.provider.CustomMetricPlotNameProvider;
import com.griddynamics.jagger.dbapi.provider.LatencyMetricNameProvider;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProvider;
import com.griddynamics.jagger.dbapi.provider.SessionInfoProviderImpl;
import com.griddynamics.jagger.dbapi.provider.StandardMetricNameProvider;
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

/**
 * Created by kgribov on 4/2/14.
 */
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
    private Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups;

    @Resource
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    private Map<String,Set<String>> defaultMonitoringParams = new HashMap<String, Set<String>>();

    @Autowired
    private StandardMetricNameProvider standardMetricNameProvider;

    @Autowired
    private CustomMetricPlotNameProvider customMetricPlotNameProvider;

    @Autowired
    private CustomMetricNameProvider customMetricNameProvider;

    @Autowired
    private LatencyMetricNameProvider latencyMetricNameProvider;

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
    private ThroughputMetricPlotFetcher throughputMetricPlotFetcher;

    @Autowired
    private LatencyMetricPlotFetcher latencyMetricPlotFetcher;

    @Autowired
    private TimeLatencyPercentileMetricPlotFetcher timeLatencyPercentileMetricPlotFetcher;

    // true way
    @Autowired
    private CustomMetricPlotFetcher customMetricPlotFetcher;

    // true way
    @Autowired
    private CustomTestGroupMetricPlotFetcher customTestGroupMetricPlotFetcher;

    @Autowired
    private MonitoringMetricPlotFetcher monitoringMetricPlotFetcher;

    // true way
    @Autowired
    private SessionScopeTestGroupMetricPlotFetcher sessionScopeTestGroupMetricPlotFetcher;

    @Autowired
    private SessionScopeMonitoringMetricPlotFetcher sessionScopeMonitoringMetricPlotFetcher;

    @Autowired // true way
    private StandardMetricSummaryFetcher standardMetricSummaryFetcher;

    @Autowired
    private DurationMetricSummaryFetcher durationMetricSummaryFetcher;

    @Autowired
    private LatencyMetricSummaryFetcher latencyMetricDataFetcher;

    @Autowired
    private CustomMetricSummaryFetcher customMetricSummaryFetcher;

    // true way
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

        Map<MetricNode, PlotIntegratedDto> result = new HashMap<MetricNode, PlotIntegratedDto>();
        for (MetricNode metricNode : metricNodes) {
            List<PlotSingleDto> plotDatasetDtoList = new ArrayList<PlotSingleDto>(tempMultiMap.get(metricNode));

            // Sort lines by legend
            Collections.sort(plotDatasetDtoList, new Comparator<PlotSingleDto>() {
                @Override
                public int compare(PlotSingleDto o1, PlotSingleDto o2) {
                    String param1 = o1.getLegend();
                    String param2 = o2.getLegend();
                    int res = String.CASE_INSENSITIVE_ORDER.compare(param1, param2);
                    return (res != 0) ? res : param1.compareTo(param2);
                }
            });

            result.put(metricNode, createPlotIntegratedDto(metricNode, plotDatasetDtoList, "Time, sec"));
        }

        log.debug("Total time of plots for metricNodes retrieving : {}", System.currentTimeMillis() - startTime);
        return result;
    }


    /**
     * Creates plot for given MetricNode and lines referred to it
     * @param metricNode metric node for witch plot should be created
     * @param curves lines of plot
     * @param xAxisLabel x axis label
     * @return plot for given MetricNode */
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
     * @param metricNode metricNode for witch legend tree should be created
     * @param curves lines of plot
     * @return legend tree */
    private MetricGroupNode<LegendNode> createLegendTree(MetricNode metricNode, List<PlotSingleDto> curves) {
        Map<String, List<LegendNode>> legendGroupsMap
                = new HashMap<String, List<LegendNode>>();
        Set<String> legendGroups = new HashSet<String>();

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
                legendGroupsMap.put(metricName, new ArrayList<LegendNode>());
            }

            legendGroupsMap.get(metricName).add(mn);
        }

        List<LegendNode> metricNodeList = new ArrayList<LegendNode>();
        for (Map.Entry<String, List<LegendNode>> entry : legendGroupsMap.entrySet()) {
            metricNodeList.addAll(entry.getValue());
            if (entry.getValue().size() > 1) {
                for (MetricNode mn : entry.getValue()) {
                    String sessionId = LegendProvider.parseSessionId(mn.getDisplayName());
                    if (sessionId  != null) {
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
        TreeViewGroupRule groupedNodesRule = legendTreeViewGroupRuleProvider.provide(
                metricNode.getId(),
                legendGroups,
                legendFormat);

        // tree with metrics distributed by groups
        return groupedNodesRule.filter(null, metricNodeList);
    }

    @Override
    public Map<MetricNameDto, List<PlotSingleDto>> getPlotDataByMetricNameDto(Set<MetricNameDto> metricNames) throws IllegalArgumentException {

        if (metricNames.isEmpty()) {
            return Collections.emptyMap();
        }
        long temp = System.currentTimeMillis();

        final Multimap<PlotsDbMetricDataFetcher, MetricNameDto> fetchMap = ArrayListMultimap.create();
        for (MetricNameDto metricNameDto : metricNames) {
            switch (metricNameDto.getOrigin()) {
                case METRIC: // true way
                    fetchMap.put(customMetricPlotFetcher, metricNameDto);
                    break;
                case TEST_GROUP_METRIC: // true way
                    fetchMap.put(customTestGroupMetricPlotFetcher, metricNameDto);
                    break;
                case MONITORING: // Deprecated
                    fetchMap.put(monitoringMetricPlotFetcher, metricNameDto);
                    break;
                case LATENCY:  // Deprecated
                    fetchMap.put(latencyMetricPlotFetcher, metricNameDto);
                    break;
                case LATENCY_PERCENTILE: // Deprecated
                    fetchMap.put(timeLatencyPercentileMetricPlotFetcher, metricNameDto);
                    break;
                case THROUGHPUT: // Deprecated
                    fetchMap.put(throughputMetricPlotFetcher, metricNameDto);
                    break;
                case SESSION_SCOPE_MONITORING: // Deprecated
                    fetchMap.put(sessionScopeMonitoringMetricPlotFetcher, metricNameDto);
                    break;
                case SESSION_SCOPE_TG: // true way
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

        List<Future<Set<Pair<MetricNameDto, List<PlotSingleDto>>>>> futures = new ArrayList<Future<Set<Pair<MetricNameDto, List<PlotSingleDto>>>>>();
        for (final PlotsDbMetricDataFetcher fetcher : fetchMap.keySet()) {
            futures.add(threadPool.submit(new Callable<Set<Pair<MetricNameDto, List<PlotSingleDto>>>>() {
                @Override
                public Set<Pair<MetricNameDto, List<PlotSingleDto>>> call() throws Exception {
                    return fetcher.getResult(new ArrayList<MetricNameDto>(fetchMap.get(fetcher)));
                }
            }));
        }

        Set<Pair<MetricNameDto, List<PlotSingleDto>>> resultSet = new HashSet<Pair<MetricNameDto, List<PlotSingleDto>>>();
        try {
            for (Future<Set<Pair<MetricNameDto, List<PlotSingleDto>>>> future : futures) {
                resultSet.addAll(future.get());
            }
        } catch (Throwable th) {
            log.error("Exception while plots retrieving", th);
            throw new RuntimeException("Exception while plots retrieving", th);
        }

        Map<MetricNameDto, List<PlotSingleDto>> result = new HashMap<MetricNameDto, List<PlotSingleDto>>();
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

            List<TaskDataDto> taskList = fetchTaskDatas(sessionIds,sessionMatchingSetup);

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
        Multimap<MetricNode, SummarySingleDto> tempMap =  ArrayListMultimap.create();
        for (SummarySingleDto singleSumDto : allMetricDto) {
            for (MetricNode metricNode : metricNodes) {
                if (metricNode.getMetricNameDtoList().contains(singleSumDto.getMetricName())) {
                    tempMap.put(metricNode, singleSumDto);
                    break;
                }
            }
        }

        // generate result map
        Map<MetricNode, SummaryIntegratedDto> resultMap = new HashMap<MetricNode, SummaryIntegratedDto>(tempMap.size());
        for (MetricNode metricNode: tempMap.keySet()) {

            List<SummarySingleDto> sumCollection = new ArrayList<SummarySingleDto>(tempMap.get(metricNode));
            List<PlotSingleDto> plotSingleDtos = new ArrayList<PlotSingleDto>(sumCollection.size());
            MetricRankingProvider.sortMetrics(sumCollection);

            for (SummarySingleDto sumSingleDto : sumCollection) {
                plotSingleDtos.add(DataProcessingUtil.generatePlotSingleDto(sumSingleDto));
            }

            SummaryIntegratedDto summaryDto = new SummaryIntegratedDto();
            summaryDto.setSummarySingleDtoList(sumCollection);
            summaryDto.setPlotIntegratedDto(createPlotIntegratedDto(metricNode, plotSingleDtos, "Sessions"));
            resultMap.put(metricNode, summaryDto);
        }

        log.debug("Total time of Summary Data retrieving for " + metricNodes.size() + " metric nodes : " + (System.currentTimeMillis() - temp));

        return resultMap;
    }


    @Override
    public Map<MetricNameDto, SummarySingleDto> getSummaryByMetricNameDto(Set<MetricNameDto> metricNames, boolean isEnableDecisionsPerMetricFetching) {

        long temp = System.currentTimeMillis();

        final Multimap<MetricDataFetcher<SummarySingleDto>, MetricNameDto> fetchMap = ArrayListMultimap.create();

        for (MetricNameDto metricName : metricNames){
            switch (metricName.getOrigin()) {
                case STANDARD_METRICS: // Deprecated
                    fetchMap.put(standardMetricSummaryFetcher, metricName);
                    break;
                case DURATION: // Deprecated
                    fetchMap.put(durationMetricSummaryFetcher, metricName);
                    break;
                case LATENCY_PERCENTILE: // Deprecated
                    fetchMap.put(latencyMetricDataFetcher, metricName);
                    break;
                case METRIC: // true way
                    fetchMap.put(customMetricSummaryFetcher, metricName);
                    break;
                case TEST_GROUP_METRIC: // true way
                    fetchMap.put(customTestGroupMetricSummaryFetcher, metricName);
                    break;
                case VALIDATOR:
                    fetchMap.put(validatorSummaryFetcher, metricName);
                    break;
                case SESSION_SCOPE_TG:
                    break;
                case SESSION_SCOPE_MONITORING:
                    break;
                default:  // if anything else
                    log.error("MetricNameDto with origin : {} appears in metric name list for summary retrieving ({})", metricName.getOrigin(), metricName);
                    throw new RuntimeException("Unable to get summary data for metric " + metricName.getMetricName() +
                            " with origin: " + metricName.getOrigin());
            }
        }

        List<Future<Set<SummarySingleDto>>> futures = new ArrayList<Future<Set<SummarySingleDto>>>();
        for (final MetricDataFetcher<SummarySingleDto> fetcher : fetchMap.keySet()) {
            futures.add(threadPool.submit(new Callable<Set<SummarySingleDto>>() {
                @Override
                public Set<SummarySingleDto> call() throws Exception {
                    return fetcher.getResult(new ArrayList<MetricNameDto>(fetchMap.get(fetcher)));
                }
            }));
        }
        Set<SummarySingleDto> result = new HashSet<SummarySingleDto>(metricNames.size());
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
            Map<MetricNameDto,Map<String,Decision>> metricDecisions = getDecisionsPerMetric(metricNames);
            if (!metricDecisions.isEmpty()) {
                for (SummarySingleDto metricDto : result) {
                    MetricNameDto metricName = metricDto.getMetricName();

                    if (metricDecisions.containsKey(metricName)) {
                        Map<String,Decision> decisionPerSession = metricDecisions.get(metricName);
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

        Map<MetricNameDto, SummarySingleDto> resultMap = new HashMap<MetricNameDto, SummarySingleDto>(result.size());
        for (SummarySingleDto ssd : result) {
            resultMap.put(ssd.getMetricName(), ssd);
        }
        log.debug("{} ms spent for fetching summary data for {} metrics", System.currentTimeMillis() - temp, metricNames.size());

        return resultMap;
    }

    @Override
    public Map<String,Set<String>> getDefaultMonitoringParameters() {
        return defaultMonitoringParams;
    }

    private Map<String,Set<String>> getDefaultMonitoringParametersMap(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        // relation of old monitoring names from Groupkey (were used in hyperlinks) to
        // new monitoring metric ids from DefaultMonitoringParameters
        // necessary to process old hyperlinks by new client
        Map<String,Set<String>> result = new HashMap<String, Set<String>>();

        for(Map.Entry<GroupKey,DefaultMonitoringParameters[]> groupKeyEntry : monitoringPlotGroups.entrySet()) {
            String key = groupKeyEntry.getKey().getUpperName();
            if (!result.containsKey(key)) {
                result.put(key,new HashSet<String>());
            }

            for (DefaultMonitoringParameters defaultMonitoringParameters : groupKeyEntry.getValue()) {
                result.get(key).add(defaultMonitoringParameters.getId());
            }
        }

        return result;
    }

    /**
     * Fetch all Monitoring tasks ids for all tests
     * @param sessionIds all sessions
     * @param taskDataDtos all tests
     * @return list of monitoring task ids
     */
    @Deprecated
    private Map<TaskDataDto, List<BigInteger>> getMonitoringIds(Set<String> sessionIds, List<TaskDataDto> taskDataDtos) {

        Set<Long> taskIds = new HashSet<Long>();
        for (TaskDataDto tdd : taskDataDtos) {
            taskIds.addAll(tdd.getIds());
        }


        long temp = System.currentTimeMillis();
        List<Object[]> monitoringTaskIds = entityManager.createNativeQuery(
                "select test.id, mysome.taskDataId from " +
                        "  ( " +
                        "    select test.id, test.sessionId, test.taskId from TaskData as test where test.sessionId in (:sessionIds)" +
                        "  ) as test join " +
                        "  (" +
                        "    select mysome.parentId, pm.monitoringId, mysome.taskDataId, pm.sessionId from" +
                        "      (" +
                        "        select pm.monitoringId, pm.sessionId, pm.parentId from PerformedMonitoring as pm where pm.sessionId in (:sessionIds) " +
                        "      ) as pm join " +
                        "      (" +
                        "        select td2.sessionId, td2.id as taskDataId, wd.parentId from" +
                        "          ( " +
                        "            select wd.parentId, wd.sessionId, wd.taskId from WorkloadData as wd where wd.sessionId in (:sessionIds)" +
                        "          ) as wd join " +
                        "            TaskData as td2" +
                        "            on td2.id in (:taskIds)" +
                        "            and wd.sessionId = td2.sessionId" +
                        "            and wd.taskId=td2.taskId" +
                        "      ) as mysome on pm.sessionId = mysome.sessionId and pm.parentId=mysome.parentId" +
                        "  ) as mysome on test.sessionId = mysome.sessionId and test.taskId=mysome.monitoringId"
        )
                .setParameter("taskIds", taskIds)
                .setParameter("sessionIds", sessionIds)
                .getResultList();
        log.debug("db call to fetch all monitoring tasks ids in {} ms (size : {})", System.currentTimeMillis() - temp, monitoringTaskIds.size());

        Map<TaskDataDto, List<BigInteger>> result = new HashMap<TaskDataDto, List<BigInteger>>();
        if (monitoringTaskIds.isEmpty()) {
            return Collections.emptyMap();
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

    private Map<TaskDataDto, List<MetricNode>> getTestMetricsMap(final List<TaskDataDto> tddos) {
        Long time = System.currentTimeMillis();
        List<MetricNameDto> list = new ArrayList<MetricNameDto>();

        try {

            Future<Set<MetricNameDto>> standardMetricNamesFuture = threadPool.submit(
                    new Callable<Set<MetricNameDto>>(){

                        @Override
                        public Set<MetricNameDto> call() throws Exception {
                            return standardMetricNameProvider.getMetricNames(tddos);
                        }
                    }
            );

            Future<Set<MetricNameDto>> latencyMetricNamesFuture = threadPool.submit(
                    new Callable<Set<MetricNameDto>>(){

                        @Override
                        public Set<MetricNameDto> call() throws Exception {
                            return latencyMetricNameProvider.getMetricNames(tddos);
                        }
                    }
            );

            Future<Set<MetricNameDto>> customMetricNamesFuture = threadPool.submit(
                    new Callable<Set<MetricNameDto>>(){

                        @Override
                        public Set<MetricNameDto> call() throws Exception {
                            return customMetricNameProvider.getMetricNames(tddos);
                        }
                    }
            );

            Future<Set<MetricNameDto>> validatorsNamesFuture = threadPool.submit(
                    new Callable<Set<MetricNameDto>>(){

                        @Override
                        public Set<MetricNameDto> call() throws Exception {
                            return validatorNamesProvider.getMetricNames(tddos);
                        }
                    }
            );

            list.addAll(standardMetricNamesFuture.get());
            list.addAll(latencyMetricNamesFuture.get());
            list.addAll(customMetricNamesFuture.get());
            list.addAll(validatorsNamesFuture.get());
        } catch (Exception e) {
            log.error("Exception occurs while fetching MetricNames for tests : ", e);
            throw new RuntimeException(e);
        }

        log.debug("Search metric names for tasks: {}", tddos);
        log.info("For {} tasks were found {} metrics names for {} ms", new Object[]{tddos.size(), list.size(), System.currentTimeMillis() - time});

        Map<TaskDataDto, List<MetricNode>> result = new HashMap<TaskDataDto, List<MetricNode>>();

        for (MetricNameDto mnd : list) {
            if ((mnd.getMetricName() == null) || (mnd.getMetricName().equals(""))) {
                log.warn("Metric with undefined id detected. It will be ignored. Details: " + mnd);
            }
            else {
                for (TaskDataDto tdd : tddos) {
                    if (tdd.getIds().containsAll(mnd.getTaskIds())) {
                        if (!result.containsKey(tdd)) {
                            result.put(tdd, new ArrayList<MetricNode>());
                        }
                        MetricNode mn = new MetricNode();
                        String id = NameTokens.SUMMARY_PREFIX + tdd.hashCode() + mnd.getMetricName();
                        mn.init(id, mnd.getMetricDisplayName(), Arrays.asList(mnd));
                        result.get(tdd).add(mn);
                        break;
                    }
                }
            }
        }

        return result;
    }

    private Map<TaskDataDto, List<PlotNode>> getTestPlotsMap(Set<String> sessionIds, List<TaskDataDto> taskList) {

        Map<TaskDataDto, List<PlotNode>> result = new HashMap<TaskDataDto, List<PlotNode>>();
        List<MetricNameDto> metricNameDtoList = new ArrayList<MetricNameDto>();
        try {
            for (TaskDataDto taskDataDto : getTasksWithWorkloadStatistics(taskList)) {
                for (Map.Entry<GroupKey, DefaultWorkloadParameters[]> monitoringPlot : workloadPlotGroups.entrySet()) {
                    MetricNameDto metricNameDto = new MetricNameDto(taskDataDto, monitoringPlot.getKey().getUpperName());
                    metricNameDto.setOrigin(monitoringPlot.getValue()[0].getOrigin());
                    metricNameDtoList.add(metricNameDto);
                }
            }

            metricNameDtoList.addAll(customMetricPlotNameProvider.getPlotNames(taskList));

            log.debug("For sessions {} are available these plots: {}", sessionIds, metricNameDtoList);

            for (MetricNameDto pnd : metricNameDtoList) {
                if ((pnd.getMetricName() == null) || (pnd.getMetricName().equals(""))) {
                    log.warn("Metric with undefined id detected. It will be ignored. Details: " + pnd);
                }
                else {
                    for (TaskDataDto tdd : taskList) {
                        if (!result.containsKey(tdd)) {
                            result.put(tdd, new ArrayList<PlotNode>());
                        }
                        if (tdd.getIds().containsAll(pnd.getTaskIds())) {

                            PlotNode pn = new PlotNode();
                            String id = NameTokens.METRICS_PREFIX + tdd.hashCode() + pnd.getMetricName() + pnd.getOrigin();
                            pn.init(id, pnd.getMetricDisplayName(), Arrays.asList(pnd));
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

    @Deprecated
    private Map<TaskDataDto, List<PlotNode>> getMonitoringPlotNames(Set<Map.Entry<GroupKey, DefaultMonitoringParameters[]>> monitoringParameters, Map<TaskDataDto, List<BigInteger>> monitoringIdsMap) {
        Set<BigInteger> monitoringIds = new HashSet<BigInteger>();
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

        Map<TaskDataDto, Set<PlotNode>> resultMap = new HashMap<TaskDataDto, Set<PlotNode>>();

        Set<TaskDataDto> taskSet = monitoringIdsMap.keySet();

        for (Object[] objects : agentIdentifierObjects) {
            BigInteger testId = (BigInteger)objects[2];
            for (TaskDataDto tdd : taskSet) {
                if (!resultMap.containsKey(tdd)) {
                    resultMap.put(tdd, new HashSet<PlotNode>());
                }
                if (monitoringIdsMap.get(tdd).contains(testId)) {
                    String description = (String) objects[3];
                    String monitoringId = null;     // Id of particular metric
                    for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> entry : monitoringParameters) {
                        for (DefaultMonitoringParameters dmp : entry.getValue()) {
                            if (dmp.getDescription().equals(description)) {
                                monitoringId = dmp.getId();
                            }
                        }
                    }

                    if (monitoringId == null) {
                        log.warn("Could not find monitoring key for description: '{}' and monitoring task id: '{}'", description, objects[2]);
                        break;
                    }

                    String agentId = objects[0] == null ? objects[1].toString() : objects[0].toString();

                    PlotNode plotNode = new PlotNode();
                    String id = NameTokens.METRICS_PREFIX + tdd.hashCode() + "_" + monitoringId + "_" + agentId;
                    MetricNameDto metricNameDto = new MetricNameDto(tdd, MonitoringIdUtils.getMonitoringMetricId(monitoringId, agentId));
                    metricNameDto.setOrigin(MetricNameDto.Origin.MONITORING);
                    metricNameDto.setMetricDisplayName(description);
                    plotNode.init(id, id, Arrays.asList(metricNameDto));

                    resultMap.get(tdd).add(plotNode);

                    // we should create new PlotNode with new Id and MetricNameDto witch has origin SESSION_SCOPE_MONITORING for Session Scope

                    PlotNode ssPlotNode = new PlotNode();
                    id = NameTokens.SESSION_SCOPE_PREFIX + tdd.hashCode() + "_" + monitoringId + "_" + agentId;
                    MetricNameDto metricNameDtoSS = new MetricNameDto(tdd, MonitoringIdUtils.getMonitoringMetricId(monitoringId, agentId));
                    metricNameDtoSS.setOrigin(MetricNameDto.Origin.SESSION_SCOPE_MONITORING);
                    metricNameDtoSS.setMetricDisplayName(description);
                    ssPlotNode.init(id, id, Arrays.asList(metricNameDtoSS));

                    resultMap.get(tdd).add(ssPlotNode);
                    break;
                }
            }
        }

        // set to list
        Map<TaskDataDto, List<PlotNode>> newResultMap = new HashMap<TaskDataDto, List<PlotNode>>();
        for (TaskDataDto taskDataDto: resultMap.keySet()) {
            newResultMap.put(taskDataDto,new ArrayList<PlotNode>(resultMap.get(taskDataDto)));
        }

        return newResultMap;
    }

    @Deprecated
    private Set<TaskDataDto> getTasksWithWorkloadStatistics(List<TaskDataDto> tests) {

        List<Long> testsIds = new ArrayList<Long>();
        for (TaskDataDto tdd : tests) {
            testsIds.addAll(tdd.getIds());
        }

        long temp = System.currentTimeMillis();
        List<Long> availableTestIds  = (List<Long>)entityManager.createQuery("select distinct tis.taskData.id from TimeInvocationStatistics as tis where tis.taskData.id in (:testsIds)")
                .setParameter("testsIds", testsIds)
                .getResultList();
        log.debug("db call to check if WorkloadStatisticsAvailable in {} ms (size: {})", System.currentTimeMillis() - temp, availableTestIds.size());

        Set<TaskDataDto> result = new HashSet<TaskDataDto>();
        for (Long availableId : availableTestIds) {
            for (TaskDataDto taskDataDto : tests) {
                if (taskDataDto.getIds().contains(availableId)) {
                    result.add(taskDataDto);
                }
            }
        }

        return result;
    }

    @Override
    public List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds, SessionMatchingSetup sessionMatchingSetup) {

        long timestamp = System.currentTimeMillis();

        int havingCount = 0;
        if (sessionMatchingSetup.isShowOnlyMatchedTests()) {
            havingCount = sessionIds.size();
        }

        List<Object[]> list = entityManager.createNativeQuery
                (
                        "select taskData.id, commonTests.name, commonTests.description, taskData.taskId , commonTests.clock, commonTests.clockValue, commonTests.termination, taskData.sessionId" +
                                " from " +
                                "( " +
                                "    select test.name, test.description, test.version, test.sessionId, test.taskId, test.clock, test.clockValue, test.termination from " +
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
        HashMap<String, TaskDataDto> map = new HashMap<String, TaskDataDto>(list.size());
        HashMap<String, Integer> mapIds = new HashMap<String, Integer>(list.size());
        int i = 0;
        for (Object[] testData : list){
            BigInteger id = (BigInteger)testData[0];
            String name = (String) testData[1];
            String description = (String) testData[2];
            String taskId = (String)testData[3];

            // we need clock , and termination here is tool of matching test.
            String clock = (String)testData[4];
            Integer clockValue = (Integer)testData[5];
            String termination = (String) testData[6];

            String sessionId = (String) testData[7];

            int taskIdInt = Integer.parseInt(taskId.substring(5));

            // key - defines how to match tests when several sessions are selected
            StringBuilder key = new StringBuilder(255);
            // uniqueIdParams - is used to generate unique Ids for nodes in control tree depending on session matching strategy
            List<String> uniqueIdParams = new ArrayList<String>();

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
            }
            else {
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
            if (map.containsKey(key.toString())){
                map.get(key.toString()).getIdToSessionId().put(id.longValue(),sessionId);

                Integer oldValue = mapIds.get(key.toString());
                mapIds.put(key.toString(), (oldValue==null ? 0 : oldValue)+taskIdInt);
            }else{
                TaskDataDto taskDataDto = new TaskDataDto(id.longValue(), sessionId, name, description);
                // generate unique id to make difference between tests with different matching parameters.
                taskDataDto.setUniqueId(CommonUtils.generateUniqueId(uniqueIdParams));

                map.put(key.toString(), taskDataDto);
                mapIds.put(key.toString(), taskIdInt);
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
            priorityQueue.add(new Object[]{mapIds.get(key), taskDataDto});
        }

        ArrayList<TaskDataDto> result = new ArrayList<TaskDataDto>(priorityQueue.size());
        while (!priorityQueue.isEmpty()){
            result.add((TaskDataDto)priorityQueue.poll()[1]);
        }

        log.info("For sessions {} was loaded {} tasks for {} ms", new Object[]{sessionIds, result.size(), System.currentTimeMillis() - timestamp});
        return result;
    }

    public boolean checkIfUserCommentStorageAvailable() {
        try {
            // even if table is empty we can set user comments
            entityManager.createQuery(
                    "select count(sm) from SessionMetaDataEntity sm")
                    .getSingleResult();
            return true;
        } catch (Exception e) {
            log.warn("Could not access SessionMetaDataTable", e);
        }
        return false;
    }

    public boolean checkIfTagsStorageAvailable() {
        try {
            entityManager.createQuery(
                        "select 1 from TagEntity")
                        .getSingleResult();
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

            Future<Map<TaskDataDto, List<PlotNode>>> monitoringNewPlotsMapFuture = threadPool.submit(
                    () -> getMonitoringPlots(sessionIds, taskList));


            //a first list element is a map with test nodes
            //a second is a map with nodes for session scope
            List<Map<TaskDataDto, List<PlotNode>>> maps = separateTestAndSessionScope(metricsPlotsMapFuture.get());
            List<Map<TaskDataDto, List<PlotNode>>> monitoringMaps = separateTestAndSessionScope(monitoringNewPlotsMapFuture.get());

            Map<TaskDataDto, List<PlotNode>> map = maps.get(0);
            Map<TaskDataDto, List<PlotNode>> mapSS = maps.get(1);

            Map<TaskDataDto, List<PlotNode>> monitoringMap = monitoringMaps.get(0);
            Map<TaskDataDto, List<PlotNode>> monitoringMapSS = monitoringMaps.get(1);

            // get agent names
            Set<PlotNode> plotNodeList = new HashSet<>();
            if (!monitoringMap.isEmpty()) {
                for (TaskDataDto taskDataDto : monitoringMap.keySet()) {
                    plotNodeList.addAll(monitoringMap.get(taskDataDto));
                }
            }
            for (TaskDataDto taskDataDto : map.keySet()) {
                plotNodeList.addAll(map.get(taskDataDto));
            }
            Map<String, Set<String>> agentNames = getAgentNamesForMonitoringParameters(plotNodeList);


            //get nodes for session scope and Session Scope Node
            Set<PlotNode> ssPlotNodes;
            if (sessionIds.size() == 1) {
                String sessionId = sessionIds.iterator().next();
                ssPlotNodes = getSessionScopeNodes(mapSS, sessionId);
                if (!monitoringMap.isEmpty()) {
                    ssPlotNodes.addAll(getSessionScopeNodes(monitoringMapSS, sessionId));
                }

                if (ssPlotNodes.size() > 0) {
                    String rootIdSS = NameTokens.SESSION_SCOPE_PLOTS;
                    sessionScopeNode = buildTreeAccordingToRules(rootIdSS, agentNames, null, false, new ArrayList<PlotNode>(ssPlotNodes));
                }
            }

            // get tree
            for (TaskDataDto tdd : taskList) {
                List<PlotNode> metricNodeList = new ArrayList<PlotNode>();
                if (map.containsKey(tdd)) {
                    metricNodeList.addAll(map.get(tdd));
                }
                if (monitoringMap.containsKey(tdd)) {
                    metricNodeList.addAll(monitoringMap.get(tdd));
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

        List<TestNode> taskDataDtoList = new ArrayList<TestNode>();

        Map<TaskDataDto, List<MetricNode>> map = getTestMetricsMap(tasks);

        // get agent names
        Set<MetricNode> metricNodeListForAgentNames = new HashSet<MetricNode>();
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

    @Deprecated
    private Map<TaskDataDto, List<PlotNode>> getMonitoringPlots(Set<String> sessionIds, List<TaskDataDto> taskDataDtos) {
        try {
            Map<TaskDataDto, List<BigInteger>>  monitoringIds = getMonitoringIds(sessionIds, taskDataDtos);
            if (monitoringIds.isEmpty()) {
                return Collections.EMPTY_MAP;
            }

            Map<TaskDataDto, List<PlotNode>> result = getMonitoringPlotNames(monitoringPlotGroups.entrySet(), monitoringIds);

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

    public class SummaryNodeFetcherThread implements Callable<SummaryNode> {
        private List<TaskDataDto> taskList;
        public SummaryNodeFetcherThread(List<TaskDataDto> taskList) {
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

    public class DetailsNodeFetcherThread implements Callable<DetailsNode> {
        private Set<String> sessionIds;
        private List<TaskDataDto> taskList;
        public DetailsNodeFetcherThread(Set<String> sessionIds, List<TaskDataDto> taskList) {
            this.sessionIds = sessionIds;
            this.taskList = taskList;
        }

        public DetailsNode call() {
           return getDetailsNode(sessionIds,taskList);
        }
    }

    private Map<String,Set<String>> getAgentNamesForMonitoringParameters(Set<? extends MetricNode> nodeList) {
        Map<String,Set<String>> agentNames = new HashMap<String, Set<String>>();

        for (MetricNode node : nodeList) {
            for (MetricNameDto metricNameDto : node.getMetricNameDtoList()) {
                // old monitoring or new monitoring as metrics
                if ((metricNameDto.getOrigin().equals(MetricNameDto.Origin.MONITORING)) ||
                        (metricNameDto.getOrigin().equals(MetricNameDto.Origin.TEST_GROUP_METRIC))) {

                    // if looks like monitoring parameter
                    MonitoringIdUtils.MonitoringId monitoringId = MonitoringIdUtils.splitMonitoringMetricId(metricNameDto.getMetricName());
                    if (monitoringId != null) {
                        // if available in default monitoring parameters
                        for (String key : defaultMonitoringParams.keySet()) {
                            if (defaultMonitoringParams.get(key).contains(monitoringId.getMonitoringName())) {
                                if (!agentNames.containsKey(key)) {
                                    agentNames.put(key,new HashSet<String>());
                                }
                                agentNames.get(key).add(monitoringId.getAgentName());
                            }
                        }
                    }
                }
            }
        }

        return agentNames;
    }

    private Set<Double> getPercentileValuesFromIds(List<? extends MetricNode> nodeList) {
        Set<Double> percentiles = new HashSet<Double>();
        for (MetricNode mn : nodeList) {
            for (MetricNameDto mnd : mn.getMetricNameDtoList()) {
                if (mnd.getMetricName().matches(StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX)) {
                    Double percentileKey = StandardMetricsNamesUtil.parseLatencyPercentileKey(mnd.getMetricName());
                    percentiles.add(percentileKey);
                }
            }
        }

        return percentiles;
    }

    /**
     * Build Tree of nodes according to rules
     * @param rootId id of root Node
     * @param agentNames map of monitoring parameter -> agent names (null if not required)
     * @param percentiles list of percentiles (null if not required)
     * @param forSummary tells what node we are building now @n
     *                   true for Summary&Trends tab view - we show standard metrics as separate metric nodes @n
     *                   false for Metrics tab view - we group metrics (metricnameDto) to single metric node (example Latency, LatencyStdDev -> Latency) @n
     * @param metricNodeList list of nodes to build tree
     * @param <M> Node type that extends MetricNode
     * @return Tree of nodes
     */
    private <M extends MetricNode> MetricGroupNode<M> buildTreeAccordingToRules(
            String rootId,
            Map<String, Set<String>> agentNames,
            Set<Double> percentiles,
            boolean forSummary,
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

        // rules to create test tree view
        TreeViewGroupRule groupedNodesRule = treeViewGroupRuleProvider.provide(rootId, rootId);
        // tree with metrics distributed by groups
        MetricGroupNode<M> testNodeBase = groupedNodesRule.filter(null, metricNodeList);

        return testNodeBase;
    }

    @Override
    public Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfoByTaskDataDto(Collection<TaskDataDto> taskDataDtos) throws RuntimeException {

        if (taskDataDtos.isEmpty()) {
            return Collections.emptyMap();
        }

        long temp = System.currentTimeMillis();

        Set<Long> taskDataIds = new HashSet<Long>();
        for (TaskDataDto taskDataDto : taskDataDtos) {
            taskDataIds.addAll(taskDataDto.getIds());
        }

        Map<Long,Map<String,TestInfoDto>> preliminaryResult = getTestInfoByTaskIds(taskDataIds);

        Map<TaskDataDto, Map<String, TestInfoDto>> resultMap = new HashMap<TaskDataDto, Map<String, TestInfoDto>>(taskDataDtos.size());

        for (Map.Entry<Long,Map<String,TestInfoDto>> entry : preliminaryResult.entrySet()) {
            for (TaskDataDto td : taskDataDtos) {
                if (td.getIds().contains(entry.getKey())) {
                    if (!resultMap.containsKey(td)) {
                        resultMap.put(td, new HashMap<String, TestInfoDto>());
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
    public SessionInfoProvider getSessionInfoService(){
        return sessionInfoServiceImpl;
    }

    @Override
    public List<NodeInfoPerSessionDto> getNodeInfo(Set<String> sessionIds) {

        Long time = System.currentTimeMillis();
        List<NodeInfoPerSessionDto> nodeInfoPerSessionDtoList = new ArrayList<NodeInfoPerSessionDto>();

        try {
            List<NodeInfoEntity> nodeInfoEntityList = (List<NodeInfoEntity>)
                    entityManager.createQuery("select nie from NodeInfoEntity as nie where nie.sessionId in (:sessionIds)").
                            setParameter("sessionIds", new ArrayList<String>(sessionIds)).
                            getResultList();

            Map<String,List<NodeInfoDto>> sessions = new HashMap<String, List<NodeInfoDto>>();

            for (NodeInfoEntity nodeInfoEntity : nodeInfoEntityList) {
                Map<String,String> parameters = new HashMap<String, String>();

                parameters.put("CPU model",nodeInfoEntity.getCpuModel());
                parameters.put("CPU frequency, MHz",String.valueOf(nodeInfoEntity.getCpuMHz()));
                parameters.put("CPU number of cores",String.valueOf(nodeInfoEntity.getCpuTotalCores()));
                parameters.put("CPU number of sockets",String.valueOf(nodeInfoEntity.getCpuTotalSockets()));
                parameters.put("Jagger Java version", nodeInfoEntity.getJaggerJavaVersion());
                parameters.put("OS name", nodeInfoEntity.getOsName());
                parameters.put("OS version", nodeInfoEntity.getOsVersion());
                parameters.put("System RAM, MB",String.valueOf(nodeInfoEntity.getSystemRAM()));

                List<NodePropertyEntity> nodePropertyEntityList = nodeInfoEntity.getProperties();
                for (NodePropertyEntity nodePropertyEntity : nodePropertyEntityList) {
                    parameters.put("Property '" + nodePropertyEntity.getName() + "'",nodePropertyEntity.getValue());
                }
                NodeInfoDto nodeInfoDto = new NodeInfoDto(nodeInfoEntity.getNodeId(),parameters);

                String sessionId = nodeInfoEntity.getSessionId();
                if (sessions.containsKey(sessionId)) {
                    sessions.get(sessionId).add(nodeInfoDto);
                }
                else {
                    List <NodeInfoDto> node = new ArrayList<NodeInfoDto>();
                    node.add(nodeInfoDto);
                    sessions.put(sessionId, node);
                }
            }

            for (Map.Entry<String,List<NodeInfoDto>> session : sessions.entrySet()) {
                nodeInfoPerSessionDtoList.add(new NodeInfoPerSessionDto(session.getKey(),session.getValue()));
            }

            log.info("For session ids " + sessionIds + " were found node info values in " + (System.currentTimeMillis() - time) + " ms");
        }
        catch (NoResultException ex) {
            log.info("No node info data was found for session id " + sessionIds, ex);
        }
        catch (PersistenceException ex) {
            log.info("No node info data was found for session id " + sessionIds, ex);
        }
        catch (Exception ex) {
            log.error("Error occurred during loading node info data for session ids " + sessionIds, ex);
            throw new RuntimeException("Error occurred during loading node info data for session ids " + sessionIds,ex);
        }

        return nodeInfoPerSessionDtoList;
    }

    @Override
    public List<String> getSessionIdsByTaskIds(Set<Long> taskIds) {
        return fetchUtil.getSessionIdsByTaskIds(taskIds);
    }

    @Override
    public Map<Long, Set<Long>> getTestGroupIdsByTestIds(Set<Long> taskIds) {
        Map<Long,Set<Long>> result = new HashMap<Long, Set<Long>>();

        Multimap<Long,Long> tempResult = fetchUtil.getTestGroupIdsByTestIds(taskIds);

        for (Long testGroupId : tempResult.keySet()) {
            result.put(testGroupId,new HashSet<Long>(tempResult.get(testGroupId)));
        }

        return result;
    }

    @Override
    public Set<TaskDecisionDto> getDecisionsPerTask(Set<Long> taskIds) {

        if (taskIds.isEmpty()) {
            return Collections.emptySet();
        }

        Long time = System.currentTimeMillis();
        Set<TaskDecisionDto> taskDecisionDtoSet = new HashSet<TaskDecisionDto>();

        try {
            List<DecisionPerTaskEntity> decisionPerTaskEntityList = (List<DecisionPerTaskEntity>)
                    entityManager.createQuery("select dpt from DecisionPerTaskEntity as dpt where dpt.taskData.id in (:taskIds)").
                            setParameter("taskIds", taskIds).
                            getResultList();

            for (DecisionPerTaskEntity decisionPerTaskEntity : decisionPerTaskEntityList) {
                TaskDecisionDto taskDecisionDto = new TaskDecisionDto();
                taskDecisionDto.setId(decisionPerTaskEntity.getTaskData().getId());
                taskDecisionDto.setName(decisionPerTaskEntity.getTaskData().getTaskName());
                taskDecisionDto.setDecision(Decision.valueOf(decisionPerTaskEntity.getDecision()));

                taskDecisionDtoSet.add(taskDecisionDto);
            }

            log.debug("For task ids " + taskIds + " were found decisions in " + (System.currentTimeMillis() - time) + " ms");
        }
        catch (NoResultException ex) {
            log.debug("No decisions were found for task ids " + taskIds, ex);
            return Collections.emptySet();
        }
        catch (PersistenceException ex) {
            log.debug("No decisions were found for task ids " + taskIds, ex);
            return Collections.emptySet();
        }
        catch (Exception ex) {
            log.error("Error occurred during loading decisions for task ids " + taskIds, ex);
            throw new RuntimeException("Error occurred during loading decisions for task ids " + taskIds,ex);
        }

        return taskDecisionDtoSet;
    }

    @Override
    public Map<MetricNameDto,Map<String,Decision>> getDecisionsPerMetric(Set<MetricNameDto> metricNames) {

        if (metricNames.isEmpty()) {
            return Collections.emptyMap();
        }

        Long time = System.currentTimeMillis();

        Map<MetricNameDto,Map<String,Decision>> result = new HashMap<MetricNameDto, Map<String, Decision>>();

        Set<String> metricIds = new HashSet<String>();
        Set<Long> taskIds = new HashSet<Long>();
        Set<Long> taskIdsWhereParentIdIsRequired = new HashSet<Long>();

        for (MetricNameDto metricName : metricNames) {
            metricIds.add(metricName.getMetricName());
            taskIds.addAll(metricName.getTaskIds());
            if (metricName.getOrigin().equals(MetricNameDto.Origin.TEST_GROUP_METRIC)) {
                taskIdsWhereParentIdIsRequired.addAll(metricName.getTaskIds());
            }
        }

        // add test group task ids when necessary
        Multimap<Long,Long> testGroupPerTest = null;
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
                Set<Long> taskIdsForEntity = new HashSet<Long>();
                Long taskId = decisionPerMetricEntity.getMetricDescriptionEntity().getTaskData().getId();
                String sessionId = decisionPerMetricEntity.getMetricDescriptionEntity().getTaskData().getSessionId();
                String metricId = decisionPerMetricEntity.getMetricDescriptionEntity().getMetricId();

                if ((testGroupPerTest != null) && (testGroupPerTest.containsKey(taskId))) {
                    // this is test group task id => we will use tests from this test group
                    for (Long testTaskId : testGroupPerTest.get(taskId)) {
                        taskIdsForEntity.add(testTaskId);
                    }
                }
                else {
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
                        throw new IllegalArgumentException("Could not find appropriate MetricDto with taskId: " + testTaskId + ", metricId: " + metricId);
                    }

                    if (!result.containsKey(metricNameDto)) {
                        result.put(metricNameDto,new HashMap<String, Decision>());
                    }
                    result.get(metricNameDto).put(sessionId, Decision.valueOf(decisionPerMetricEntity.getDecision()));
                }
            }

            log.debug("For metrics " + metricNames + " were found decisions in " + (System.currentTimeMillis() - time) + " ms");
        }
        catch (NoResultException ex) {
            log.debug("No decisions were found for metrics " + metricNames, ex);
            return Collections.emptyMap();
        }
        catch (PersistenceException ex) {
            log.debug("No decisions were found for metrics " + metricNames, ex);
            return Collections.emptyMap();
        }
        catch (Exception ex) {
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

        Map<String,Decision> result = new HashMap<String, Decision>();

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
        }
        catch (NoResultException ex) {
            log.debug("No decisions were found for session ids " + sessionIds, ex);
            return Collections.emptyMap();
        }
        catch (PersistenceException ex) {
            log.debug("No decisions were found for session ids " + sessionIds, ex);
            return Collections.emptyMap();
        }
        catch (Exception ex) {
            log.error("Error occurred during loading decisions for session ids " + sessionIds, ex);
            throw new RuntimeException("Error occurred during loading decisions for session ids " + sessionIds, ex);
        }

        return result;
    }

    @Override
    public TaskData getTaskData(String taskId, String sessionId) {
        return fetchUtil.getTaskData(taskId,sessionId);
    }

    @Override
    public Map<Long, TaskData> getTaskData(Collection<Long> ids) {
        return fetchUtil.getTaskData(ids);
    }


    private Set<PlotNode> getSessionScopeNodes(Map<TaskDataDto, List<PlotNode>> mapAfterFiltration, String sessionId) {
        List<String> metricNameList = new ArrayList<String>();
        Map<String, List<Long>> nameId = new HashMap<String, List<Long>>();
        Set<PlotNode> ssPlotNodes = new HashSet<PlotNode>();

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
                                Arrays.asList(tempMetricNameDto));

                        ssPlotNodes.add(ssPlotNode);
                    }
                    //we looking for all Id of TaskDataDto for every MetricNameDto
                    if (!nameId.containsKey(metricNameDto.getMetricName()))
                        nameId.put(metricNameDto.getMetricName(), new ArrayList<Long>());
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
        return Lists.newArrayList(mapForTests, mapForSessionScope);
    }

    private boolean isSessionScopeMetric(MetricNameDto metricNameDto){
        return  (metricNameDto.getOrigin().equals(MetricNameDto.Origin.SESSION_SCOPE_TG)
                || metricNameDto.getOrigin().equals(MetricNameDto.Origin.SESSION_SCOPE_MONITORING));
    }
}

