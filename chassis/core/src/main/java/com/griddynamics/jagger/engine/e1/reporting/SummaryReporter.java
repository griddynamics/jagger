/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.entity.WorkloadTaskData;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.util.FormatCalculator;
import com.griddynamics.jagger.util.MetricNamesRankingProvider;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SummaryReporter {
    private DatabaseService databaseService;
    private String sessionId;
    private Map<TestEntity, Set<MetricEntity>> metricsPerTest;
    private Map<String, List<SummaryDto>> summaryMap = new HashMap<String, List<SummaryDto>>();
    private Map<String, List<SummaryDto>> latencyPercentilesMap = new HashMap<String, List<SummaryDto>>();
    private Map<String, List<SummaryDto>> validatorsMap = new HashMap<String, List<SummaryDto>>();
    private Map<TestEntity, Map<MetricEntity, MetricSummaryValueEntity>> standardMetricsMap = new HashMap<TestEntity, Map<MetricEntity, MetricSummaryValueEntity>>();
    private DateFormat dateFormatter = new SimpleDateFormat(FormatCalculator.DATE_FORMAT);
    private int numberOfTestGroups;
    private boolean isMetricHighlighting;
    private List<SummaryTestDto> testSummaryData = new ArrayList<>();
    private Map<TestEntity,Map<String,Double>> dataForScalabilityPlots = new HashMap<TestEntity, Map<String, Double>>();
    private SessionEntity sessionEntity;

    private SessionStatusDecisionMaker decisionMaker;
    private StatusImageProvider statusImageProvider;

    @Required
    public void setMetricHighlighting(boolean isMetricHighlighting) {
        this.isMetricHighlighting = isMetricHighlighting;
    }

    @Required
    public void setDecisionMaker(SessionStatusDecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    @Required
    public void setStatusImageProvider(StatusImageProvider statusImageProvider) {
        this.statusImageProvider = statusImageProvider;
    }


    public List<SummaryDto> getSummary(String sessionId, String taskId) {

        getData(sessionId);

        if (summaryMap.containsKey(taskId)) {
            return summaryMap.get(taskId);
        } else {
            return null;
        }
    }

    public List<SummaryDto> getValidators(String sessionId, String taskId) {

        getData(sessionId);

        if (validatorsMap.containsKey(taskId)) {
            return validatorsMap.get(taskId);
        } else {
            return null;
        }
    }

    public List<SummaryDto> getLatencyPercentile(String sessionId, String taskId) {

        getData(sessionId);

        if (latencyPercentilesMap.containsKey(taskId)) {
            return latencyPercentilesMap.get(taskId);
        } else {
            return null;
        }
    }

    public Map<TestEntity, Map<MetricEntity, MetricSummaryValueEntity>> getStandardMetricsPerTest(String sessionId) {

        getData(sessionId);

        return standardMetricsMap;
    }

    public int getNumberOfTestGroups(String sessionId) {

        getData(sessionId);

        return numberOfTestGroups;
    }

    public List<SummaryTestDto> getTestSummaryData(String sessionId) {

        getData(sessionId);

        return testSummaryData;
    }

    public Map<TestEntity,Map<String,Double>> getDataForScalabilityPlots(String sessionId) {

        getData(sessionId);

        return dataForScalabilityPlots;
    }

    public SessionEntity getSessionEntity(String sessionId) {

        getData(sessionId);

        return sessionEntity;
    }

    private void getData(String sessionId) {

        // Remember what session id was set for cashed data
        if (this.sessionId == null) {
            this.sessionId = sessionId;
        }

        // Reset data if new session id arrived
        if (!sessionId.equals(this.sessionId)) {
            sessionEntity = null;
            metricsPerTest = null;
            this.sessionId = sessionId;

            summaryMap.clear();
            latencyPercentilesMap.clear();
            validatorsMap.clear();
            standardMetricsMap.clear();
            testSummaryData.clear();
            dataForScalabilityPlots.clear();
        }

        if (metricsPerTest == null) {

            Set<String> standardMetricsIds = new HashSet<String>();
            standardMetricsIds.addAll(StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.THROUGHPUT_ID));
            standardMetricsIds.addAll(StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.FAIL_COUNT_ID));
            standardMetricsIds.addAll(StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.SUCCESS_RATE_ID));
            standardMetricsIds.addAll(StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.LATENCY_ID));
            standardMetricsIds.addAll(StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID));
            standardMetricsIds.addAll(StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.ITERATION_SAMPLES_ID));

            LocalRankingProvider localRankingProvider = new LocalRankingProvider();
            DataService dataService = new DefaultDataService(databaseService);
            Set<TestEntity> testEntities = dataService.getTests(sessionId);
            sessionEntity = dataService.getSession(sessionId);
            metricsPerTest = dataService.getMetricsByTests(testEntities);

            Set<Long> testIds = new HashSet<Long>();
            for (TestEntity testEntity : testEntities) {
                testIds.add(testEntity.getId());
            }
            numberOfTestGroups = databaseService.getTestGroupIdsByTestIds(testIds).keySet().size();

            for (Map.Entry<TestEntity, Set<MetricEntity>> entry : metricsPerTest.entrySet()) {
                List<SummaryDto> summaryList = new ArrayList<SummaryDto>();
                List<SummaryDto> latencyPercentilesList = new ArrayList<SummaryDto>();
                List<SummaryDto> validatorsList = new ArrayList<SummaryDto>();
                Map<MetricEntity, MetricSummaryValueEntity> standardMetricsPerTest = new HashMap<MetricEntity, MetricSummaryValueEntity>();

                // Metrics
                Map<MetricEntity, MetricSummaryValueEntity> summary = dataService.getMetricSummary(entry.getValue());

                for (MetricEntity metricEntity : summary.keySet()) {
                    SummaryDto value = new SummaryDto();

                    // All summary
                    value.setKey(metricEntity.getDisplayName());

                    MetricSummaryValueEntity metricSummaryValueEntity = summary.get(metricEntity);

                    Double summaryValue = metricSummaryValueEntity.getValue();
                    value.setValue(new DecimalFormat(FormatCalculator.getNumberFormat(summaryValue)).format(summaryValue));
                    if (isMetricHighlighting && metricSummaryValueEntity.getDecision() != null) {
                        value.setDecision(metricSummaryValueEntity.getDecision().toString());
                    }

                    // Validators
                    if (metricEntity.getMetricNameDto().getOrigin().equals(MetricNameDto.Origin.VALIDATOR)) {
                        validatorsList.add(value);
                    }

                    // Latency percentiles
                    if (metricEntity.getMetricId().matches("^" + StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX)) {
                        // change key (name) for back compatibility
                        value.setKey(metricEntity.getDisplayName().replace("Latency ", "").concat("  -  "));
                        latencyPercentilesList.add(value);
                    } else {
                        summaryList.add(value);
                    }

                    // Standard metrics
                    if (standardMetricsIds.contains(metricEntity.getMetricId())) {
                        standardMetricsPerTest.put(metricEntity, summary.get(metricEntity));
                    }
                }

                localRankingProvider.sortSummaryDto(summaryList);
                localRankingProvider.sortSummaryDto(validatorsList);
                localRankingProvider.sortSummaryDto(latencyPercentilesList);

                // Test info
                SummaryDto description = new SummaryDto();
                description.setKey("Test description");
                description.setValue(entry.getKey().getDescription());
                summaryList.add(0, description);

                SummaryDto startTime = new SummaryDto();
                startTime.setKey("Start time");
                startTime.setValue(dateFormatter.format(entry.getKey().getStartDate()));
                summaryList.add(0, startTime);

                SummaryDto termination = new SummaryDto();
                termination.setKey("Termination");
                termination.setValue(entry.getKey().getTerminationStrategy());
                summaryList.add(0, termination);

                SummaryDto load = new SummaryDto();
                load.setKey("Load");
                load.setValue(entry.getKey().getLoad());
                summaryList.add(0, load);

                summaryMap.put(entry.getKey().getId().toString(), summaryList);
                latencyPercentilesMap.put(entry.getKey().getId().toString(), latencyPercentilesList);
                validatorsMap.put(entry.getKey().getId().toString(), validatorsList);
                standardMetricsMap.put(entry.getKey(), standardMetricsPerTest);
            }

            fillTestSummaryAndScalabilityData(this.sessionId);
        }
    }

    private void fillTestSummaryAndScalabilityData(String sessionId) {

        for (TestEntity testEntity : standardMetricsMap.keySet()) {

            dataForScalabilityPlots.put(testEntity, new HashMap<String, Double>());

            //??? todo JFG_777 - decide how to provide session comparison and decision making for back compatibility
            // workaround for back compatibility
            // create dummy workloadTaskData entity for decision maker
            WorkloadTaskData workloadTaskData = new WorkloadTaskData();
            Map<MetricEntity,MetricSummaryValueEntity> metricsForThisTest = standardMetricsMap.get(testEntity);
            for (MetricEntity metricEntity : metricsForThisTest.keySet()) {
                if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.THROUGHPUT_ID).contains(metricEntity.getMetricId())) {
                    workloadTaskData.setThroughput(new BigDecimal(metricsForThisTest.get(metricEntity).getValue()));
                    dataForScalabilityPlots.get(testEntity).put(StandardMetricsNamesUtil.THROUGHPUT_ID, metricsForThisTest.get(metricEntity).getValue());
                }
                if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.FAIL_COUNT_ID).contains(metricEntity.getMetricId())) {
                    workloadTaskData.setFailuresCount(metricsForThisTest.get(metricEntity).getValue().intValue());
                }
                if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.SUCCESS_RATE_ID).contains(metricEntity.getMetricId())) {
                    workloadTaskData.setSuccessRate(new BigDecimal(metricsForThisTest.get(metricEntity).getValue()));
                }
                if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.LATENCY_ID).contains(metricEntity.getMetricId())) {
                    workloadTaskData.setAvgLatency(new BigDecimal(metricsForThisTest.get(metricEntity).getValue()));
                    dataForScalabilityPlots.get(testEntity).put(StandardMetricsNamesUtil.LATENCY_ID, metricsForThisTest.get(metricEntity).getValue());
                }
                if (StandardMetricsNamesUtil.getAllVariantsOfMetricName(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID).contains(metricEntity.getMetricId())) {
                    workloadTaskData.setStdDevLatency(new BigDecimal(metricsForThisTest.get(metricEntity).getValue()));
                    dataForScalabilityPlots.get(testEntity).put(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID, metricsForThisTest.get(metricEntity).getValue());
                }
            }

            String testStatusComment = "";
            Decision testStatus = Decision.OK;

            // Success rate
            Decision testSuccessRateStatus = decisionMaker.decideOnTest(workloadTaskData);
            if (testSuccessRateStatus.ordinal() > testStatus.ordinal()) {
                testStatusComment = "Test status is based on success rate. Success rate is below the threshold defined by the property: 'chassis.master.reporting.successrate.threshold'";
                testStatus = testSuccessRateStatus;
            }

            // Errors during workload configuration
            Decision testExecutionStatus = testEntity.getTestExecutionStatus();
            if (testExecutionStatus.ordinal() > testStatus.ordinal()) {
                testStatusComment = "Test status is based on test execution status. There were errors during test execution (f.e. timeouts)";
                testStatus = testExecutionStatus;
            }

            // Limits based decision
            Decision testDecisionBasedOLimits = testEntity.getDecision();
            if (testDecisionBasedOLimits != null) {
                if (testDecisionBasedOLimits.ordinal() > testStatus.ordinal()) {
                    testStatusComment = "Test status is based on comparison of summary values to limits";
                    testStatus = testDecisionBasedOLimits;
                }

            }

            SummaryTestDto reportData = new SummaryTestDto();
            reportData.setSessionId(sessionId);
            reportData.setNumber(testEntity.getTestGroupIndex().toString());
            reportData.setId(testEntity.getId().toString());
            reportData.setTestStatus(testStatus);
            reportData.setStatusImage(statusImageProvider.getImageByDecision(testStatus));
            reportData.setTestName(testEntity.getName() + "\n\n\n" + testStatusComment);
            testSummaryData.add(reportData);
        }

        Collections.sort(testSummaryData, new Comparator<SummaryTestDto>() {
            @Override
            public int compare(final SummaryTestDto result1, final SummaryTestDto result2) {
                int val1 = Integer.parseInt(result1.getNumber());
                int val2 = Integer.parseInt(result2.getNumber());
                return val1 > val2 ? 1 : val1 < val2 ? -1 : 0;
            }
        } );
    }

    private class LocalRankingProvider extends MetricNamesRankingProvider {
        public void sortSummaryDto(List<SummaryDto> list) {
            Collections.sort(list, new Comparator<SummaryDto>() {
                @Override
                public int compare(SummaryDto o, SummaryDto o2) {
                    return LocalRankingProvider.compare(o.getKey(), o2.getKey());
                }
            });
        }
    }

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

}
