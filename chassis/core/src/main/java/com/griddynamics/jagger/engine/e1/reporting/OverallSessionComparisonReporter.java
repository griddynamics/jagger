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

import com.griddynamics.jagger.engine.e1.sessioncomparation.SessionComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.SessionVerdict;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;

public class OverallSessionComparisonReporter extends AbstractReportProvider {

    private static final Logger log = LoggerFactory.getLogger(OverallSessionComparisonReporter.class);
    public static final String JAGGER_SESSION_CURRENT = "jagger.session.current";
    public static final String JAGGER_VERDICT = "jagger.verdict";
    public static final String JAGGER_SESSION_BASELINE = "jagger.session.baseline";
    public static final String JAGGER_STATUS_IMAGE_PROVIDER = "jagger.statusImageProvider";

    private SessionComparator sessionComparator;
    private StatusImageProvider statusImageProvider;

    @Override
    public JRDataSource getDataSource(String currentSession) {
    
        log.debug("Going to build session comparison report");
        
        String baselineSession = getContext().getBaselineSessionProvider().getBaselineSession(currentSession);
        SessionVerdict verdict = sessionComparator.compare(currentSession, baselineSession);

        getContext().getParameters().put(JAGGER_VERDICT, verdict);
        getContext().getParameters().put(JAGGER_SESSION_BASELINE, baselineSession);
        getContext().getParameters().put(JAGGER_SESSION_CURRENT, currentSession);
        getContext().getParameters().put(JAGGER_STATUS_IMAGE_PROVIDER, statusImageProvider);

        return new JRBeanCollectionDataSource(Lists.newArrayList(1, 2));
    }

    @Required
    public void setStatusImageProvider(StatusImageProvider statusImageProvider) {
        this.statusImageProvider = statusImageProvider;
    }

    @Required
    public void setSessionComparator(SessionComparator sessionComparator) {
        this.sessionComparator = sessionComparator;
    }
    
    public SessionComparator getSessionComparator(){
        return sessionComparator;
    }
}

