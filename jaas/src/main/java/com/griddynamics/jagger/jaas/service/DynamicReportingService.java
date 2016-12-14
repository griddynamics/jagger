package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.reporting.ReportingService;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface DynamicReportingService {
    Resource generateReportFor(Long dbId, String sessionId, String baseLineSessionId) throws IOException;

    ReportingService getReportingServiceFor(Long dbId, String sessionId, String baseLineSessionId) throws IOException;
}
