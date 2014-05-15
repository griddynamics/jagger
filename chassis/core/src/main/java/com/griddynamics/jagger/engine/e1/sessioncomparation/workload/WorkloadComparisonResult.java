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

import com.griddynamics.jagger.dbapi.entity.WorkloadTaskData;

public class WorkloadComparisonResult {
    private final WorkloadTaskData currentData;
    private final WorkloadTaskData baselineData;

    private final double throughputDeviation;
    /**
     * @deprecated we don't show a total duration in the WebUI and a report, but we decided to keep a total duration deviation for a comparison.
     *             Afterwords, we should remove it.
     */
    @Deprecated
    private final double totalDurationDeviation;
    private final double successRateDeviation;
    private final double avgLatencyDeviation;
    private final double stdDevLatencyDeviation;

    public static WorkloadComparisonResultBuilder builder() {
        return new WorkloadComparisonResultBuilder();
    }

    private WorkloadComparisonResult(WorkloadTaskData currentData, WorkloadTaskData baselineData,
                                     double throughputDeviation, double totalDurationDeviation,
                                     double successRateDeviation, double avgLatencyDeviation, double stdDevLatencyDeviation) {
        this.currentData = currentData;
        this.baselineData = baselineData;
        this.throughputDeviation = throughputDeviation;
        this.totalDurationDeviation = totalDurationDeviation;
        this.successRateDeviation = successRateDeviation;
        this.avgLatencyDeviation = avgLatencyDeviation;
        this.stdDevLatencyDeviation = stdDevLatencyDeviation;
    }

    public double getThroughputDeviation() {
        return throughputDeviation;
    }

    /**
     * @deprecated we don't show a total duration in the WebUI and a report, but we decided to keep a total duration deviation for a comparison.
     *             Afterwords, we should remove it.
     */
    @Deprecated
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

    public WorkloadTaskData getCurrentData() {
        return currentData;
    }

    public WorkloadTaskData getBaselineData() {
        return baselineData;
    }

    @Override
    public String toString() {
        return "WorkloadComparisonResult{" +
                "currentData=" + currentData +
                ", baselineData=" + baselineData +
                ", throughputDeviation=" + throughputDeviation +
                ", totalDurationDeviation=" + totalDurationDeviation +
                ", successRateDeviation=" + successRateDeviation +
                ", avgLatencyDeviation=" + avgLatencyDeviation +
                ", stdDevLatencyDeviation=" + stdDevLatencyDeviation +
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
        if (baselineData != null ? !baselineData.equals(that.baselineData) : that.baselineData != null) return false;
        if (currentData != null ? !currentData.equals(that.currentData) : that.currentData != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = currentData != null ? currentData.hashCode() : 0;
        result = 31 * result + (baselineData != null ? baselineData.hashCode() : 0);
        temp = throughputDeviation != +0.0d ? Double.doubleToLongBits(throughputDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = totalDurationDeviation != +0.0d ? Double.doubleToLongBits(totalDurationDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = successRateDeviation != +0.0d ? Double.doubleToLongBits(successRateDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = avgLatencyDeviation != +0.0d ? Double.doubleToLongBits(avgLatencyDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = stdDevLatencyDeviation != +0.0d ? Double.doubleToLongBits(stdDevLatencyDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static class WorkloadComparisonResultBuilder {
        private double throughputDeviation;
        @Deprecated
        /**
         * @deprecated
         * We don't show a total duration in the WebUI and a report, but we decided to keep a total duration deviation for a comparison.
         * Afterwords, we should remove it.
         */
        private double totalDurationDeviation;
        private double successRateDeviation;
        private double avgLatencyDeviation;
        private double stdDevLatencyDeviation;
        private WorkloadTaskData currentData;
        private WorkloadTaskData baselineData;

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

        public WorkloadComparisonResultBuilder baselineData(WorkloadTaskData baselineData) {
            this.baselineData = baselineData;
            return this;
        }

        public WorkloadComparisonResultBuilder currentData(WorkloadTaskData currentData) {
            this.currentData = currentData;
            return this;
        }

        public WorkloadComparisonResultBuilder stdDevLatencyDeviation(double stdDevLatencyDeviation) {
            this.stdDevLatencyDeviation = stdDevLatencyDeviation;
            return this;
        }

        public WorkloadComparisonResult build() {
            return new WorkloadComparisonResult(currentData, baselineData,
                    throughputDeviation, totalDurationDeviation,
                    successRateDeviation, avgLatencyDeviation, stdDevLatencyDeviation);
        }
    }

}
