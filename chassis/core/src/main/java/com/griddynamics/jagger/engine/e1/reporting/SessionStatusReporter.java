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

import com.griddynamics.jagger.dbapi.entity.SessionData;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import com.griddynamics.jagger.util.Decision;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class SessionStatusReporter extends AbstractReportProvider {
    private static final Logger log = LoggerFactory.getLogger(SessionStatusReporter.class);

    private SummaryReporter summaryReporter;
    private StatusImageProvider statusImageProvider;


    @Override
    public JRDataSource getDataSource() {

        String sessionId = getSessionIdProvider().getSessionId();

        String sessionStatusComment = "Status is based on status of the worst test";
        Decision sessionStatus = Decision.OK;

        // Worst status of executed tests
        Decision sessionsWorstTestStatus = Decision.OK;
        String sessionsWorstTestStatusComment = "";
        List<SummaryTestDto> summaryTestDtoList = summaryReporter.getTestSummaryData(sessionId);
        for (SummaryTestDto summaryTestDto : summaryTestDtoList) {
            if (summaryTestDto.getTestStatus().ordinal() > sessionsWorstTestStatus.ordinal()) {
                sessionsWorstTestStatus = summaryTestDto.getTestStatus();
                sessionsWorstTestStatusComment = summaryTestDto.getTestName();
            }
        }
        if (sessionsWorstTestStatus.ordinal() > sessionStatus.ordinal()) {
            sessionStatusComment = "Session status is based on status of the worst test: " + sessionsWorstTestStatusComment;
            sessionStatus = sessionsWorstTestStatus;
        }

        // Errors during session execution
        Decision sessionExecutionStatus = Decision.OK;
        String errorMessage = "";
        List<SessionData> sessionData = (List<SessionData>) getHibernateTemplate().find("from SessionData d where d.sessionId=?",
                getSessionIdProvider().getSessionId());
        if (sessionData.size() == 1) {
            errorMessage = sessionData.get(0).getErrorMessage();
            if (errorMessage != null) {
                log.info("Session status changed to fatal. There were errors during session execution (f.e. exceptions or some task failed). Session error was reported: {}", errorMessage);
                sessionExecutionStatus = Decision.FATAL;
            }
        } else {
            log.warn("Session data has unexpected size: {}", sessionData.size());
        }
        if (sessionExecutionStatus.ordinal() > sessionStatus.ordinal()) {
            sessionStatusComment = "Session status is based on session execution status. There were errors during session execution (f.e. exceptions or some task failed). Session error was reported: '"
                    + errorMessage + "'";
            sessionStatus = sessionExecutionStatus;
        }


        SessionStatus result = new SessionStatus();
        result.setMessage(sessionStatusComment);
        result.setDecision(sessionStatus);
        result.setStatusImage(statusImageProvider.getImageByDecision(sessionStatus));

        return new JRBeanCollectionDataSource(Collections.singletonList(result));
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

    @Required
    public void setSummaryReporter(SummaryReporter summaryReporter) {
        this.summaryReporter = summaryReporter;
    }

}
