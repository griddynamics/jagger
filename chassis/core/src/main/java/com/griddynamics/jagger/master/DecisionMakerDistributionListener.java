package com.griddynamics.jagger.master;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.ProviderUtil;
import com.griddynamics.jagger.engine.e1.collector.limits.*;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerInfo;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.engine.e1.sessioncomparation.WorstCaseDecisionMaker;
import com.griddynamics.jagger.master.configuration.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DecisionMakerDistributionListener implements DistributionListener {
    private static final Logger log = LoggerFactory.getLogger(DecisionMakerDistributionListener.class);

    private NodeContext nodeContext;
    private WorstCaseDecisionMaker worstCaseDecisionMaker = new WorstCaseDecisionMaker();

    public DecisionMakerDistributionListener(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        //do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {

        if (task instanceof CompositeTask) {
            TestGroupDecisionMakerListener decisionMakerListener = TestGroupDecisionMakerListener.Composer.compose(ProviderUtil.provideElements(((CompositeTask) task).getDecisionMakerListeners(),
                    sessionId,
                    taskId,
                    nodeContext,
                    JaggerPlace.TEST_GROUP_DECISION_MAKER_LISTENER));

            DataService dataService = new DefaultDataService(nodeContext);

            // Get tests in test group
            CompositeTask compositeTask = (CompositeTask) task;
            List<WorkloadTask> workloadTasks = new ArrayList<WorkloadTask>();
            for (CompositableTask compositableTask : compositeTask.getAttendant()) {
                if (compositableTask instanceof WorkloadTask) {
                    workloadTasks.add((WorkloadTask) compositableTask);
                }
            }
            for (CompositableTask compositableTask : compositeTask.getLeading()) {
                if (compositableTask instanceof WorkloadTask) {
                    workloadTasks.add((WorkloadTask) compositableTask);
                }
            }

            // Make decision per tests
            Set<DecisionPerTest> decisionsPerTest = new HashSet<DecisionPerTest>();

            for (WorkloadTask workloadTask : workloadTasks) {
                if (workloadTask.getLimits() != null) {
                    String testName = workloadTask.getName();

                    // Get data for current session
                    TestEntity testEntity = dataService.getTestByName(sessionId,testName);
                    Set<MetricEntity> metricEntitySet = dataService.getMetrics(testEntity);
                    Map<MetricEntity,Double> metricValues = dataService.getMetricSummary(metricEntitySet);

                    Map<String,MetricEntity> idToEntity = new HashMap<String, MetricEntity>();
                    for (MetricEntity metricEntity : metricValues.keySet()) {
                        idToEntity.put(metricEntity.getMetricId(),metricEntity);
                    }

                    // Get relation limit <-> metrics
                    boolean needBaselineSessionValue = false;
                    Map<Limit,Set<MetricEntity>> limitToEntity = new HashMap<Limit, Set<MetricEntity>>();
                    for (Limit limit : workloadTask.getLimits().getLimits()) {
                        limitToEntity.put(limit,getMetricsForLimit(limit, idToEntity));
                        if (!limitToEntity.get(limit).isEmpty()) {
                            if (limit.getRefValue() == null) {
                                needBaselineSessionValue = true;
                            }
                        }
                    }

                    // Get data for baseline session
                    //todo ??? JFG_745 check that tests match when getting baseline values
                    Map<String,Double> metricIdToValuesBaseline = new HashMap<String, Double>();
                    if (needBaselineSessionValue) {
                        String baselineId = workloadTask.getLimits().getBaselineId();
                        TestEntity testEntityBaseline = dataService.getTestByName(baselineId, testName);
                        if (testEntityBaseline != null) {
                            Set<MetricEntity> metricEntitySetBaseline = dataService.getMetrics(testEntityBaseline);
                            Map<MetricEntity,Double> metricValuesBaseline = dataService.getMetricSummary(metricEntitySetBaseline);
                            for (Map.Entry<MetricEntity,Double> entry : metricValuesBaseline.entrySet()) {
                                metricIdToValuesBaseline.put(entry.getKey().getMetricId(),entry.getValue());
                            }
                        }
                        else {
                            log.error("Was not able to find test {} in baseline session {}",testName,baselineId);
                        }
                    }

                    log.info("Making decision for test: {} (baseline session: {})",testName,workloadTask.getLimits().getBaselineId());

                    // Compare
                    Set<DecisionPerLimit> decisionsPerLimit = new HashSet<DecisionPerLimit>();
                    for (Limit limit : workloadTask.getLimits().getLimits()) {
                        DecisionPerLimit decisionPerLimit = compareMetricsToLimit(limit,limitToEntity.get(limit),
                                metricValues,metricIdToValuesBaseline,
                                workloadTask.getLimits().getLimitSetConfig());

                        log.debug(decisionPerLimit.toString());

                        decisionsPerLimit.add(decisionPerLimit);
                    }

                    // decisionPetTest = worst case decisionPerLimit
                    Decision decisionPerTest;
                    List<Decision> decisions = new ArrayList<Decision>();
                    for (DecisionPerLimit decisionPerLimit : decisionsPerLimit) {
                        decisions.add(decisionPerLimit.getDecisionPerLimit());
                    }
                    decisionPerTest = worstCaseDecisionMaker.getDecision(decisions);

                    DecisionPerTest resultForTest = new DecisionPerTest(testEntity, decisionsPerLimit, decisionPerTest);
                    log.info(resultForTest.toString());

                    decisionsPerTest.add(resultForTest);
                }
            }

            // call test group listener
            TestGroupDecisionMakerInfo testGroupDecisionMakerInfo =
                    new TestGroupDecisionMakerInfo((CompositeTask)task,sessionId,decisionsPerTest);
            Decision decisionPerTestGroup = decisionMakerListener.onDecisionMaking(testGroupDecisionMakerInfo);

            log.info("Decision for test group {} - {}",task.getTaskName(),decisionPerTestGroup);

            //todo ??? JFG_746 save/update decision
        }
    }


    private Set<MetricEntity> getMetricsForLimit(Limit limit, Map<String, MetricEntity> idToEntity) {
        String metricId = limit.getMetricName();
        Set<MetricEntity> metricsForLimit = new HashSet<MetricEntity>();

        // Strict matching
        if (idToEntity.keySet().contains(metricId)) {
            metricsForLimit.add(idToEntity.get(metricId));
        }
        else {
            // Matching to regex (f.e. agent name(s) or aggregator name(s) omitted)
            String regex = "^" + metricId + ".*";
            for (String id : idToEntity.keySet()) {
                if (id.matches(regex)) {
                    metricsForLimit.add(idToEntity.get(id));
                }
            }
        }

        return metricsForLimit;
    }

    private DecisionPerLimit compareMetricsToLimit(Limit limit, Set<MetricEntity> metricsPerLimit,
                                                   Map<MetricEntity, Double> metricValues,
                                                   Map<String, Double> metricValuesBaseline,
                                                   LimitSetConfig limitSetConfig) {

        Set<DecisionPerMetric> decisionsPerMetric = new HashSet<DecisionPerMetric>();
        for (MetricEntity metricEntity : metricsPerLimit) {
            Double refValue = limit.getRefValue();
            Double value = metricValues.get(metricEntity);
            Decision decision = Decision.OK;

            // if null - we are comparing to baseline
            if (refValue == null) {
                if (metricValuesBaseline.containsKey(metricEntity.getMetricId())) {
                    refValue = metricValuesBaseline.get(metricEntity.getMetricId());
                }
            }

            if (refValue == null) {
                String errorText = "Reference value for comparison of metric vs baseline was not found. Metric: {},\n" +
                        "Decision per metric: {}";
                switch (limitSetConfig.getDecisionWhenNoBaselineForMetric()) {
                    case OK:
                        decision = Decision.OK;
                        log.info(errorText,metricEntity.toString(), decision);
                        break;
                    case WARNING:
                        decision = Decision.WARNING;
                        log.warn(errorText, metricEntity.toString(), decision);
                        break;
                    default:
                        decision = Decision.FATAL;
                        log.error(errorText, metricEntity.toString(), decision);
                        break;
                }
            }
            else {
                if ((refValue > 0.0) || (refValue.equals(0D))) {
                    if (value < limit.getLowerErrorThreshold()*refValue) {
                        decision = Decision.FATAL;
                    }
                    else if (value < limit.getLowerWarningThreshold()*refValue) {
                        decision = Decision.WARNING;
                    }
                    else if (value < limit.getUpperWarningThreshold()*refValue) {
                        decision = Decision.OK;
                    }
                    else if (value < limit.getUpperErrorThreshold()*refValue) {
                        decision = Decision.WARNING;
                    }
                    else {
                        decision = Decision.FATAL;
                    }
                }
                else {
                    if (value < limit.getUpperErrorThreshold()*refValue) {
                        decision = Decision.FATAL;
                    }
                    else if (value < limit.getUpperWarningThreshold()*refValue) {
                        decision = Decision.WARNING;
                    }
                    else if (value < limit.getLowerWarningThreshold()*refValue) {
                        decision = Decision.OK;
                    }
                    else if (value < limit.getLowerErrorThreshold()*refValue) {
                        decision = Decision.WARNING;
                    }
                    else {
                        decision = Decision.FATAL;
                    }
                }
            }

            decisionsPerMetric.add(new DecisionPerMetric(metricEntity, value, refValue, decision));
        }

        // decisionPerLimit = worst case decisionPerLimit
        Decision decisionPerLimit;
        List<Decision> decisions = new ArrayList<Decision>();
        for (DecisionPerMetric decisionPerMetric : decisionsPerMetric) {
            decisions.add(decisionPerMetric.getDecisionPerMetric());
            log.info(decisionPerMetric.toString());
        }
        if (decisions.isEmpty()) {
            String errorText = "Limit doesn't have any matching metric in current session. Limit {},\n" +
                    "Decision per limit: {}";
            switch (limitSetConfig.getDecisionWhenNoMetricForLimit()) {
                case OK:
                    decisionPerLimit = Decision.OK;
                    log.info(errorText,limit, decisionPerLimit);
                    break;
                case WARNING:
                    decisionPerLimit = Decision.WARNING;
                    log.warn(errorText,limit, decisionPerLimit);
                    break;
                default:
                    decisionPerLimit = Decision.FATAL;
                    log.error(errorText,limit, decisionPerLimit);
                    break;
            }
        }
        else {
            decisionPerLimit = worstCaseDecisionMaker.getDecision(decisions);
        }

        return new DecisionPerLimit(limit,decisionsPerMetric,decisionPerLimit);
    }
}

