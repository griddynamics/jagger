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

import com.griddynamics.jagger.reporting.AbstractReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * @author Mark Novozhilov
 *         Date: 26.06.2014
 */
public class SessionScopePlotsReporter extends AbstractReportProvider {
    private Logger log = LoggerFactory.getLogger(SessionScopePlotsReporter.class);

    @Override
    public JRDataSource getDataSource(String sessionId) {

        int numberOfTestGroup = getContext().getSummaryReporter().getNumberOfTestGroups(sessionId);
        if (numberOfTestGroup < 2) {
            if (numberOfTestGroup == 0) {
                log.error("No test groups were fetched for {} session.", sessionId);
            } else {
                log.info("There is one test group in current {} session. Skipping session scope plots fetching.", sessionId);
            }

            return new JRBeanCollectionDataSource(Collections.emptyList());
        }

        PlotsReporter.MetricPlotDTOs sessionScopePlots = getContext().getPlotsReporter().getSessionScopePlots(sessionId);

        return new JRBeanCollectionDataSource(Collections.singleton(sessionScopePlots));
    }
}