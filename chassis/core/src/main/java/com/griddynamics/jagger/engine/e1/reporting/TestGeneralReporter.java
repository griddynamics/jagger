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

import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.monitoring.MonitoringParameterBean;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.monitoring.reporting.SystemUnderTestPlotsGeneralProvider;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: dkotlyarov
 */
public class TestGeneralReporter extends AbstractReportProvider {
    public static class TestDetailsDTO {
        private String testId;
        private String testName;

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
        }

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }
    }

    @Override
    public JRDataSource getDataSource() {
        SystemUnderTestPlotsGeneralProvider plotsGeneralProvider = (SystemUnderTestPlotsGeneralProvider) getContext().getMappedProvider("sysUTPlotsGeneral");

        List<TestDetailsDTO> result = new ArrayList<TestDetailsDTO>();
        Set<String> boxIdentifiers = plotsGeneralProvider.getStatistics().findBoxIdentifiers();
        Set<String> sutUrls = plotsGeneralProvider.getStatistics().findSutUrls();
        for (GroupKey groupName : plotsGeneralProvider.getPlotGroups().getPlotGroups().keySet()) {
            if (hasGlobalStatistics(plotsGeneralProvider, groupName)) {
                TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
                testDetailsDTO.setTestId(groupName.getUpperName());
                testDetailsDTO.setTestName(groupName.getUpperName());
                result.add(testDetailsDTO);
            }

            for (String boxIdentifier : boxIdentifiers) {
                if (hasBoxStatistics(plotsGeneralProvider, boxIdentifier, groupName)) {
                    String name = groupName.getUpperName() + " on " + boxIdentifier;
                    TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
                    testDetailsDTO.setTestId(name);
                    testDetailsDTO.setTestName(name);
                    result.add(testDetailsDTO);
                }
            }

            for (String sutUrl : sutUrls) {
                if (hasSutStatistics(plotsGeneralProvider, sutUrl, groupName)) {
                    String name = groupName.getUpperName() + " on " + sutUrl;
                    TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
                    testDetailsDTO.setTestId(name);
                    testDetailsDTO.setTestName(name);
                    result.add(testDetailsDTO);
                }
            }
        }

        return new JRBeanCollectionDataSource(result);
    }

    private boolean hasGlobalStatistics(SystemUnderTestPlotsGeneralProvider plotsGeneralProvider, GroupKey groupName) {
        for (MonitoringParameter parameterId : plotsGeneralProvider.getPlotGroups().getPlotGroups().get(groupName)) {
            MonitoringParameterBean param = MonitoringParameterBean.copyOf(parameterId);
            if (plotsGeneralProvider.getStatistics().hasGlobalStatistics(param)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasBoxStatistics(SystemUnderTestPlotsGeneralProvider plotsGeneralProvider, String boxIdentifier, GroupKey groupName) {
        for (MonitoringParameter parameterId : plotsGeneralProvider.getPlotGroups().getPlotGroups().get(groupName)) {
            MonitoringParameterBean param = MonitoringParameterBean.copyOf(parameterId);
            if (plotsGeneralProvider.getStatistics().hasBoxStatistics(param, boxIdentifier)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSutStatistics(SystemUnderTestPlotsGeneralProvider plotsGeneralProvider, String sutUrl, GroupKey groupName) {
        for (MonitoringParameter parameterId : plotsGeneralProvider.getPlotGroups().getPlotGroups().get(groupName)) {
            MonitoringParameterBean param = MonitoringParameterBean.copyOf(parameterId);
            if (plotsGeneralProvider.getStatistics().hasSutStatistics(param, sutUrl)) {
                return true;
            }
        }
        return false;
    }
}
