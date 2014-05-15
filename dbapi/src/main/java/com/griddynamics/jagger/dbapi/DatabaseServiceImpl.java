package com.griddynamics.jagger.dbapi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.entity.NodeInfoEntity;
import com.griddynamics.jagger.dbapi.entity.NodePropertyEntity;
import com.griddynamics.jagger.dbapi.fetcher.*;
import com.griddynamics.jagger.dbapi.model.rules.TreeViewGroupMetricsToNodeRule;
import com.griddynamics.jagger.dbapi.model.rules.TreeViewGroupMetricsToNodeRuleProvider;
import com.griddynamics.jagger.dbapi.model.rules.TreeViewGroupRule;
import com.griddynamics.jagger.dbapi.model.rules.TreeViewGroupRuleProvider;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.DefaultWorkloadParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.dbapi.provider.*;
import com.griddynamics.jagger.dbapi.util.*;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.dbapi.model.*;
import com.griddynamics.jagger.dbapi.dto.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by kgribov on 4/2/14.
 */
public class DatabaseServiceImpl implements DatabaseService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private EntityManager entityManager;

    private WebClientProperties webClientProperties;

    private ExecutorService threadPool;
    private LegendProvider legendProvider;

    private Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups;
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;
    private List<MetricNameDto> standardMetricNameDtoList;
    private Map<String,Set<String>> defaultMonitoringParams = new HashMap<String, Set<String>>();

    private CustomMetricPlotNameProvider customMetricPlotNameProvider;
    private CustomMetricNameProvider customMetricNameProvider;
    private LatencyMetricNameProvider latencyMetricNameProvider;
    private ValidatorNamesProvider validatorNamesProvider;
    private SessionInfoProviderImpl sessionInfoServiceImpl;

    private TreeViewGroupRuleProvider treeViewGroupRuleProvider;
    private TreeViewGroupMetricsToNodeRuleProvider treeViewGroupMetricsToNodeRuleProvider;

    private ThroughputMetricPlotFetcher throughputMetricPlotFetcher;
    private LatencyMetricPlotFetcher latencyMetricPlotFetcher;
    private TimeLatencyPercentileMetricPlotFetcher timeLatencyPercentileMetricPlotFetcher;
    private CustomMetricPlotFetcher customMetricPlotFetcher;
    private CustomTestGroupMetricPlotFetcher customTestGroupMetricPlotFetcher;
    private MonitoringMetricPlotFetcher monitoringMetricPlotFetcher;

    private StandardMetricSummaryFetcher standardMetricSummaryFetcher;
    private DurationMetricSummaryFetcher durationMetricSummaryFetcher;
    private LatencyMetricSummaryFetcher latencyMetricDataFetcher;
    private CustomMetricSummaryFetcher customMetricSummaryFetcher;
    private CustomTestGroupMetricSummaryFetcher customTestGroupMetricSummaryFetcher;
    private ValidatorSummaryFetcher validatorSummaryFetcher;

    private FetchUtil fetchUtil;

    //==========Setters

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Map<GroupKey, DefaultMonitoringParameters[]> getMonitoringPlotGroups() {
        return monitoringPlotGroups;
    }

    @Required
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
        this.defaultMonitoringParams = getDefaultMonitoringParametersMap(monitoringPlotGroups);
    }

    @Required
    public void setWorkloadPlotGroups(Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups) {
        this.workloadPlotGroups = workloadPlotGroups;
    }

    @Required
    public void setCustomMetricPlotNameProvider(CustomMetricPlotNameProvider customMetricPlotNameProvider) {
        this.customMetricPlotNameProvider = customMetricPlotNameProvider;
    }

    @Required
    public void setStandardMetricNameDtoList(List<MetricNameDto> standardMetricNameDtoList) {
        this.standardMetricNameDtoList = standardMetricNameDtoList;
    }

    @Required
    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    @Required
    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    @Required
    public void setThroughputMetricPlotFetcher(ThroughputMetricPlotFetcher throughputMetricPlotFetcher) {
        this.throughputMetricPlotFetcher = throughputMetricPlotFetcher;
    }

    @Required
    public void setLatencyMetricPlotFetcher(LatencyMetricPlotFetcher latencyMetricPlotFetcher) {
        this.latencyMetricPlotFetcher = latencyMetricPlotFetcher;
    }

    @Required
    public void setTimeLatencyPercentileMetricPlotFetcher(TimeLatencyPercentileMetricPlotFetcher timeLatencyPercentileMetricPlotFetcher) {
        this.timeLatencyPercentileMetricPlotFetcher = timeLatencyPercentileMetricPlotFetcher;
    }

    @Required
    public void setCustomMetricPlotFetcher(CustomMetricPlotFetcher customMetricPlotFetcher) {
        this.customMetricPlotFetcher = customMetricPlotFetcher;
    }

    @Required
    public void setCustomTestGroupMetricPlotFetcher(CustomTestGroupMetricPlotFetcher customTestGroupMetricPlotFetcher) {
        this.customTestGroupMetricPlotFetcher = customTestGroupMetricPlotFetcher;
    }

    @Required
    public void setMonitoringMetricPlotFetcher(MonitoringMetricPlotFetcher monitoringMetricPlotFetcher) {
        this.monitoringMetricPlotFetcher = monitoringMetricPlotFetcher;
    }

    @Required
    public void setWebClientProperties(WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
        this.webClientProperties.setUserCommentStoreAvailable(checkIfUserCommentStorageAvailable());
        this.webClientProperties.setTagsStoreAvailable(checkIfTagsStorageAvailable());
    }

    public WebClientProperties getWebClientProperties() {
        return webClientProperties;
    }

    @Required
    public void setStandardMetricSummaryFetcher(StandardMetricSummaryFetcher standardMetricSummaryFetcher) {
        this.standardMetricSummaryFetcher = standardMetricSummaryFetcher;
    }

    @Required
    public void setDurationMetricSummaryFetcher(DurationMetricSummaryFetcher durationMetricSummaryFetcher) {
        this.durationMetricSummaryFetcher = durationMetricSummaryFetcher;
    }

    @Required
    public void setLatencyMetricDataFetcher(LatencyMetricSummaryFetcher latencyMetricDataFetcher) {
        this.latencyMetricDataFetcher = latencyMetricDataFetcher;
    }

    @Required
    public void setCustomMetricSummaryFetcher(CustomMetricSummaryFetcher customMetricSummaryFetcher) {
        this.customMetricSummaryFetcher = customMetricSummaryFetcher;
    }

    @Required
    public void setValidatorSummaryFetcher(ValidatorSummaryFetcher validatorSummaryFetcher) {
        this.validatorSummaryFetcher = validatorSummaryFetcher;
    }

    @Required
    public void setCustomTestGroupMetricSummaryFetcher(CustomTestGroupMetricSummaryFetcher customTestGroupMetricSummaryFetcher) {
        this.customTestGroupMetricSummaryFetcher = customTestGroupMetricSummaryFetcher;
    }

    @Required
    public void setTreeViewGroupRuleProvider(TreeViewGroupRuleProvider treeViewGroupRuleProvider) {
        this.treeViewGroupRuleProvider = treeViewGroupRuleProvider;
    }

    @Required
    public void setTreeViewGroupMetricsToNodeRuleProvider(TreeViewGroupMetricsToNodeRuleProvider treeViewGroupMetricsToNodeRuleProvider) {
        this.treeViewGroupMetricsToNodeRuleProvider = treeViewGroupMetricsToNodeRuleProvider;
    }

    @Required
    public void setSessionInfoServiceImpl(SessionInfoProviderImpl sessionInfoServiceImpl) {
        this.sessionInfoServiceImpl = sessionInfoServiceImpl;
    }

    @Required
    public void setCustomMetricNameProvider(CustomMetricNameProvider customMetricNameProvider) {
        this.customMetricNameProvider = customMetricNameProvider;
    }

    @Required
    public void setLatencyMetricNameProvider(LatencyMetricNameProvider latencyMetricNameProvider) {
        this.latencyMetricNameProvider = latencyMetricNameProvider;
    }

    @Required
    public void setValidatorNamesProvider(ValidatorNamesProvider validatorNamesProvider) {
        this.validatorNamesProvider = validatorNamesProvider;
    }

    @Required
    public void setFetchUtil(FetchUtil fetchUtil) {
        this.fetchUtil = fetchUtil;
    }

    //===========================
    //=======Get plot data=======
    //===========================

    @Override
    public Map<MetricNode, PlotSeriesDto> getPlotDataByMetricNode(Set<MetricNode> plots) throws IllegalArgumentException{

        if (plots.isEmpty()) {
            return Collections.emptyMap();
        }

        long temp = System.currentTimeMillis();

        Set<MetricNameDto> metricNameDtoSet = new HashSet<MetricNameDto>();

        for (MetricNode metricNode : plots) {
            metricNameDtoSet.addAll(metricNode.getMetricNameDtoList());
        }

        Map<MetricNameDto,List<PlotDatasetDto>> resultMap = getPlotDataByMetricNameDto(metricNameDtoSet);

        Multimap<MetricNode, PlotDatasetDto> tempMultiMap = ArrayListMultimap.create();

        for (Map.Entry<MetricNameDto,List<PlotDatasetDto>> entry : resultMap.entrySet()) {
            for (MetricNode metricNode : plots) {
                if (metricNode.getMetricNameDtoList().contains(entry.getKey())) {
                    tempMultiMap.putAll(metricNode, entry.getValue());
                    break;
                }
            }
        }

        Map<MetricNode, PlotSeriesDto> result = new HashMap<MetricNode, PlotSeriesDto>();

        for (MetricNode metricNode : plots) {
            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>(tempMultiMap.get(metricNode));

            // Sort lines by legend
            Collections.sort(plotDatasetDtoList, new Comparator<PlotDatasetDto>() {
                @Override
                public int compare(PlotDatasetDto o1, PlotDatasetDto o2) {
                    String param1 = o1.getLegend();
                    String param2 = o2.getLegend();
                    int res = String.CASE_INSENSITIVE_ORDER.compare(param1,param2);
                    return (res != 0) ? res : param1.compareTo(param2);
                }
            });

            // at the moment all MetricNameDtos in MetricNode have same taskIds => it is valid to use first one for legend provider
            result.put(metricNode, new PlotSeriesDto(plotDatasetDtoList,"Time, sec", "",legendProvider.getPlotHeader(metricNode.getMetricNameDtoList().get(0).getTaskIds(), metricNode.getDisplayName())));
        }

        log.debug("Total time of plots for metricNodes retrieving : " + (System.currentTimeMillis() - temp));
        return result;
    }

    @Override
    public Map<MetricNameDto, List<PlotDatasetDto>> getPlotDataByMetricNameDto(Set<MetricNameDto> metricNames) throws IllegalArgumentException {

        if (metricNames.isEmpty()) {
            return Collections.emptyMap();
        }

        long temp = System.currentTimeMillis();

        final Multimap<PlotsDbMetricDataFetcher, MetricNameDto> fetchMap = ArrayListMultimap.create();

        for (MetricNameDto metricNameDto : metricNames) {
            switch (metricNameDto.getOrigin()) {
                case METRIC:
                    fetchMap.put(customMetricPlotFetcher, metricNameDto);
                    break;
                case TEST_GROUP_METRIC:
                    fetchMap.put(customTestGroupMetricPlotFetcher, metricNameDto);
                    break;
                case MONITORING:
                    fetchMap.put(monitoringMetricPlotFetcher, metricNameDto);
                    break;
                case LATENCY:
                    fetchMap.put(latencyMetricPlotFetcher, metricNameDto);
                    break;
                case LATENCY_PERCENTILE:
                    fetchMap.put(timeLatencyPercentileMetricPlotFetcher, metricNameDto);
                    break;
                case THROUGHPUT:
                    fetchMap.put(throughputMetricPlotFetcher, metricNameDto);
                    break;
                default:  // if anything else
                    log.error("MetricNameDto with origin : {} appears in metric name list for plot retrieving ({})", metricNameDto.getOrigin(), metricNameDto);
                    throw new RuntimeException("Unable to get plot for metric " + metricNameDto.getMetricName() +
                            " with origin: " + metricNameDto.getOrigin());
            }
        }

        Set<PlotsDbMetricDataFetcher> fetcherSet = fetchMap.keySet();

        List<Future<Set<Pair<MetricNameDto, List<PlotDatasetDto>>>>> futures = new ArrayList<Future<Set<Pair<MetricNameDto, List<PlotDatasetDto>>>>>();

        for (final PlotsDbMetricDataFetcher fetcher : fetcherSet) {
            futures.add(threadPool.submit(new Callable<Set<Pair<MetricNameDto, List<PlotDatasetDto>>>>() {

                @Override
                public Set<Pair<MetricNameDto, List<PlotDatasetDto>>> call() throws Exception {
                    return fetcher.getResult(new ArrayList<MetricNameDto>(fetchMap.get(fetcher)));
                }
            }));
        }

        Set<Pair<MetricNameDto, List<PlotDatasetDto>>>  resultSet = new HashSet<Pair<MetricNameDto, List<PlotDatasetDto>>>();

        try {
            for (Future<Set<Pair<MetricNameDto, List<PlotDatasetDto>>>> future : futures) {
                resultSet.addAll(future.get());
            }
        } catch (Throwable th) {
            log.error("Exception while plots retrieving", th);
            throw new RuntimeException("Exception while plots retrieving", th);
        }

        Map<MetricNameDto, List<PlotDatasetDto>> result = new HashMap<MetricNameDto, List<PlotDatasetDto>>();

        for (Pair<MetricNameDto, List<PlotDatasetDto>> pair : resultSet) {
            result.put(pair.getFirst(),pair.getSecond());
        }

        log.debug("Total time of plots for metricNameDtos retrieving : " + (System.currentTimeMillis() - temp));

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

            Future<SummaryNode> summaryFuture = threadPool.submit(new SummaryNodeFetcherThread(sessionIds, taskList));
            Future<DetailsNode> detailsNodeFuture = threadPool.submit(new DetailsNodeFetcherThread(sessionIds, taskList));
            //Future<SessionScopePlotsNode> sessionScopePlotsNodeFuture = threadPool.submit(new SessionScopePlotsNodeFetcherThread(sessionIds));

            SummaryNode summaryNode = summaryFuture.get();
            DetailsNode detailsNode = detailsNodeFuture.get();
            //SessionScopePlotsNode sessionScopePlotsNode = sessionScopePlotsNodeFuture.get();

            // todo restore session scope plots for test group metrics JFG_667
            // temporary disabled session scope plots while transferring monitoring to metrics
            //detailsNode.setSessionScopePlotsNode(sessionScopePlotsNode);

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
    public List<MetricDto> getSummaryByMetricNameDto(List<MetricNameDto> metricNames) {

        long temp = System.currentTimeMillis();
        List<MetricDto> result = new ArrayList<MetricDto>(metricNames.size());

        final Multimap<MetricDataFetcher<MetricDto>, MetricNameDto> fetchMap = ArrayListMultimap.create();

        for (MetricNameDto metricName : metricNames){
            switch (metricName.getOrigin()) {
                case STANDARD_METRICS:
                    fetchMap.put(standardMetricSummaryFetcher, metricName);
                    break;
                case DURATION:
                    fetchMap.put(durationMetricSummaryFetcher, metricName);
                    break;
                case LATENCY_PERCENTILE:
                    fetchMap.put(latencyMetricDataFetcher, metricName);
                    break;
                case METRIC:
                    fetchMap.put(customMetricSummaryFetcher, metricName);
                    break;
                case TEST_GROUP_METRIC:
                    fetchMap.put(customTestGroupMetricSummaryFetcher, metricName);
                    break;
                case VALIDATOR:
                    fetchMap.put(validatorSummaryFetcher, metricName);
                    break;
                default:  // if anything else
                    log.error("MetricNameDto with origin : {} appears in metric name list for summary retrieving ({})", metricName.getOrigin(), metricName);
                    throw new RuntimeException("Unable to get summary data for metric " + metricName.getMetricName() +
                            " with origin: " + metricName.getOrigin());
            }
        }

        Set<MetricDataFetcher<MetricDto>> fetcherSet = fetchMap.keySet();

        List<Future<Set<MetricDto>>> futures = new ArrayList<Future<Set<MetricDto>>>();

        for (final MetricDataFetcher<MetricDto> fetcher : fetcherSet) {
            futures.add(threadPool.submit(new Callable<Set<MetricDto>>() {

                @Override
                public Set<MetricDto> call() throws Exception {
                    return fetcher.getResult(new ArrayList<MetricNameDto>(fetchMap.get(fetcher)));
                }
            }));
        }

        try {
            for (Future<Set<MetricDto>> future : futures) {
                result.addAll(future.get());
            }
        } catch (Throwable th) {
            th.printStackTrace();
            log.error("Exception while summary retrieving", th);
            throw new RuntimeException("Exception while summary retrieving" + th.getMessage());
        }
        log.debug("{} ms spent for fetching summary data for {} metrics", System.currentTimeMillis() - temp, metricNames.size());

        return result;
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
        for (TaskDataDto taskDataDto : tddos){
            for (MetricNameDto metricNameDto : standardMetricNameDtoList) {
                MetricNameDto metric = new MetricNameDto();
                metric.setMetricName(metricNameDto.getMetricName());
                metric.setMetricDisplayName(metricNameDto.getMetricDisplayName());
                metric.setOrigin(metricNameDto.getOrigin());
                metric.setTest(taskDataDto);
                metric.setMetricNameSynonyms(metricNameDto.getMetricNameSynonyms());
                list.add(metric);
            }
        }

        try {

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

            list.addAll(latencyMetricNamesFuture.get());
            list.addAll(customMetricNamesFuture.get());
            list.addAll(validatorsNamesFuture.get());
        } catch (Exception e) {
            log.error("Exception occurs while fetching MetricNames for tests : ", e);
            throw new RuntimeException(e);
        }

        log.info("For tasks {} was found {} metrics names for {} ms", new Object[]{tddos, list.size(), System.currentTimeMillis() - time});

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

            Map<TaskDataDto, Boolean> isWorkloadMap = isWorkloadStatisticsAvailable(taskList);
            for (Map.Entry<TaskDataDto, Boolean> entry: isWorkloadMap.entrySet()) {
                if (entry.getValue()) {
                    for (Map.Entry<GroupKey, DefaultWorkloadParameters[]> monitoringPlot : workloadPlotGroups.entrySet()) {
                        MetricNameDto metricNameDto = new MetricNameDto(entry.getKey(), monitoringPlot.getKey().getUpperName());
                        metricNameDto.setOrigin(monitoringPlot.getValue()[0].getOrigin());
                        metricNameDtoList.add(metricNameDto);
                    }
                }
            }

            Set<MetricNameDto> customMetrics = customMetricPlotNameProvider.getPlotNames(taskList);

            metricNameDtoList.addAll(customMetrics);

            log.debug("For sessions {} are available these plots: {}", sessionIds, metricNameDtoList);

            for (MetricNameDto pnd : metricNameDtoList) {
                if ((pnd.getMetricName() == null) || (pnd.getMetricName().equals(""))) {
                    log.warn("Metric with undefined id detected. It will be ignored. Details: " + pnd);
                }
                else {
                    for (TaskDataDto tdd : taskList) {
                        if (tdd.getIds().containsAll(pnd.getTaskIds())) {
                            if (!result.containsKey(tdd)) {
                                result.put(tdd, new ArrayList<PlotNode>());
                            }
                            PlotNode pn = new PlotNode();
                            String id = NameTokens.METRICS_PREFIX + tdd.hashCode() + pnd.getMetricName();
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
                if (monitoringIdsMap.get(tdd).contains(testId)) {
                    if (!resultMap.containsKey(tdd)) {
                        resultMap.put(tdd, new HashSet<PlotNode>());
                    }

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
                        log.warn("Could not find monitoring key for description: '{}' and monitoing task id: '{}'", description, objects[2]);
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
                map.get(key.toString()).getIds().add(id.longValue());
                map.get(key.toString()).getSessionIds().add(sessionId);

                Integer oldValue = mapIds.get(key.toString());
                mapIds.put(key.toString(), (oldValue==null ? 0 : oldValue)+taskIdInt);
            }else{
                TaskDataDto taskDataDto = new TaskDataDto(id.longValue(), name, description);
                Set<String> sessionIdList = new TreeSet<String>();
                sessionIdList.add(sessionId);
                taskDataDto.setSessionIds(sessionIdList);
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

    private boolean checkIfUserCommentStorageAvailable() {

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

    private boolean checkIfTagsStorageAvailable() {

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

    private List<TestDetailsNode> getDetailsTaskNodeList(final Set<String> sessionIds, final List<TaskDataDto> taskList) {
        List<TestDetailsNode> taskDataDtoList = new ArrayList<TestDetailsNode>();

        try {
            Future<Map<TaskDataDto, List<PlotNode>>> metricsPlotsMapFuture = threadPool.submit(
                    new Callable<Map<TaskDataDto, List<PlotNode>>>() {
                        @Override
                        public Map<TaskDataDto, List<PlotNode>> call() throws Exception {
                            return getTestPlotsMap(sessionIds, taskList);
                        }
                    }
            );

            Future<Map<TaskDataDto, List<PlotNode>>> monitoringNewPlotsMapFuture = threadPool.submit(
                    new Callable<Map<TaskDataDto, List<PlotNode>>>() {
                        @Override
                        public Map<TaskDataDto, List<PlotNode>> call() throws Exception {
                            return getMonitoringPlots(sessionIds, taskList);
                        }
                    }
            );

            Map<TaskDataDto, List<PlotNode>> map = metricsPlotsMapFuture.get();
            Map<TaskDataDto, List<PlotNode>> monitoringMap = monitoringNewPlotsMapFuture.get();

            // get agent names
            Set<PlotNode> plotNodeList = new HashSet<PlotNode>();
            if (!monitoringMap.isEmpty()) {
                for (TaskDataDto taskDataDto : monitoringMap.keySet()) {
                    plotNodeList.addAll(monitoringMap.get(taskDataDto));
                }
            }

            for (TaskDataDto taskDataDto : map.keySet()) {
                plotNodeList.addAll(map.get(taskDataDto));
            }
            Map<String,Set<String>> agentNames = getAgentNamesForMonitoringParameters(plotNodeList);

            // get tree
            for (TaskDataDto tdd : taskList) {
                List<PlotNode> metricNodeList = map.get(tdd);
                if (monitoringMap.containsKey(tdd)) {
                    metricNodeList.addAll(monitoringMap.get(tdd));
                }

                String rootId = NameTokens.METRICS_PREFIX + tdd.hashCode();

                if (metricNodeList.size() > 0) {
                    // apply rules how to build tree
                    MetricGroupNode<PlotNode> testDetailsNodeBase = buildTreeAccordingToRules(rootId, agentNames, metricNodeList);

                    // full test details node
                    TestDetailsNode testNode = new TestDetailsNode(testDetailsNodeBase);
                    testNode.setTaskDataDto(tdd);

                    taskDataDtoList.add(testNode);
                }
            }

            MetricRankingProvider.sortPlotNodes(taskDataDtoList);
            return taskDataDtoList;

        } catch (Exception e) {
            log.error("Exception occurs while fetching plotNames for sessions {}, and tests {}", sessionIds, taskList);
            throw new RuntimeException(e);
        }
    }

    private List<TestNode> getSummaryTaskNodeList(List<TaskDataDto> tasks) {

        List<TestNode> taskDataDtoList = new ArrayList<TestNode>();

        Map<TaskDataDto, List<MetricNode>> map = getTestMetricsMap(tasks);
        for (TaskDataDto tdd : map.keySet()) {
            List<MetricNode> metricNodeList = map.get(tdd);
            String rootId = NameTokens.SUMMARY_PREFIX + tdd.hashCode();

            if (metricNodeList.size() > 0) {
                // apply rules how to build tree
                MetricGroupNode<MetricNode> testNodeBase = buildTreeAccordingToRules(rootId, null, metricNodeList);

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
        private Set<String> sessionIds;
        private List<TaskDataDto> taskList;
        public SummaryNodeFetcherThread(Set<String> sessionIds, List<TaskDataDto> taskList) {
            this.sessionIds = sessionIds;
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
            DetailsNode dn = new DetailsNode(NameTokens.CONTROL_METRICS, NameTokens.CONTROL_METRICS);
            if (!taskList.isEmpty()) {
                dn.setTests(getDetailsTaskNodeList(sessionIds, taskList));
            }
            return dn;
        }
    }

    private Map<String,Set<String>> getAgentNamesForMonitoringParameters(Set<PlotNode> plotNodeList) {
        Map<String,Set<String>> agentNames = new HashMap<String, Set<String>>();

        for (PlotNode plotNode : plotNodeList) {
            for (MetricNameDto metricNameDto : plotNode.getMetricNameDtoList()) {
                // old monitoring or new monitoring as metrics
                if ((metricNameDto.getOrigin() == MetricNameDto.Origin.MONITORING) ||
                        (metricNameDto.getOrigin() == MetricNameDto.Origin.TEST_GROUP_METRIC)) {

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

    private <M extends MetricNode> MetricGroupNode<M> buildTreeAccordingToRules(String rootId, Map<String, Set<String>> agentNames, List<M> metricNodeList) {
        // rules to unite metrics in single plot
        TreeViewGroupMetricsToNodeRule unitedMetricsRule = treeViewGroupMetricsToNodeRuleProvider.provide(agentNames);
        // unite metrics and add result to original list
        List<M> unitedMetrics = unitedMetricsRule.filter(rootId, metricNodeList);
        if (unitedMetrics != null) {
            metricNodeList.addAll(unitedMetrics);
        }

        // rules to create test tree view
        TreeViewGroupRule groupedNodesRule = treeViewGroupRuleProvider.provide(rootId, rootId);
        // tree with metrics distributed by groups
        MetricGroupNode<M> testNodeBase = groupedNodesRule.filter(null,metricNodeList);

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

        log.debug("Time spent for testInfo fetching for {} taskDataDtos : {}ms", new Object[]{taskDataDtos.size(), System.currentTimeMillis() - temp});

        return resultMap;
    }

    @Override
    public Map<Long, Map<String, TestInfoDto>> getTestInfoByTaskIds(Set<Long> taskIds) throws RuntimeException {

        if (taskIds.isEmpty()) {
            return Collections.emptyMap();
        }

        long temp = System.currentTimeMillis();

        @SuppressWarnings("all")
        List<Object[]> objectsList = (List<Object[]>)entityManager.createNativeQuery(
                "select wtd.sessionId, wtd.clock, wtd.clockValue, wtd.termination, taskData.id " +
                        "from WorkloadTaskData as wtd join " +
                        "( select  td.id, td.sessionId, td.taskId from TaskData td where td.id in (:taskDataIds) " +
                        ") as taskData " +
                        "on wtd.sessionId=taskData.sessionId and wtd.taskId=taskData.taskId")
                .setParameter("taskDataIds", taskIds)
                .getResultList();

        Map<Long, Map<String, TestInfoDto>> resultMap = new HashMap<Long, Map<String, TestInfoDto>>(taskIds.size());

        for (Object[] objects : objectsList) {

            Long taskId = ((BigInteger)objects[4]).longValue();
            String clock = objects[1] + " (" + objects[2] + ')';
            String termination = (String)objects[3];
            String sessionId = (String)objects[0];

            if (!resultMap.containsKey(taskId)) {
                resultMap.put(taskId,new HashMap<String, TestInfoDto>());
            }
            TestInfoDto testInfo = new TestInfoDto();
            testInfo.setClock(clock);
            testInfo.setTermination(termination);

            resultMap.get(taskId).put(sessionId,testInfo);
        }

        log.debug("Time spent for testInfo fetching for {} tests ids: {}ms", new Object[]{taskIds.size(), System.currentTimeMillis() - temp});

        return resultMap;
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
}

