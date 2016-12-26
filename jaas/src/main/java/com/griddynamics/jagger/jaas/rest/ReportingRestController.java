package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.service.DynamicReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.io.IOException;

import static com.griddynamics.jagger.jaas.service.DynamicDataService.DEFAULT_DB_CONFIG_ID;

/**
 * JaaS REST API controller based on Spring MVC which generates Jagger test execution reports
 * using DB which is configured at deployment time
 * in contrast to {@link DynamicReportingRestController}
 * which is capable to generate reports for DB described at runtime.
 */
@RequestMapping(value = "/report")
@RestController
@ConditionalOnProperty(name = "jaas.hide.db.access.via.api", havingValue = "true")
public class ReportingRestController {
    
    private final DynamicReportingRestController dynamicReportingRestController;
    
    @Autowired
    public ReportingRestController(DynamicReportingService dynamicReportingService) {
        this.dynamicReportingRestController = new DynamicReportingRestController(dynamicReportingService);
    }
    
    @GetMapping(value = "")
    public WebAsyncTask<ResponseEntity<Resource>> getReport(
            @RequestParam(name = "sessionId") String sessionId,
            @RequestParam(name = "baseLineSessionId", required = false) String baseLineSessionId
    ) throws IOException {
        return dynamicReportingRestController.getReport(DEFAULT_DB_CONFIG_ID, sessionId, baseLineSessionId);
    }
}
