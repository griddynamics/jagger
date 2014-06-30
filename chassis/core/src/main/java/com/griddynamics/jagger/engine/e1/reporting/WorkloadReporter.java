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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.dbapi.entity.WorkloadTaskData;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Required;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class WorkloadReporter extends AbstractReportProvider {
    private SummaryReporter summaryReporter;
    private SessionStatusDecisionMaker decisionMaker;
    private StatusImageProvider statusImageProvider;

	@Override
	public JRDataSource getDataSource() {
        String sessionId = getSessionIdProvider().getSessionId();
        Map<TestEntity, Map<MetricEntity,MetricSummaryValueEntity>> metricsPerTest =
                summaryReporter.getStandardMetricsPerTest(sessionId);

        List<E1ScenarioReportData> result = Lists.newLinkedList();

        for (TestEntity testEntity : metricsPerTest.keySet()) {
			E1ScenarioReportData reportData = new E1ScenarioReportData();
			reportData.setSessionId(sessionId);
            reportData.setNumber(testEntity.getTestGroupIndex().toString());
            reportData.setId(testEntity.getId().toString());

            //??? todo JFG_777 - decide how to provide session comparison and decision making for back compatibility
            // workaround for back compatibility
            // create dummy workloadTaskData entity for decision maker
            WorkloadTaskData workloadTaskData = new WorkloadTaskData();
            Map<MetricEntity,MetricSummaryValueEntity> metricsForThisTest = metricsPerTest.get(testEntity);
            for (MetricEntity metricEntity : metricsForThisTest.keySet()) {
                if (metricEntity.getMetricId().equals(StandardMetricsNamesUtil.THROUGHPUT_ID)) {
                    workloadTaskData.setThroughput(new BigDecimal(metricsForThisTest.get(metricEntity).getValue()));
                }
                if (metricEntity.getMetricId().equals(StandardMetricsNamesUtil.FAIL_COUNT_ID)) {
                    workloadTaskData.setFailuresCount(metricsForThisTest.get(metricEntity).getValue().intValue());
                }
                if (metricEntity.getMetricId().equals(StandardMetricsNamesUtil.SUCCESS_RATE_ID)) {
                    workloadTaskData.setSuccessRate(new BigDecimal(metricsForThisTest.get(metricEntity).getValue()));
                }
                if (metricEntity.getMetricId().equals(StandardMetricsNamesUtil.LATENCY_ID)) {
                    workloadTaskData.setAvgLatency(new BigDecimal(metricsForThisTest.get(metricEntity).getValue()));
                }
                if (metricEntity.getMetricId().equals(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID)) {
                    workloadTaskData.setStdDevLatency(new BigDecimal(metricsForThisTest.get(metricEntity).getValue()));
                }
            }

            String testStatusComment = "";
            Decision testStatus = Decision.OK;

            // Success rate
            Decision testSuccessRateStatus = decisionMaker.decideOnTest(workloadTaskData);
            if (testSuccessRateStatus.ordinal() > testStatus.ordinal()) {
                testStatusComment = "Status is based on success rate. Success rate is below threshold defined by property: 'chassis.master.reporting.successrate.threshold'";
                testStatus = testSuccessRateStatus;
            }

            // Errors during workload configuration
            Decision testExecutionStatus = testEntity.getTestExecutionStatus();
            if (testExecutionStatus.ordinal() > testStatus.ordinal()) {
                testStatusComment = "Status is based on test execution status. There were errors during test execution (f.e. timeouts)";
                testStatus = testExecutionStatus;
            }

            // Limits based decision
            Decision testDecisionBasedOLimits = testEntity.getDecision();
            if (testDecisionBasedOLimits != null) {
                if (testDecisionBasedOLimits.ordinal() > testStatus.ordinal()) {
                    testStatusComment = "Status is based on comparison of summary values to limits";
                    testStatus = testDecisionBasedOLimits;
                }

            }

            reportData.setStatusImage(statusImageProvider.getImageByDecision(testStatus));
            reportData.setScenarioName(testEntity.getName() + "\n\n\n" + testStatusComment);
            result.add(reportData);
		}

        Collections.sort(result, new Comparator<E1ScenarioReportData>() {
            @Override
            public int compare(final E1ScenarioReportData result1, final E1ScenarioReportData result2) {
                int val1 = Integer.parseInt(result1.getNumber());
                int val2 = Integer.parseInt(result2.getNumber());
                return val1 > val2 ? 1 : val1 < val2 ? -1 : 0;
            }
        } );

        return new JRBeanCollectionDataSource(result);
	}

    @Required
    public void setDecisionMaker(SessionStatusDecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    public void setStatusImageProvider(StatusImageProvider statusImageProvider) {
        this.statusImageProvider = statusImageProvider;
    }

    public static class E1ScenarioReportData {
		private String sessionId;
        private String number;
		private String scenarioName;
        private String Id;

        private Image statusImage;

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getScenarioName() {
			return scenarioName;
		}

		public void setScenarioName(String scenarioName) {
			this.scenarioName = scenarioName;
		}

        public Image getStatusImage() {
            return statusImage;
        }

        public void setStatusImage(Image statusImage) {
            this.statusImage = statusImage;
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }
    }

    @Required
    public void setSummaryReporter(SummaryReporter summaryReporter) {
        this.summaryReporter = summaryReporter;
    }

}
