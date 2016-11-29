/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.griddynamics.jagger.engine.e1.sessioncomparation.workload;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.engine.e1.reporting.SummaryReporter;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.FeatureComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Verdict;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.*;

import static com.griddynamics.jagger.engine.e1.sessioncomparation.ComparisonUtil.calculateDeviation;

public class WorkloadFeatureComparator implements FeatureComparator<WorkloadComparisonResult> {
    private static final Logger log = LoggerFactory.getLogger(WorkloadFeatureComparator.class);

    private SummaryReporter summaryReporter;
    private SummaryReporter baselineSummaryReporter;
    private DatabaseService databaseService;
    private WorkloadDecisionMaker workloadDecisionMaker;


    @Override
    public List<Verdict<WorkloadComparisonResult>> compare(String currentSession, String baselineSession) {

        log.debug("Going to compare workloads for sessions {} {}", currentSession, baselineSession);

        DefaultDataService dataService = new DefaultDataService(databaseService);

        // Find if there are matching tests in sessions
        // Strategy to match sessions: we will use baseline only when all test parameters are matching
        Set<TestEntity> baselineSessionTests = null;
        SessionMatchingSetup sessionMatchingSetup = new SessionMatchingSetup(true, EnumSet.of(SessionMatchingSetup.MatchBy.ALL));
        Map<String, Set<TestEntity>> matchingTestEntities =
                dataService.getTestsWithName(Arrays.asList(currentSession, baselineSession), null, sessionMatchingSetup);
        if (!matchingTestEntities.get(baselineSession).isEmpty()) {
            baselineSessionTests = matchingTestEntities.get(baselineSession);
        }

        Decision decision;
        String description;
        double throughputDeviation;
        double avgLatencyDeviation;
        double stdDevLatencyDeviation;
        double successRateDeviation;
        // Total duration is not supported any more (starting from 1.2.6) - deviation will be also 0
        double totalDurationDeviation;
        WorkloadComparisonResult workloadComparisonResult = null;

        // Compare
        List<Verdict<WorkloadComparisonResult>> result = Lists.newArrayList();

        if (baselineSessionTests != null) {

            // Mapping of matching tests from different sessions
            HashMap<Long,Long> currentTestIdToBaselineTestId = new HashMap<Long, Long>();
            for (TestEntity baselineSessionTest : baselineSessionTests) {
                Long key = -1L;
                Long value = -1L;
                for (Map.Entry<Long, String> entry : baselineSessionTest.getTaskDataDto().getIdToSessionId().entrySet()) {
                    if (entry.getValue().equals(currentSession)) {key = entry.getKey();}
                    if (entry.getValue().equals(baselineSession)) {value = entry.getKey();}
                }
                if ((key > 0) && (value > 0)) {
                    currentTestIdToBaselineTestId.put(key, value);
                }
            }

            Map<TestEntity, Map<MetricEntity, MetricSummaryValueEntity>> currentSessionStandardMetrics = summaryReporter.getStandardMetricsPerTest(currentSession);
            Map<TestEntity, Map<MetricEntity, MetricSummaryValueEntity>> baselineSessionStandardMetrics = baselineSummaryReporter.getStandardMetricsPerTest(baselineSession);

            log.debug("current session {} workloads", currentSessionStandardMetrics.keySet().size());
            log.debug("baseline session {} workloads", baselineSessionStandardMetrics.keySet().size());

            // Compare
            for (TestEntity currentTest : currentSessionStandardMetrics.keySet()) {
                boolean testMatched = false;

                // Reset
                decision = Decision.ERROR;
                description = "";
                throughputDeviation = 0.0;
                avgLatencyDeviation = 0.0;
                stdDevLatencyDeviation = 0.0;
                successRateDeviation = 0.0;
                totalDurationDeviation = 0.0;
                workloadComparisonResult = null;

                for (Map.Entry<Long, Long> currentToBaseline : currentTestIdToBaselineTestId.entrySet()) {
                    // Check that baseline contains this particular test
                    if (currentTest.getId().equals(currentToBaseline.getKey())) {
                        testMatched = true;

                        log.debug("Going to compare workload {}", currentTest.getName());

                        Map<MetricEntity, MetricSummaryValueEntity> currentTestMetrics = currentSessionStandardMetrics.get(currentTest);
                        Map<MetricEntity, MetricSummaryValueEntity> baselineTestMetrics = null;
                        TestEntity baselineTestEntity = null;
                        for (TestEntity baselineTest : baselineSessionStandardMetrics.keySet()) {
                            // Check that we have summary values for this particular test
                            if (baselineTest.getId().equals(currentToBaseline.getValue())) {
                                baselineTestMetrics = baselineSessionStandardMetrics.get(baselineTest);
                                baselineTestEntity = baselineTest;
                                break;
                            }
                        }


                        boolean thereWereNoErrors = true;

                        for (MetricEntity currentMetricEntity : currentTestMetrics.keySet()) {
                            boolean metricMatch = false;

                            if (baselineTestMetrics != null) {
                                for (MetricEntity baselineMetricEntity : baselineTestMetrics.keySet()) {
                                    if (currentMetricEntity.getMetricId().equals(baselineMetricEntity.getMetricId()) ||
                                            (
                                                    (currentMetricEntity.getMetricNameDto().getMetricNameSynonyms() != null) &&
                                                    (currentMetricEntity.getMetricNameDto().getMetricNameSynonyms().contains(baselineMetricEntity.getMetricId()))
                                            )
                                        ) {
                                        metricMatch = true;

                                        Double currentValue = currentTestMetrics.get(currentMetricEntity).getValue();
                                        Double baselineValue = baselineTestMetrics.get(baselineMetricEntity).getValue();

                                        if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.THROUGHPUT_ID).contains(currentMetricEntity.getMetricId())) {
                                            throughputDeviation = calculateDeviation(new BigDecimal(currentValue), new BigDecimal(baselineValue));
                                        }
                                        if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.SUCCESS_RATE_ID).contains(currentMetricEntity.getMetricId())) {
                                            successRateDeviation = calculateDeviation(new BigDecimal(currentValue), new BigDecimal(baselineValue));
                                        }
                                        if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.LATENCY_ID).contains(currentMetricEntity.getMetricId())) {
                                            avgLatencyDeviation = calculateDeviation(new BigDecimal(currentValue), new BigDecimal(baselineValue));
                                        }
                                        if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID).contains(currentMetricEntity.getMetricId())) {
                                            stdDevLatencyDeviation = calculateDeviation(new BigDecimal(currentValue), new BigDecimal(baselineValue));
                                        }
                                    }
                                }
                            }

                            if (!metricMatch) {
                                thereWereNoErrors = false;
                                description = "Error: can't find metric '" + currentMetricEntity.getMetricId() + "' in baseline results";
                                log.error(description);
                            }
                        }

                        // make decision if there were no errors during data fetching
                        if (thereWereNoErrors) {
                            workloadComparisonResult = WorkloadComparisonResult.builder()
                                    .throughputDeviation(throughputDeviation)
                                    .avgLatencyDeviation(avgLatencyDeviation)
                                    .stdDevLatencyDeviation(stdDevLatencyDeviation)
                                    .successRateDeviation(successRateDeviation)
                                    .totalDurationDeviation(totalDurationDeviation)
                                    .currentSessionEntity(summaryReporter.getSessionEntity(currentSession))
                                    .baselineSessionEntity(baselineSummaryReporter.getSessionEntity(baselineSession))
                                    .currentTestEntity(currentTest)
                                    .baselineTestEntity(baselineTestEntity)
                                    .currentStandardMetrics(currentTestMetrics)
                                    .baselineStandardMetrics(baselineTestMetrics)
                                    .build();

                            decision = workloadDecisionMaker.makeDecision(workloadComparisonResult);
                            description = currentTest.getName();
                        } else {
                            decision = Decision.ERROR;
                        }

                        Verdict<WorkloadComparisonResult> verdict = new Verdict<WorkloadComparisonResult>(description, decision, workloadComparisonResult);
                        log.debug("Verdict {}", verdict);
                        result.add(verdict);

                        break;
                    }
                }

                if (!testMatched) {
                    // Not an error case => verdict is not set
                    // Possible situation when new test was added to suite

                    description = "Warning: no matching test " + currentTest.getName();
                    log.warn(description);
                }
            }
        } else {
            decision = Decision.ERROR;
            description = "Error: no matching tests for sessions " + currentSession + ", " + baselineSession;
            log.error(description);

            Verdict<WorkloadComparisonResult> verdict = new Verdict<WorkloadComparisonResult>(description, decision, workloadComparisonResult);
            log.debug("Verdict {}", verdict);
            result.add(verdict);
        }


        return result;
    }

    @Override
    public String getDescription() {
        return "Workload";
    }

    @Required
    public void setWorkloadDecisionMaker(WorkloadDecisionMaker workloadDecisionMaker) {
        this.workloadDecisionMaker = workloadDecisionMaker;
    }

    public WorkloadDecisionMaker getWorkloadDecisionMaker() {
        return workloadDecisionMaker;
    }

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Required
    public void setSummaryReporter(SummaryReporter summaryReporter) {
        this.summaryReporter = summaryReporter;
    }
    @Required
    public void setBaselineSummaryReporter(SummaryReporter baselineSummaryReporter) {
        this.baselineSummaryReporter = baselineSummaryReporter;
    }


}
