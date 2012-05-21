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

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.ArrayList;
import java.util.List;

public class TestDetailsReporter extends AbstractReportProvider {
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
        @SuppressWarnings("unchecked")
        List<WorkloadTaskData> tests = getHibernateTemplate().find("from WorkloadTaskData d where d.sessionId=? order by d.number asc, d.scenario.name asc",
				getSessionIdProvider().getSessionId());

        List<TestDetailsDTO> result = new ArrayList<TestDetailsDTO>();
        for(WorkloadTaskData test : tests) {
            TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
            testDetailsDTO.setTestId(test.getTaskId());
            testDetailsDTO.setTestName(getTestHumanReadableName(test));
            result.add(testDetailsDTO);
        }

        return new JRBeanCollectionDataSource(result);
    }

    private static String getTestHumanReadableName(WorkloadTaskData workloadTaskData) {
        return workloadTaskData.getNumber() + ") " + workloadTaskData.getScenario().getName() + ", " + workloadTaskData.getClock();
    }
}
