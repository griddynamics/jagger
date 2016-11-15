package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.reporting.ReportingContext;
import com.griddynamics.jagger.reporting.ReportingService;
import org.apache.commons.lang.StringUtils;

/**
 * Produces {@link com.griddynamics.jagger.reporting.ReportingService} instances
 * based on custom session id and output report location
 * but still using some defaults
 */
public class ReportingServiceFactory {
    
    private final ReportingContext context;
    private final String rootTemplateLocation;
    
    public ReportingServiceFactory(ReportingContext context, String rootTemplateLocation) {
        this.context = context;
        this.rootTemplateLocation = rootTemplateLocation;
    }
    
    public ReportingService newInstance(String sessionId, String outputReportLocation) {
        ReportingService reportingService = new ReportingService();
        
        reportingService.setContext(context);
        reportingService.setRootTemplateLocation(rootTemplateLocation);
        
        reportingService.setDoGenerateXmlReport(false);
        reportingService.setReportType(ReportingService.ReportType.PDF);
        
        reportingService.setOutputReportLocation(outputReportLocation);
        reportingService.setSessionId(sessionId);
        
        return reportingService;
    }
    
    public ReportingService newInstance(String sessionId, String baselineSessionId, String outputReportLocation) {
        ReportingService reportingService = newInstance(sessionId, outputReportLocation);
        if (StringUtils.isNotBlank(baselineSessionId)) {
            reportingService.getContext().getBaselineSessionProvider().setBaselineSessionId(baselineSessionId);
        }
        
        return reportingService;
    }
}
