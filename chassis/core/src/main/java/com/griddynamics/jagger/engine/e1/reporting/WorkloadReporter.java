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
import com.google.common.collect.Maps;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessDescriptiveStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import com.griddynamics.jagger.util.TimeUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WorkloadReporter extends AbstractReportProvider {
    private SessionStatusDecisionMaker decisionMaker;
    private StatusImageProvider statusImageProvider;
    private final static Logger log = LoggerFactory.getLogger(WorkloadReporter.class);

	@Override
	public JRDataSource getDataSource() {
		@SuppressWarnings("unchecked")
		List<WorkloadData> testData = getHibernateTemplate().find("from WorkloadData d where d.sessionId=? order by d.number asc, d.scenario.name asc",
                getSessionIdProvider().getSessionId());

		@SuppressWarnings("unchecked")
		List<WorkloadTaskData> allWorkloadTasks = getHibernateTemplate().find("from WorkloadTaskData d where d.sessionId=? order by d.number asc, d.scenario.name asc",
                getSessionIdProvider().getSessionId());

        @SuppressWarnings({"unchecked"}) List<WorkloadProcessDescriptiveStatistics> statistics = getHibernateTemplate().find(
                "select s from WorkloadProcessDescriptiveStatistics s where s.taskData.sessionId=?",
                getSessionIdProvider().getSessionId());

        @SuppressWarnings({"unchecked"}) List<TaskData> taskDatas = getHibernateTemplate().find(
                "select d from TaskData d where d.sessionId=?",
                getSessionIdProvider().getSessionId());

        Map<String, WorkloadProcessDescriptiveStatistics> statisticsByTasks = Maps.newHashMap();
        if (statistics != null) {
            for (WorkloadProcessDescriptiveStatistics statistic : statistics) {
                statisticsByTasks.put(statistic.getTaskData().getTaskId(), statistic);
            }
        }


		List<E1ScenarioReportData> result = Lists.newLinkedList();

		for (WorkloadData workloadData : testData) {
			E1ScenarioReportData reportData = new E1ScenarioReportData();
			reportData.setSessionId(workloadData.getSessionId());
            reportData.setTestId(workloadData.getTaskId());
            reportData.setNumber(workloadData.getNumber().toString());
			reportData.setScenarioName(workloadData.getScenario().getName());
			reportData.setVersion(workloadData.getScenario().getVersion());
			BigDecimal duration = new BigDecimal(workloadData.getEndTime().getTime()
					- workloadData.getStartTime().getTime()).divide(new BigDecimal(1000));
			reportData.setDuration(TimeUtils.formatDuration(duration));

            reportData.setStartTime(workloadData.getStartTime());

			WorkloadTaskData resultData = null;
			for (WorkloadTaskData workloadTaskData : allWorkloadTasks) {
				if (workloadTaskData.getTaskId().equals(workloadData.getTaskId())) {
					resultData = workloadTaskData;
					break;
				}
			}

			if (resultData == null) {
				throw new IllegalStateException("Result data is not specified");
			}

			reportData.setSamples(resultData.getSamples());
			reportData.setClock(resultData.getClock());
			reportData.setTermination(resultData.getTermination());
			reportData.setTotalDuration(resultData.getTotalDuration());
			reportData.setThroughput(resultData.getThroughput());
			reportData.setFailuresCount(resultData.getFailuresCount());
			reportData.setSuccessRate(resultData.getSuccessRate());
			reportData.setAvgLatency(resultData.getAvgLatency());
			reportData.setStdDevLatency(resultData.getStdDevLatency());
            reportData.setLatency85(getLatency85(statisticsByTasks, workloadData.getTaskId()));

            reportData.setStatusImage(statusImageProvider.getImageByDecision(decisionMaker.decideOnTest(resultData)));

            for(TaskData taskData: taskDatas) {
                if(workloadData.getTaskId().equals(taskData.getTaskId())) {
                    if(TaskData.ExecutionStatus.FAILED.equals(taskData.getStatus())) {
                        reportData.setStatusImage(statusImageProvider.getImageByDecision(Decision.ERROR));
                    }
                }
            }


			result.add(reportData);
		}

		return new JRBeanCollectionDataSource(result);
	}

    private String getLatency85(Map<String, WorkloadProcessDescriptiveStatistics> statisticsByTasks, String taskId) {
        WorkloadProcessDescriptiveStatistics statistics = statisticsByTasks.get(taskId);
        String ret = "Unknown";
        if (statistics != null && statistics.getPercentiles() != null) {
            for (WorkloadProcessLatencyPercentile workloadProcessLatencyPercentile : statistics.getPercentiles()) {
                if (workloadProcessLatencyPercentile.getPercentileKey().equals(85D)) {
                    ret = String.format("%.3fs", workloadProcessLatencyPercentile.getPercentileValue() / 1000);
                    break;
                }
            }
        } else {
            log.warn("Statistics unavailable for task {}", taskId);
        }
        return ret;
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
        private String testId;
        private String number;
		private String scenarioName;
		private String version;
		private String clock;
        private String termination;
        private Integer samples;
        private String duration;
        private Date startTime;
		private BigDecimal totalDuration;
		private BigDecimal throughput;
        private String latency85;
		private Integer failuresCount;
		private BigDecimal successRate;
		private BigDecimal avgLatency;
		private BigDecimal stdDevLatency;

        private Image statusImage;

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
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

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}


		public String getDuration() {
			return duration;
		}

		public void setDuration(String duration) {
			this.duration = duration;
		}

		public Integer getSamples() {
			return samples;
		}

		public void setSamples(Integer samples) {
			this.samples = samples;
		}

		public BigDecimal getTotalDuration() {
			return totalDuration;
		}

		public void setTotalDuration(BigDecimal totalDuration) {
			this.totalDuration = totalDuration;
		}

		public BigDecimal getThroughput() {
			return throughput;
		}

		public void setThroughput(BigDecimal throughput) {
			this.throughput = throughput;
		}

		public Integer getFailuresCount() {
			return failuresCount;
		}

		public void setFailuresCount(Integer failuresCount) {
			this.failuresCount = failuresCount;
		}

		public BigDecimal getSuccessRate() {
			return successRate;
		}

		public void setSuccessRate(BigDecimal successRate) {
			this.successRate = successRate;
		}

		public BigDecimal getAvgLatency() {
			return avgLatency;
		}

		public void setAvgLatency(BigDecimal avgLatency) {
			this.avgLatency = avgLatency;
		}

		public BigDecimal getStdDevLatency() {
			return stdDevLatency;
		}

		public void setStdDevLatency(BigDecimal stdDevLatency) {
			this.stdDevLatency = stdDevLatency;
		}

        public Image getStatusImage() {
            return statusImage;
        }

        public void setStatusImage(Image statusImage) {
            this.statusImage = statusImage;
        }

        public String getClock() {
            return clock;
        }

        public void setClock(String clock) {
            this.clock = clock;
        }

        public String getTermination() {
            return termination;
        }

        public void setTermination(String termination) {
            this.termination = termination;
        }

        public String getLatency85() {
            return latency85;
        }

        public void setLatency85(String latency85) {
            this.latency85 = latency85;
        }
    }
}
