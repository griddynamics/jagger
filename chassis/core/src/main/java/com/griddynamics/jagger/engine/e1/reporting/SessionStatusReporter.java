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

import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.awt.Image;
import java.util.Collections;
import java.util.List;

public class SessionStatusReporter extends AbstractReportProvider {
    private static final Logger log = LoggerFactory.getLogger(SessionStatusReporter.class);

    private SessionStatusDecisionMaker decisionMaker;
    private StatusImageProvider statusImageProvider;


    @Override
    public JRDataSource getDataSource() {
        @SuppressWarnings("unchecked")
		List<WorkloadTaskData> scenarioData = getHibernateTemplate().find("from WorkloadTaskData d where d.sessionId=? order by d.number asc, d.scenario.name asc",
				getSessionIdProvider().getSessionId());


        List<SessionData> sessionData = getHibernateTemplate().find("from SessionData d where d.sessionId=?",
                getSessionIdProvider().getSessionId());

        SessionStatus result = new SessionStatus();
        result.setMessage(decisionMaker.getDescription());

        result.setDecision(decisionMaker.decideOnSession(scenarioData));
        if(sessionData.size()==1){
            String errorMessage=sessionData.get(0).getErrorMessage();
            if(errorMessage!=null){
                log.info("Session status changed to fatal cause error message is {}", errorMessage);
                result.setDecision(Decision.FATAL);
                result.setMessage("Session error: "+errorMessage);
            }
        } else{
            log.warn("Session data has unexpected size: {}",sessionData.size());
        }
        result.setStatusImage(statusImageProvider.getImageByDecision(result.getDecision()));

        return new JRBeanCollectionDataSource(Collections.singletonList(result));
    }

    @Required
    public void setDecisionMaker(SessionStatusDecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    @Required
    public void setStatusImageProvider(StatusImageProvider statusImageProvider) {
        this.statusImageProvider = statusImageProvider;
    }

    public static class SessionStatus {
        private Image statusImage;
        private String message;
        private Decision decision;

        public Image getStatusImage() {
            return statusImage;
        }

        public void setStatusImage(Image statusImage) {
            this.statusImage = statusImage;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Decision getDecision() {
            return decision;
        }

        public void setDecision(Decision decision) {
            this.decision = decision;
        }
    }
}
