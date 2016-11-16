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
package com.griddynamics.jagger.engine.e1.sessioncomparation.workload;

import com.griddynamics.jagger.dbapi.entity.WorkloadDetails;
import com.griddynamics.jagger.dbapi.entity.WorkloadTaskData;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class WorkloadComparisonResult {

    private final double throughputDeviation;
    @Deprecated
    /** @deprecated starting from version 1.2.6 this parameter is always zero */
    private final double totalDurationDeviation;
    private final double successRateDeviation;
    private final double avgLatencyDeviation;
    private final double stdDevLatencyDeviation;

    private final SessionEntity currentSessionEntity;
    private final SessionEntity baselineSessionEntity;
    private final TestEntity currentTestEntity;
    private final TestEntity baselineTestEntity;
    private final Map<MetricEntity, MetricSummaryValueEntity> currentStandardMetrics;
    private final Map<MetricEntity, MetricSummaryValueEntity> baselineStandardMetrics;


    public static WorkloadComparisonResultBuilder builder() {
        return new WorkloadComparisonResultBuilder();
    }

    public WorkloadComparisonResult(double throughputDeviation, double totalDurationDeviation, double successRateDeviation,
                                    double avgLatencyDeviation, double stdDevLatencyDeviation,
                                    SessionEntity currentSessionEntity, SessionEntity baselineSessionEntity,
                                    TestEntity currentTestEntity, TestEntity baselineTestEntity,
                                    Map<MetricEntity, MetricSummaryValueEntity> currentStandardMetrics, Map<MetricEntity, MetricSummaryValueEntity> baselineStandardMetrics) {
        this.throughputDeviation = throughputDeviation;
        this.totalDurationDeviation = totalDurationDeviation;
        this.successRateDeviation = successRateDeviation;
        this.avgLatencyDeviation = avgLatencyDeviation;
        this.stdDevLatencyDeviation = stdDevLatencyDeviation;
        this.currentSessionEntity = currentSessionEntity;
        this.baselineSessionEntity = baselineSessionEntity;
        this.currentTestEntity = currentTestEntity;
        this.baselineTestEntity = baselineTestEntity;
        this.currentStandardMetrics = currentStandardMetrics;
        this.baselineStandardMetrics = baselineStandardMetrics;
    }


    public double getThroughputDeviation() {
        return throughputDeviation;
    }

    @Deprecated
    /** @deprecated starting from version 1.2.6 this function will always return zero */
    public double getTotalDurationDeviation() {
        return totalDurationDeviation;
    }

    public double getSuccessRateDeviation() {
        return successRateDeviation;
    }

    public double getAvgLatencyDeviation() {
        return avgLatencyDeviation;
    }

    public double getStdDevLatencyDeviation() {
        return stdDevLatencyDeviation;
    }

    @Deprecated
    /** @deprecated Use @ref getCurrentSessionEntity, @ref getCurrentTestEntity, @ref getCurrentStandardMetrics instead */
    public WorkloadTaskData getCurrentData() {
        return createDummyWorkloadTaskData(currentSessionEntity,currentTestEntity,currentStandardMetrics);
    }

    @Deprecated
    /** @deprecated Use @ref getBaselineSessionEntity, @ref getBaselineTestEntity, @ref getBaselineStandardMetrics instead */
    public WorkloadTaskData getBaselineData() {
        return createDummyWorkloadTaskData(baselineSessionEntity,baselineTestEntity,baselineStandardMetrics);
    }


    public SessionEntity getCurrentSessionEntity() {
        return currentSessionEntity;
    }

    public SessionEntity getBaselineSessionEntity() {
        return baselineSessionEntity;
    }

    public TestEntity getCurrentTestEntity() {
        return currentTestEntity;
    }

    public TestEntity getBaselineTestEntity() {
        return baselineTestEntity;
    }

    public Map<MetricEntity, MetricSummaryValueEntity> getCurrentStandardMetrics() {
        return currentStandardMetrics;
    }

    public Map<MetricEntity, MetricSummaryValueEntity> getBaselineStandardMetrics() {
        return baselineStandardMetrics;
    }

    /** Support function to get value for particular standard metrics from the map of standard metrics returned by @n
     * @ref getCurrentStandardMetrics or @ref getBaselineStandardMetrics function @n
     * @author Dmitry Latnikov
     * @n
     * @param standardMetricId - standard metric id. Possible values: @n
     *                         StandardMetricsNamesUtil.THROUGHPUT_ID @n
     *                         StandardMetricsNamesUtil.LATENCY_ID @n
     *                         StandardMetricsNamesUtil.LATENCY_STD_DEV_ID @n
     *                         StandardMetricsNamesUtil.SUCCESS_RATE_ID @n
     *                         StandardMetricsNamesUtil.FAIL_COUNT_ID @n
     *                         StandardMetricsNamesUtil.ITERATION_SAMPLES_ID @n
     * @param standardMetricsMap - map of standard metrics, returned by @ref getCurrentStandardMetrics or @ref getBaselineStandardMetrics function @n
     *
     * @return value of standard metric or null if not found */
    public MetricSummaryValueEntity getStandardMetricValueById (String standardMetricId, Map<MetricEntity, MetricSummaryValueEntity> standardMetricsMap) {
        Set<String> allMetricNames = StandardMetricsNamesUtil.getAllVariantsOfMetricName(standardMetricId);

        for (MetricEntity metricEntity : standardMetricsMap.keySet()) {
            if (allMetricNames.contains(metricEntity.getMetricId())) {
                return standardMetricsMap.get(metricEntity);
            }
        }

        return null;
    }

    // function for back compatibility
    private WorkloadTaskData createDummyWorkloadTaskData(SessionEntity sessionEntity,
                                                         TestEntity testEntity,
                                                         Map<MetricEntity, MetricSummaryValueEntity> standardMetricsMap) {

        WorkloadDetails workloadDetails = new WorkloadDetails();
        workloadDetails.setComment(null);
        workloadDetails.setDescription(testEntity.getDescription());
        workloadDetails.setId(0L);  // dummy value
        workloadDetails.setName(testEntity.getName());
        workloadDetails.setVersion("1");

        WorkloadTaskData workloadTaskData = new WorkloadTaskData();
        workloadTaskData.setId(0L);  // dummy value
        workloadTaskData.setSessionId(sessionEntity.getId());
        workloadTaskData.setTaskId("0");    // dummy value
        workloadTaskData.setNumber(testEntity.getTestGroupIndex());
        workloadTaskData.setScenario(workloadDetails);
        Double samples = getStandardMetricValueById(StandardMetricsNamesUtil.ITERATION_SAMPLES_ID,standardMetricsMap).getValue();
        if (samples != null) {
            workloadTaskData.setSamples(samples.intValue());
        } else {
            workloadTaskData.setSamples(0);
        }
        workloadTaskData.setClock(testEntity.getLoad());
        workloadTaskData.setClockValue(testEntity.getClockValue());
        workloadTaskData.setTermination(testEntity.getTerminationStrategy());
        workloadTaskData.setKernels(sessionEntity.getKernels());
        workloadTaskData.setTotalDuration(new BigDecimal(0));
        Double throughput = getStandardMetricValueById(StandardMetricsNamesUtil.THROUGHPUT_ID,standardMetricsMap).getValue();
        if (throughput != null) {
            workloadTaskData.setThroughput(new BigDecimal(throughput));
        } else {
            workloadTaskData.setThroughput(new BigDecimal(0));
        }
        Double failuresCount = getStandardMetricValueById(StandardMetricsNamesUtil.FAIL_COUNT_ID,standardMetricsMap).getValue();
        if (failuresCount != null) {
            workloadTaskData.setFailuresCount(failuresCount.intValue());
        } else {
            workloadTaskData.setFailuresCount(0);
        }
        Double successRate = getStandardMetricValueById(StandardMetricsNamesUtil.SUCCESS_RATE_ID,standardMetricsMap).getValue();
        if (successRate != null) {
            workloadTaskData.setSuccessRate(new BigDecimal(successRate));
        } else {
            workloadTaskData.setSuccessRate(new BigDecimal(0));
        }
        Double avgLatency =  getStandardMetricValueById(StandardMetricsNamesUtil.LATENCY_ID,standardMetricsMap).getValue();
        if (avgLatency != null) {
            workloadTaskData.setAvgLatency(new BigDecimal(avgLatency));
        } else {
            workloadTaskData.setAvgLatency(new BigDecimal(0));
        }
        Double stdDevLatency = getStandardMetricValueById(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID,standardMetricsMap).getValue();
        if (stdDevLatency != null) {
            workloadTaskData.setStdDevLatency(new BigDecimal(stdDevLatency));
        } else {
            workloadTaskData.setStdDevLatency(new BigDecimal(0));
        }

        return workloadTaskData;
    }


    @Override
    public String toString() {
        return "WorkloadComparisonResult{" +
                ", throughputDeviation=" + throughputDeviation +
                ", totalDurationDeviation=" + totalDurationDeviation +
                ", successRateDeviation=" + successRateDeviation +
                ", avgLatencyDeviation=" + avgLatencyDeviation +
                ", stdDevLatencyDeviation=" + stdDevLatencyDeviation +
                ", currentSessionEntity=" + currentSessionEntity +
                ", baselineSessionEntity=" + baselineSessionEntity +
                ", currentTestEntity=" + currentTestEntity +
                ", baselineTestEntity=" + baselineTestEntity +
                ", currentStandardMetrics=" + currentStandardMetrics +
                ", baselineStandardMetrics=" + baselineStandardMetrics +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkloadComparisonResult that = (WorkloadComparisonResult) o;

        if (Double.compare(that.avgLatencyDeviation, avgLatencyDeviation) != 0) return false;
        if (Double.compare(that.stdDevLatencyDeviation, stdDevLatencyDeviation) != 0) return false;
        if (Double.compare(that.successRateDeviation, successRateDeviation) != 0) return false;
        if (Double.compare(that.throughputDeviation, throughputDeviation) != 0) return false;
        if (Double.compare(that.totalDurationDeviation, totalDurationDeviation) != 0) return false;
        if (baselineSessionEntity != null ? !baselineSessionEntity.equals(that.baselineSessionEntity) : that.baselineSessionEntity != null)
            return false;
        if (baselineStandardMetrics != null ? !baselineStandardMetrics.equals(that.baselineStandardMetrics) : that.baselineStandardMetrics != null)
            return false;
        if (baselineTestEntity != null ? !baselineTestEntity.equals(that.baselineTestEntity) : that.baselineTestEntity != null)
            return false;
        if (currentSessionEntity != null ? !currentSessionEntity.equals(that.currentSessionEntity) : that.currentSessionEntity != null)
            return false;
        if (currentStandardMetrics != null ? !currentStandardMetrics.equals(that.currentStandardMetrics) : that.currentStandardMetrics != null)
            return false;
        if (currentTestEntity != null ? !currentTestEntity.equals(that.currentTestEntity) : that.currentTestEntity != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(throughputDeviation);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalDurationDeviation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(successRateDeviation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(avgLatencyDeviation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stdDevLatencyDeviation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (currentSessionEntity != null ? currentSessionEntity.hashCode() : 0);
        result = 31 * result + (baselineSessionEntity != null ? baselineSessionEntity.hashCode() : 0);
        result = 31 * result + (currentTestEntity != null ? currentTestEntity.hashCode() : 0);
        result = 31 * result + (baselineTestEntity != null ? baselineTestEntity.hashCode() : 0);
        result = 31 * result + (currentStandardMetrics != null ? currentStandardMetrics.hashCode() : 0);
        result = 31 * result + (baselineStandardMetrics != null ? baselineStandardMetrics.hashCode() : 0);
        return result;
    }

    public static class WorkloadComparisonResultBuilder {
        private double throughputDeviation;
        @Deprecated
        /** @deprecated starting from version 1.2.6 this parameter is always zero */
        private double totalDurationDeviation;
        private double successRateDeviation;
        private double avgLatencyDeviation;
        private double stdDevLatencyDeviation;

        private SessionEntity currentSessionEntity;
        private SessionEntity baselineSessionEntity;
        private TestEntity currentTestEntity;
        private TestEntity baselineTestEntity;
        private Map<MetricEntity, MetricSummaryValueEntity> currentStandardMetrics;
        private Map<MetricEntity, MetricSummaryValueEntity> baselineStandardMetrics;

        private WorkloadComparisonResultBuilder() {

        }

        public WorkloadComparisonResultBuilder throughputDeviation(double throughputDeviation) {
            this.throughputDeviation = throughputDeviation;
            return this;
        }

        @Deprecated
        public WorkloadComparisonResultBuilder totalDurationDeviation(double totalDurationDeviation) {
            this.totalDurationDeviation = totalDurationDeviation;
            return this;
        }

        public WorkloadComparisonResultBuilder successRateDeviation(double successRateDeviation) {
            this.successRateDeviation = successRateDeviation;
            return this;
        }

        public WorkloadComparisonResultBuilder avgLatencyDeviation(double avgLatencyDeviation) {
            this.avgLatencyDeviation = avgLatencyDeviation;
            return this;
        }

        public WorkloadComparisonResultBuilder stdDevLatencyDeviation(double stdDevLatencyDeviation) {
            this.stdDevLatencyDeviation = stdDevLatencyDeviation;
            return this;
        }

        public WorkloadComparisonResultBuilder currentSessionEntity(SessionEntity currentSessionEntity) {
            this.currentSessionEntity = currentSessionEntity;
            return this;
        }

        public WorkloadComparisonResultBuilder baselineSessionEntity(SessionEntity baselineSessionEntity) {
            this.baselineSessionEntity = baselineSessionEntity;
            return this;
        }

        public WorkloadComparisonResultBuilder currentTestEntity(TestEntity currentTestEntity) {
            this.currentTestEntity = currentTestEntity;
            return this;
        }

        public WorkloadComparisonResultBuilder baselineTestEntity(TestEntity baselineTestEntity) {
            this.baselineTestEntity = baselineTestEntity;
            return this;
        }

        public WorkloadComparisonResultBuilder currentStandardMetrics(Map<MetricEntity, MetricSummaryValueEntity> currentStandardMetrics) {
            this.currentStandardMetrics = currentStandardMetrics;
            return this;
        }

        public WorkloadComparisonResultBuilder baselineStandardMetrics(Map<MetricEntity, MetricSummaryValueEntity> baselineStandardMetrics) {
            this.baselineStandardMetrics = baselineStandardMetrics;
            return this;
        }


        public WorkloadComparisonResult build() {
            return new WorkloadComparisonResult(throughputDeviation, totalDurationDeviation,
                    successRateDeviation, avgLatencyDeviation, stdDevLatencyDeviation,
                    currentSessionEntity, baselineSessionEntity,
                    currentTestEntity, baselineTestEntity,
                    currentStandardMetrics, baselineStandardMetrics);
        }

    }

}
