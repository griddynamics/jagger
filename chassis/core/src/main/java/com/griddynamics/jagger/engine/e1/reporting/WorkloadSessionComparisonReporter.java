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

package com.griddynamics.jagger.engine.e1.reporting;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Verdict;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.WorkloadComparisonResult;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import com.griddynamics.jagger.util.Decision;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkloadSessionComparisonReporter extends AbstractMappedReportProvider<Collection<Verdict<WorkloadComparisonResult>>> {

    public static final Comparator<WorkloadSessionComparisonDto> BY_NAME =
            Comparator.comparing(WorkloadSessionComparisonDto::getName);

    private StatusImageProvider statusImageProvider;

    @Override
    public JRDataSource getDataSource(Collection<Verdict<WorkloadComparisonResult>> key, String sessionId) {
        getContext().getParameters().put("jagger.workloadsessioncomparator.statusImageProvider", statusImageProvider);

        List<WorkloadSessionComparisonDto> result = Lists.newArrayList();

        for (Verdict<WorkloadComparisonResult> verdict : key) {
            WorkloadSessionComparisonDto dto = new WorkloadSessionComparisonDto();

            dto.setName(verdict.getDescription());
            dto.setDecision(verdict.getDecision());

            WorkloadComparisonResult details = verdict.getDetails();

            // null will come in case of errors - f.e. when compared sessions do not match
            if (details != null) {
                dto.setAvgLatencyDeviation(details.getAvgLatencyDeviation());
                dto.setStdDevLatencyDeviation(details.getStdDevLatencyDeviation());
                dto.setThroughputDeviation(details.getThroughputDeviation());
                dto.setTotalDurationDeviation(details.getTotalDurationDeviation());
                dto.setSuccessRateDeviation(details.getSuccessRateDeviation());
            } else {
                dto.setAvgLatencyDeviation(0.0);
                dto.setStdDevLatencyDeviation(0.0);
                dto.setThroughputDeviation(0.0);
                dto.setTotalDurationDeviation(0.0);
                dto.setSuccessRateDeviation(0.0);
            }

            result.add(dto);
        }

        Collections.sort(result, BY_NAME);
        return new JRBeanCollectionDataSource(result);
    }

    @Required
    public void setStatusImageProvider(StatusImageProvider statusImageProvider) {
        this.statusImageProvider = statusImageProvider;
    }

    public static class WorkloadSessionComparisonDto {

        private String name;
        private Decision decision;
        private double throughputDeviation;
        /**
         * @deprecated We don't show a total duration in the WebUI and a report, but we decided to keep a total duration deviation for a session's comparison.
         *             Afterwords, we should remove it.
         */
        @Deprecated
        private double totalDurationDeviation;
        private double successRateDeviation;
        private double avgLatencyDeviation;
        private double stdDevLatencyDeviation;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Decision getDecision() {
            return decision;
        }

        public void setDecision(Decision decision) {
            this.decision = decision;
        }

        public double getThroughputDeviation() {
            return throughputDeviation;
        }

        public void setThroughputDeviation(double throughputDeviation) {
            this.throughputDeviation = throughputDeviation;
        }

        /**
         * @deprecated we don't show a total duration in the WebUI and a report
         */
        @Deprecated
        public double getTotalDurationDeviation() {
            return totalDurationDeviation;
        }

        /**
         * @deprecated we don't show a total duration in the WebUI and a report
         */
        @Deprecated
        public void setTotalDurationDeviation(double totalDurationDeviation) {
            this.totalDurationDeviation = totalDurationDeviation;
        }

        public double getSuccessRateDeviation() {
            return successRateDeviation;
        }

        public void setSuccessRateDeviation(double successRateDeviation) {
            this.successRateDeviation = successRateDeviation;
        }

        public double getAvgLatencyDeviation() {
            return avgLatencyDeviation;
        }

        public void setAvgLatencyDeviation(double avgLatencyDeviation) {
            this.avgLatencyDeviation = avgLatencyDeviation;
        }

        public double getStdDevLatencyDeviation() {
            return stdDevLatencyDeviation;
        }

        public void setStdDevLatencyDeviation(double stdDevLatencyDeviation) {
            this.stdDevLatencyDeviation = stdDevLatencyDeviation;
        }
    }
}

