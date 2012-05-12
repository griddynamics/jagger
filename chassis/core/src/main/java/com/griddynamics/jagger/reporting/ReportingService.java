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

package com.griddynamics.jagger.reporting;

import com.griddynamics.jagger.exception.ConfigurationException;
import com.griddynamics.jagger.exception.TechnicalException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.*;

public class ReportingService {

    private final static Logger log = LoggerFactory.getLogger(ReportingService.class);

    public enum ReportType {PDF, HTML}

    private ReportingContext context;

    private String rootTemplateLocation;
    private ReportType reportType;
    private String outputReportLocation;

    public JasperPrint generateReport(boolean removeFrame) {
        context.setRemoveFrame(removeFrame);

        Map<String, Object> contextMap = new HashMap<String, Object>();
        contextMap.put(ReportingContext.CONTEXT_NAME, context);

        try {
            log.info("BEGIN: Compile report");
            JasperReport jasperReport = JasperCompileManager.compileReport(new ReportInputStream(context.getResource(rootTemplateLocation), removeFrame));
            log.info("END: Compile report");
            log.info("BEGIN: Fill report");
            JasperPrint result = JasperFillManager.fillReport(jasperReport, contextMap, new JRBeanArrayDataSource(new Object[1]));
            log.info("END: Fill report");
            return result;
        } catch (JRException e) {
            log.error("Error during report rendering", e);
            throw new TechnicalException(e);
        }
    }

    public void renderReport(boolean removeFrame) {
        try {
            JasperPrint jasperPrint = generateReport(removeFrame);

            log.info("BEGIN: Export report");
            switch(reportType) {
                case HTML : JasperExportManager.exportReportToHtmlFile(jasperPrint, outputReportLocation); break;
                case PDF : JasperExportManager.exportReportToPdfStream(jasperPrint, context.getOutputResource(outputReportLocation)); break;
                default : throw new ConfigurationException("ReportType is not specified");
            }
            log.info("END: Export report");
        } catch (JRException e) {
            log.error("Error during report rendering", e);
            throw new TechnicalException(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void setContext(ReportingContext context) {
        this.context = context;
    }

    public void setRootTemplateLocation(String rootTemplateLocation) {
        this.rootTemplateLocation = rootTemplateLocation;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public void setOutputReportLocation(String outputReportLocation) {
        this.outputReportLocation = outputReportLocation;
    }
}
