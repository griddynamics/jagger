package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.dbapi.dto.DecisionPerSessionDto;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricPlotPointEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.jaas.service.DynamicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.griddynamics.jagger.jaas.service.DynamicDataService.DEFAULT_DB_CONFIG_ID;

/**
 * JaaS REST API controller based on Spring MVC which exposes DB backed resources
 * of DB which is configured at deployment time
 * in contrast to {@link DynamicDataServiceRestController}
 * which is capable to register DB to connect to dynamically at runtime.
 */
@RequestMapping(value = "/db")
@RestController
@ConditionalOnProperty(name = "jaas.hide.db.access.via.api", havingValue = "true")
public class DataServiceRestController extends AbstractController {
    
    private final DynamicDataServiceRestController dynamicDataServiceRestController;
    
    public DataServiceRestController(@Autowired DynamicDataService dynamicDataService) {
        this.dynamicDataServiceRestController = new DynamicDataServiceRestController(dynamicDataService);
    }
    
    @GetMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<SessionEntity>> getSessions(
            @RequestParam(name = "id", required = false) String[] sessionIds
    ) {
        return dynamicDataServiceRestController.getSessions(DEFAULT_DB_CONFIG_ID, sessionIds);
    }
    
    @GetMapping(value = "/sessions/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionEntity> getSession(@PathVariable String sessionId) {
        return dynamicDataServiceRestController.getSession(DEFAULT_DB_CONFIG_ID, sessionId);
    }

    @GetMapping(value = "/sessions/{sessionId}/decision", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DecisionPerSessionDto> getSessionDecision(@PathVariable String sessionId) {
        return dynamicDataServiceRestController.getSessionDecision(DEFAULT_DB_CONFIG_ID, sessionId);
    }

    @GetMapping(value = "/sessions/{sessionId}/tests/{testName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestEntity> getTest(@PathVariable String sessionId, @PathVariable String testName) {
        return dynamicDataServiceRestController.getTest(DEFAULT_DB_CONFIG_ID, sessionId, testName);
    }
    
    @GetMapping(value = "/sessions/{sessionId}/tests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<TestEntity>> getTests(@PathVariable String sessionId) {
        return dynamicDataServiceRestController.getTests(DEFAULT_DB_CONFIG_ID, sessionId);
    }
    
    @GetMapping(value = "/tests/{testId}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<MetricEntity>> getMetrics(@PathVariable Long testId) {
        return dynamicDataServiceRestController.getMetrics(DEFAULT_DB_CONFIG_ID, testId);
    }
    
    @GetMapping(value = "/sessions/{sessionId}/tests/{testName}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<MetricEntity>> getMetrics(@PathVariable String sessionId, @PathVariable String testName) {
        return dynamicDataServiceRestController.getMetrics(DEFAULT_DB_CONFIG_ID, sessionId, testName);
    }
    
    @GetMapping(value = "/sessions/{sessionId}/tests/{testName}/metrics/summary",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<MetricEntity, MetricSummaryValueEntity>> getMetricsSummary(@PathVariable String sessionId,
                                                                                         @PathVariable String testName
    ) {
        return dynamicDataServiceRestController.getMetricsSummary(DEFAULT_DB_CONFIG_ID, sessionId, testName);
    }
    
    @GetMapping(value = "/sessions/{sessionId}/tests/{testName}/metrics/plot-data",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<MetricEntity, List<MetricPlotPointEntity>>> getMetricPlotData(
            @PathVariable String sessionId, @PathVariable String testName
    ) {
        return dynamicDataServiceRestController.getMetricPlotData(DEFAULT_DB_CONFIG_ID, sessionId, testName);
    }
}
