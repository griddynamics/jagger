package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.service.DynamicReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.io.IOException;

/**
 * JaaS REST API controller based on Spring MVC which generates Jagger test execution reports.
 */
@RequestMapping(value = "/report")
@RestController
@ConditionalOnProperty(name = "jaas.hide.db.access.via.api", havingValue = "false")
public class DynamicReportingRestController {
    
    private final DynamicReportingService dynamicReportingService;
    
    @Autowired
    public DynamicReportingRestController(DynamicReportingService dynamicReportingService) {
        this.dynamicReportingService = dynamicReportingService;
    }
    
    @GetMapping(value = "/dbs/{dbId}")
    public WebAsyncTask<ResponseEntity<Resource>> getReport(
            @PathVariable Long dbId,
            @RequestParam(name = "sessionId", required = true) String sessionId,
            @RequestParam(name = "baseLineSessionId", required = false) String baseLineSessionId
    ) throws IOException {
        
        // Given up to 10 minutes to generate a report before timeout failure.
        return new WebAsyncTask<>(1000 * 60 * 10, () -> {
            
            Resource reportResource = dynamicReportingService.generateReportFor(dbId, sessionId, baseLineSessionId);
            return ResponseEntity.ok()
                                 .header(
                                         HttpHeaders.CONTENT_DISPOSITION,
                                         "inline; filename=\"" + reportResource.getFilename() + "\""
                                 )
                                 .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                                 .body(reportResource);
        });
    }
}
