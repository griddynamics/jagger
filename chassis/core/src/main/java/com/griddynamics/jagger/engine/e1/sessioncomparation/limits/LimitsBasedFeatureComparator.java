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
package com.griddynamics.jagger.engine.e1.sessioncomparation.limits;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.dto.TaskDecisionDto;
import com.griddynamics.jagger.dbapi.entity.DecisionPerSessionEntity;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.engine.e1.sessioncomparation.WorstCaseDecisionMaker;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.FeatureComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Verdict;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.SQLException;
import java.util.*;


public class LimitsBasedFeatureComparator extends HibernateDaoSupport implements FeatureComparator<String> {
    private static final Logger log = LoggerFactory.getLogger(LimitsBasedFeatureComparator.class);

    private DatabaseService databaseService;

    @Override
    public List<Verdict<String>> compare(String currentSession, String baselineSession) {

        Decision decisionPerSession;

        // If decision already taken => use it
        // Such case can occur, f.e. when Jagger is running in reporter mode
        Map<String,Decision> decisionPerSessionMap = databaseService.getDecisionsPerSession(new HashSet<String>(Arrays.asList(currentSession)));
        if (decisionPerSessionMap.containsKey(currentSession)) {
            decisionPerSession = decisionPerSessionMap.get(currentSession);
            log.info("Decision '{}' was fetched from database for session {}",decisionPerSession,currentSession);
        }
        else {

            WorstCaseDecisionMaker worstCaseDecisionMaker = new WorstCaseDecisionMaker();

            // Get tests ids
            // matching setup = no matching
            SessionMatchingSetup sessionMatchingSetup = new SessionMatchingSetup(false, Collections.<SessionMatchingSetup.MatchBy>emptySet());
            List<TaskDataDto> taskDataDtoList =
                    databaseService.getTaskDataForSessions(new HashSet<String>(Arrays.asList(currentSession)),sessionMatchingSetup);
            Set<Long> testIds = new HashSet<Long>();
            for (TaskDataDto taskDataDto : taskDataDtoList) {
                testIds.addAll(taskDataDto.getIds());
            }

            // Get test group ids
            Set<Long> testGroupIds = databaseService.getTestGroupIdsByTestIds(testIds).keySet();

            // Get decisions for test groups
            Set<TaskDecisionDto> taskDecisionDtoSet = databaseService.getDecisionsPerTask(testGroupIds);
            List<Decision> testGroupDecisions = new ArrayList<Decision>();
            for (TaskDecisionDto taskDecisionDto : taskDecisionDtoSet) {
                log.info("Decision '{}' was made for test group {}",taskDecisionDto.getDecision(),taskDecisionDto.getName());
                testGroupDecisions.add(taskDecisionDto.getDecision());
            }

            // Make decision per session
            decisionPerSession = worstCaseDecisionMaker.getDecision(testGroupDecisions);
            log.info("As result, decision '{}' was made for session {}",decisionPerSession,currentSession);

            // Save decision per session
            final DecisionPerSessionEntity decisionPerSessionEntity = new DecisionPerSessionEntity(currentSession,decisionPerSession.toString());

            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    session.persist(decisionPerSessionEntity);
                    session.flush();
                    return null;
                }
            });
        }

        List<Verdict<String>> verdicts = Lists.newArrayList();
        verdicts.add(new Verdict<String>("Decision per session based on comparing metrics to limits",
                decisionPerSession,""));
        return verdicts;
    }

    @Override
    public String getDescription() {
        return "Limits based comparator";
    }

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

}
