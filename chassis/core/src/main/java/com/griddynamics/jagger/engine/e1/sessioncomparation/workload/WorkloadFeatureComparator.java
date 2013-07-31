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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.FeatureComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Verdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

import static com.griddynamics.jagger.engine.e1.sessioncomparation.ComparisonUtil.calculateDeviation;

public class WorkloadFeatureComparator extends HibernateDaoSupport implements FeatureComparator<WorkloadComparisonResult> {
    private static final Logger log = LoggerFactory.getLogger(WorkloadFeatureComparator.class);

    private WorkloadDecisionMaker workloadDecisionMaker;


    @Override
    public List<Verdict<WorkloadComparisonResult>> compare(String currentSession, String baselineSession) {

        log.debug("Going to compare workloads for sessions {} {}", currentSession, baselineSession);
        List<WorkloadTaskData> currentData = getAllWorkloadTasks(currentSession);
        List<WorkloadTaskData> baselineData = getAllWorkloadTasks(baselineSession);

        log.debug("current session {} workloads", currentData.size());
        log.debug("baseline session {} workloads", baselineData.size());

        return compareData(currentData, baselineData);
    }

    private List<Verdict<WorkloadComparisonResult>> compareData(List<WorkloadTaskData> testData, List<WorkloadTaskData> baselineTestData) {

        List<Verdict<WorkloadComparisonResult>> result = Lists.newArrayList();

        for (WorkloadTaskData currentTest : testData) {
            boolean workloadMatched = false;
            for (WorkloadTaskData baselineTest : baselineTestData) {
                if (areComparable(currentTest, baselineTest)) {
                    workloadMatched = true;

                    log.debug("Going to compare workload {}", describe(currentTest));
                    Decision decision = workloadDecisionMaker.makeDecision(currentTest, baselineTest);
                    String description = describe(currentTest);

                    Verdict<WorkloadComparisonResult> verdict = new Verdict<WorkloadComparisonResult>(description, decision, compareWorkloads(currentTest, baselineTest));

                    log.debug("Verdict {}", verdict);

                    result.add(verdict);

                    break;
                }
            }

            if (!workloadMatched) {
                log.warn("Workload '{}' doesn't match", describe(currentTest));
            }
        }

        return result;
    }

    private String describe(WorkloadTaskData currentTest) {
        return currentTest.getScenario().getName() + " " + currentTest.getClock();
    }

    private WorkloadComparisonResult compareWorkloads(WorkloadTaskData first, WorkloadTaskData second) {
        return WorkloadComparisonResult.builder()
                .throughputDeviation(calculateDeviation(first.getThroughput(), second.getThroughput()))
                .avgLatencyDeviation(calculateDeviation(first.getAvgLatency(), second.getAvgLatency()))
                .stdDevLatencyDeviation(calculateDeviation(first.getStdDevLatency(), second.getStdDevLatency()))
                .successRateDeviation(calculateDeviation(first.getSuccessRate(), second.getSuccessRate()))
                .totalDurationDeviation(calculateDeviation(first.getTotalDuration(), second.getTotalDuration()))
                .build();
    }

    private static boolean areComparable(WorkloadTaskData first, WorkloadTaskData second) {

        return first.getScenario().equals(second.getScenario())     &&
               first.getClock().equals(second.getClock())           &&
               first.getClockValue().equals(second.getClockValue()) &&
               first.getTermination().equals(second.getTermination());

    }


    @Override
    public String getDescription() {
        return "Workload";
    }

    private List<WorkloadTaskData> getAllWorkloadTasks(String sessionId) {
        @SuppressWarnings("unchecked")
        List<WorkloadTaskData> scenarioData = getHibernateTemplate().find(
                "from WorkloadTaskData d where d.sessionId=? order by d.number asc, d.scenario.name asc",
                sessionId
        );

        return scenarioData;
    }

    @Required
    public void setWorkloadDecisionMaker(WorkloadDecisionMaker workloadDecisionMaker) {
        this.workloadDecisionMaker = workloadDecisionMaker;
    }

    public WorkloadDecisionMaker getWorkloadDecisionMaker() {
        return workloadDecisionMaker;
    }
}
