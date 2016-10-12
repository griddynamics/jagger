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

package com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.FeatureComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Verdict;
import com.griddynamics.jagger.dbapi.entity.PerformedMonitoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

import static com.google.common.base.Objects.equal;

@Deprecated
public class MonitoringFeatureComparator extends HibernateDaoSupport implements FeatureComparator<MonitoringParameterComparison> {
    private static final Logger log = LoggerFactory.getLogger(MonitoringFeatureComparator.class);

    private MonitoringSummaryRetriever monitoringSummaryRetriever;
    private MonitoringParameterDecisionMaker monitoringParameterDecisionMaker;

    @Override
    @Deprecated
    public List<Verdict<MonitoringParameterComparison>> compare(String currentSession, String baselineSession) {

        log.debug("Comparing of sessions {} and {} requested", currentSession, baselineSession);

        List<PerformedMonitoring> currentMonitoring = loadPerformedMonitoring(currentSession);
        List<PerformedMonitoring> baselineMonitoring = loadPerformedMonitoring(baselineSession);

        log.debug("{} stats for current sessions", currentMonitoring.size());
        log.debug("{} stats for baseline sessions", baselineMonitoring.size());

        List<Verdict<MonitoringParameterComparison>> verdicts = Lists.newLinkedList();
        for (PerformedMonitoring current : currentMonitoring) {

            boolean monitoringMatched = false;

            for (PerformedMonitoring baseline : baselineMonitoring) {

                if (areComparable(current, baseline)) {
                    monitoringMatched = true;

                    String currentTaskId = current.getMonitoringId();

                    MonitoringSummary currentSummary = monitoringSummaryRetriever.load(currentSession, currentTaskId);

                    String baselineTaskId = baseline.getMonitoringId();

                    MonitoringSummary baselineSummary = monitoringSummaryRetriever.load(baselineSession, baselineTaskId);

                    log.debug("going to compare summaries {} {} ", currentSummary, baselineSummary);
                    verdicts.addAll(compareSummaries(current.getName(), currentSummary, baselineSummary));
                }

            }

            if (!monitoringMatched) {
                log.warn("Monitoring doesn't match");
            }
        }

        return verdicts;
    }

    @Deprecated
    private List<Verdict<MonitoringParameterComparison>> compareSummaries(String taskName, MonitoringSummary firstSummary, MonitoringSummary secondSummary) {

        log.debug("Comparing of summaries {} {} requested", firstSummary, secondSummary);

        List<Verdict<MonitoringParameterComparison>> verdicts = Lists.newLinkedList();
        for (String firstSource : firstSummary.getSources()) {
            boolean sourceMatched = false;

            for (String secondSource : secondSummary.getSources()) {

                if (equal(firstSource, secondSource)) {
                    log.debug("Matched {}", firstSource);

                    sourceMatched = true;

                    for (String firstParam : firstSummary.getParams(firstSource)) {
                        boolean paramMatched = false;

                        String description = taskName + " " + firstSource + " " + firstParam;
                        for (String secondParam : secondSummary.getParams(secondSource)) {

                            if (equal(firstParam, secondParam)) {
                                log.debug("Param {} of source", firstParam, firstSource);

                                paramMatched = true;

                                MonitoringParameterComparison comparison = new MonitoringParameterComparison(firstSummary.getStats(firstSource, firstParam),
                                        secondSummary.getStats(secondSource, secondParam));
                                Decision decision = monitoringParameterDecisionMaker.makeDecision(firstParam, comparison);
                                verdicts.add(new Verdict<MonitoringParameterComparison>(description, decision, comparison));
                            }

                        }

                        if (!paramMatched) {
                            log.warn("No match for source {} and parameter {}", firstSource, firstParam);
                        }
                    }

                }

            }

            if (!sourceMatched) {
                log.warn("No match for source {}", firstSource);
            }

        }

        return verdicts;

    }

    @Deprecated
    private boolean areComparable(PerformedMonitoring current, PerformedMonitoring baseline) {
        if (current.getParentId() == null && baseline.getParentId() == null) {
            return true;
        }

        return current.getName().equals(baseline.getName());
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    private List<PerformedMonitoring> loadPerformedMonitoring(String sessionId) {
        return (List<PerformedMonitoring>) getHibernateTemplate().find("from PerformedMonitoring where sessionId = ?", sessionId);
    }

    @Override
    public String getDescription() {
        return "Monitoring";
    }

    @Required
    public void setMonitoringSummaryRetriever(MonitoringSummaryRetriever monitoringSummaryRetriever) {
        this.monitoringSummaryRetriever = monitoringSummaryRetriever;
    }

    @Required
    public void setMonitoringParameterDecisionMaker(MonitoringParameterDecisionMaker monitoringParameterDecisionMaker) {
        this.monitoringParameterDecisionMaker = monitoringParameterDecisionMaker;
    }

    public MonitoringSummaryRetriever getMonitoringSummaryRetriever() {
        return monitoringSummaryRetriever;
    }

    public MonitoringParameterDecisionMaker getMonitoringParameterDecisionMaker() {
        return monitoringParameterDecisionMaker;
    }
}
