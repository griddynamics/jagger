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

import com.griddynamics.jagger.engine.e1.sessioncomparation.Verdict;
import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.MonitoringParameterComparison;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import com.griddynamics.jagger.util.Decision;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Deprecated
public class MonitoringSessionComparisonReporter extends AbstractMappedReportProvider<Collection<Verdict<MonitoringParameterComparison>>> {

    private StatusImageProvider statusImageProvider;

    @Override
    @Deprecated
    public JRDataSource getDataSource(Collection<Verdict<MonitoringParameterComparison>> key) {
        getContext().getParameters().put("jagger.monitoringsessioncomparator.statusImageProvider", statusImageProvider);

        ArrayList<MonitoringComparisonDto> result = Lists.newArrayList();
        for (Verdict<MonitoringParameterComparison> verdict : key) {
            MonitoringComparisonDto dto = new MonitoringComparisonDto();
            dto.setName(verdict.getDescription());
            dto.setDecision(verdict.getDecision());
            dto.setFirstStdDev(verdict.getDetails().getFirst().getSttDev());
            dto.setSecondStdDev(verdict.getDetails().getSecond().getSttDev());
            result.add(dto);
        }
        Collections.sort(result);

        return new JRBeanCollectionDataSource(result);
    }

    @Required
    public void setStatusImageProvider(StatusImageProvider statusImageProvider) {
        this.statusImageProvider = statusImageProvider;
    }

    public static class MonitoringComparisonDto implements Comparable<MonitoringComparisonDto> {
        private String name;
        private Decision decision;
        private double firstStdDev;
        private double secondStdDev;


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

        public double getFirstStdDev() {
            return firstStdDev;
        }

        public void setFirstStdDev(double firstStdDev) {
            this.firstStdDev = firstStdDev;
        }

        public double getSecondStdDev() {
            return secondStdDev;
        }

        public void setSecondStdDev(double secondStdDev) {
            this.secondStdDev = secondStdDev;
        }

        @Override
        public int compareTo(MonitoringComparisonDto dto) {
            return name.compareTo(dto.name);
        }
    }
}
