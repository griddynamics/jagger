package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricPlotPointEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.jaas.service.DynamicDataService;
import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * JaaS REST API controller based on Spring MVC which exposes db backed resources.
 */
@RequestMapping(value = "/jaas")
@RestController
public class JaasRestController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JaasRestController.class);
    
    @Autowired
    private DynamicDataService dynamicDataService;
    
    private DataService getDataService(Long id) {
        return dynamicDataService.getDataServiceFor(id);
    }
    
    private <T, R> ResponseEntity<R> produceGetResponse(T responseSource, Function<T, R> responseFunction) {
        ResponseEntity<R> responseEntity = HttpGetResponseProducer.produce(responseSource, responseFunction);
        LOGGER.debug("Produced response: {}", responseEntity);
        return responseEntity;
    }
    
    private <R> ResponseEntity<R> produceDsResponse(Long dbId, Function<DataService, R> responseFunction) {
        ResponseEntity<R> responseEntity = HttpGetResponseProducer.produce(getDataService(dbId), responseFunction);
        LOGGER.debug("Produced response: {}", responseEntity);
        return responseEntity;
    }
    
    @GetMapping(value = "/dbs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DbConfigEntity>> getDbConfigs() {
        return produceGetResponse(dynamicDataService, t -> dynamicDataService.readAll());
    }
    
    @PostMapping(value = "/dbs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDbConfig(@RequestBody DbConfigEntity config) {
        dynamicDataService.create(config);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{dbId}").buildAndExpand(config.getId()).toUri())
                             .build();
    }
    
    @PutMapping(value = "/dbs/{dbId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDbConfig(@PathVariable Long dbId, @RequestBody DbConfigEntity config) {
        config.setId(dbId);
        dynamicDataService.update(config);
        return ResponseEntity.accepted().build();
    }
    
    @GetMapping(value = "/dbs/{dbId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DbConfigEntity> getDbConfig(@PathVariable Long dbId) {
        return produceGetResponse(dynamicDataService, t -> dynamicDataService.read(dbId));
    }
    
    @DeleteMapping("/dbs/{dbId}")
    public ResponseEntity<?> deleteDbConfig(@PathVariable Long dbId) {
        DbConfigEntity config = new DbConfigEntity();
        config.setId(dbId);
        dynamicDataService.delete(config);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping(value = "/dbs/{dbId}/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<SessionEntity>> getSessions(@PathVariable Long dbId, @RequestParam(name = "id",
                                                                                                 required = false) String[] sessionIds
    ) {
        final String[] finalSessionIds = Optional.ofNullable(sessionIds).orElse(new String[0]);
        return produceDsResponse(dbId, dataService -> dataService.getSessions(Arrays.asList(finalSessionIds)));
    }
    
    @GetMapping(value = "/dbs/{dbId}/sessions/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionEntity> getSession(@PathVariable Long dbId, @PathVariable String sessionId) {
        return produceDsResponse(dbId, dataService -> dataService.getSession(sessionId));
    }
    
    @GetMapping(value = "/dbs/{dbId}/sessions/{sessionId}/tests/{testName}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestEntity> getTest(@PathVariable Long dbId, @PathVariable String sessionId,
                                              @PathVariable String testName
    ) {
        return produceDsResponse(dbId, dataService -> getTestByName(dataService, sessionId, testName));
    }
    
    private TestEntity getTestByName(DataService dataService, String sessionId, String testName) {
        return dataService.getTestByName(sessionId, testName);
    }
    
    @GetMapping(value = "/dbs/{dbId}/sessions/{sessionId}/tests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<TestEntity>> getTests(@PathVariable Long dbId, @PathVariable String sessionId) {
        return produceDsResponse(dbId, dataService -> dataService.getTests(sessionId));
    }
    
    @GetMapping(value = "/dbs/{dbId}/tests/{testId}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<MetricEntity>> getMetrics(@PathVariable Long dbId, @PathVariable Long testId) {
        return produceDsResponse(dbId, dataService -> dataService.getMetrics(testId));
    }
    
    @GetMapping(value = "/dbs/{dbId}/sessions/{sessionId}/tests/{testName}/metrics",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<MetricEntity>> getMetrics(@PathVariable Long dbId, @PathVariable String sessionId,
                                                        @PathVariable String testName
    ) {
        return produceDsResponse(dbId, dataService -> getMetrics(dataService, sessionId, testName));
    }
    
    private Set<MetricEntity> getMetrics(DataService dataService, String sessionId, String testName) {
        return Optional.ofNullable(getTestByName(dataService, sessionId, testName)).map(dataService::getMetrics)
                       .orElse(Collections.emptySet());
    }
    
    @GetMapping(value = "/dbs/{dbId}/sessions/{sessionId}/tests/{testName}/metrics/summary",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<MetricEntity, MetricSummaryValueEntity>> getMetricsSummary(@PathVariable Long dbId,
                                                                                         @PathVariable String sessionId,
                                                                                         @PathVariable String testName
    ) {
        return produceDsResponse(dbId, dataService -> dataService
                .getMetricSummary(getMetrics(dataService, sessionId, testName)));
    }
    
    @GetMapping(value = "/dbs/{dbId}/sessions/{sessionId}/tests/{testName}/metrics/plot-data",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<MetricEntity, List<MetricPlotPointEntity>>> getMetricPlotData(@PathVariable Long dbId,
                                                                                            @PathVariable String sessionId,
                                                                                            @PathVariable String testName
    ) {
        return produceDsResponse(dbId, dataService -> dataService
                .getMetricPlotData(getMetrics(dataService, sessionId, testName)));
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StaleStateException.class)
    /**
     * Catches an exception which occurs if we try delete or update a row that does
     * not exist.
     */
    void noDataFound() {
    }
}