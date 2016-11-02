package com.griddynamics.jagger.jaas.service.impl;

import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import com.griddynamics.jagger.jaas.service.DynamicReportingService;
import com.griddynamics.jagger.jaas.service.ReportingServiceFactory;
import com.griddynamics.jagger.reporting.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Provides {@link com.griddynamics.jagger.reporting.ReportingService} based on runtime DB config.
 */
@Service
public class DynamicReportingServiceImpl implements DynamicReportingService {
    
    private final DynamicDataServiceImpl dynamicDataService;
    
    public DynamicReportingServiceImpl(@Autowired DynamicDataServiceImpl dynamicDataService) {
        this.dynamicDataService = dynamicDataService;
    }

    @Override
    public Resource generateReportFor(final Long dbId, final String sessionId) throws IOException {
        ReportingService reportingService = getReportingServiceFor(dbId, sessionId);
        reportingService.renderReport(true);
        
        return new FileSystemResource(reportingService.getOutputReportLocation());
    }

    @Override
    public ReportingService getReportingServiceFor(final Long dbId, final String sessionId) throws IOException {
                     
        ApplicationContext applicationContext = Optional.ofNullable(dynamicDataService.getDynamicContextFor(dbId))
                                                        .orElseThrow(ResourceNotFoundException::getDbResourceNfe);
        Optional.ofNullable(applicationContext.getBean(DataService.class).getSession(sessionId))
                .orElseThrow(ResourceNotFoundException::getSessionResourceNfe);
        
        ReportingServiceFactory factory = applicationContext.getBean(ReportingServiceFactory.class);
        Path path = Files.createTempDirectory("jaas");
        return factory.newInstance(sessionId, path.resolve("jagger-report.pdf").toString());
    }
}
