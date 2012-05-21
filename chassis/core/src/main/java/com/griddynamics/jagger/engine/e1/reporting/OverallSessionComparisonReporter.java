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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.engine.e1.sessioncomparation.SessionComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.SessionVerdict;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class OverallSessionComparisonReporter extends AbstractReportProvider {

    private static final Logger log = LoggerFactory.getLogger(OverallSessionComparisonReporter.class);

    private SessionComparator sessionComparator;
    private StatusImageProvider statusImageProvider;
    private BaselineSessionProvider baselineSessionProvider;


    @Override
    public JRDataSource getDataSource() {

        log.debug("Going to build session comparison report");

        String currentSession = getSessionIdProvider().getSessionId();
        String baselineSession = baselineSessionProvider.getBaselineSession();

        SessionVerdict verdict = sessionComparator.compare(currentSession, baselineSession);

        getContext().getParameters().put("jagger.verdict", verdict);
        getContext().getParameters().put("jagger.session.baseline", baselineSession);
        getContext().getParameters().put("jagger.session.current", currentSession);
        getContext().getParameters().put("jagger.statusImageProvider", statusImageProvider);

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

    @Required
    public void setBaselineSessionProvider(BaselineSessionProvider baselineSessionProvider) {
        this.baselineSessionProvider = baselineSessionProvider;
    }
}

