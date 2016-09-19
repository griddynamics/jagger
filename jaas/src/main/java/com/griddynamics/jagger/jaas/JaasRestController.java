package com.griddynamics.jagger.jaas;

import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricPlotPointEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.jaas.rest.HttpGetResponseProducer;
import com.griddynamics.jagger.jaas.service.DynamicDataService;
import com.griddynamics.jagger.jaas.storage.model.JaggerTestDbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    
    private DataService getDataService(String name) {
        return dynamicDataService.getDataServiceFor(name);
    }
    
    private <T, R> ResponseEntity<R> produceGetResponse(T responseSource, Function<T, R> responseFunction) {
        ResponseEntity<R> responseEntity = HttpGetResponseProducer.produce(responseSource, responseFunction);
        LOGGER.debug("Produced response: {}", responseEntity);
        return responseEntity;
    }
    
    private <R> ResponseEntity<R> produceDsResponse(String dbName, Function<DataService, R> responseFunction) {
        ResponseEntity<R> responseEntity = HttpGetResponseProducer.produce(getDataService(dbName), responseFunction);
        LOGGER.debug("Produced response: {}", responseEntity);
        return responseEntity;
    }
    
    @GetMapping("/dbs")
    public ResponseEntity<List<JaggerTestDbConfig>> getDbConfigs() {
        return produceGetResponse(dynamicDataService, t -> dynamicDataService.readAll());
    }
    
    @PostMapping(value = "/dbs")
    public ResponseEntity<?> createDbConfig(@RequestBody JaggerTestDbConfig config) {
        dynamicDataService.createOrUpdate(config);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{dbName}").buildAndExpand(config.getName())
                                           .toUri()).build();
    }
    
    @GetMapping("/dbs/{dbName}")
    public ResponseEntity<JaggerTestDbConfig> getDbConfig(@PathVariable String dbName) {
        return produceGetResponse(dynamicDataService, t -> dynamicDataService.read(dbName));
    }
    
    @DeleteMapping("/dbs/{dbName}")
    public ResponseEntity<?> deleteDbConfig(@PathVariable String dbName) {
        JaggerTestDbConfig config = dynamicDataService.read(dbName);
        if (config == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        dynamicDataService.delete(config);
        return ResponseEntity.accepted().build();
    }
    
    @GetMapping("/dbs/{dbName}/sessions")
    public ResponseEntity<Set<SessionEntity>> getSessions(@PathVariable String dbName,
                                                          @RequestParam(name = "id", required = false) String[] sessionIds
    ) {
        final String[] finalSessionIds = Optional.ofNullable(sessionIds).orElse(new String[0]);
        return produceDsResponse(dbName, dataService -> dataService.getSessions(Arrays.asList(finalSessionIds)));
    }
    
    @GetMapping("/dbs/{dbName}/sessions/{sessionId}")
    public ResponseEntity<SessionEntity> getSession(@PathVariable String dbName, @PathVariable String sessionId) {
        return produceDsResponse(dbName, dataService -> dataService.getSession(sessionId));
    }
    
    @GetMapping("/dbs/{dbName}/sessions/{sessionId}/tests/{testName}")
    public ResponseEntity<TestEntity> getTest(@PathVariable String dbName, @PathVariable String sessionId,
                                              @PathVariable String testName
    ) {
        return produceDsResponse(dbName, dataService -> getTestByName(dataService, sessionId, testName));
    }
    
    private TestEntity getTestByName(DataService dataService, String sessionId, String testName) {
        return dataService.getTestByName(sessionId, testName);
    }
    
    @GetMapping("/dbs/{dbName}/sessions/{sessionId}/tests")
    public ResponseEntity<Set<TestEntity>> getTests(@PathVariable String dbName, @PathVariable String sessionId) {
        return produceDsResponse(dbName, dataService -> dataService.getTests(sessionId));
    }
    
    @GetMapping("/tests/{testId}/metrics")
    public ResponseEntity<Set<MetricEntity>> getMetrics(@PathVariable String dbName, @PathVariable Long testId) {
        return produceDsResponse(dbName, dataService -> dataService.getMetrics(testId));
    }
    
    @GetMapping("/dbs/{dbName}/sessions/{sessionId}/tests/{testName}/metrics")
    public ResponseEntity<Set<MetricEntity>> getMetrics(@PathVariable String dbName, @PathVariable String sessionId,
                                                        @PathVariable String testName
    ) {
        return produceDsResponse(dbName, dataService -> getMetrics(dataService, sessionId, testName));
    }
    
    private Set<MetricEntity> getMetrics(DataService dataService, String sessionId, String testName) {
        return Optional.ofNullable(getTestByName(dataService, sessionId, testName)).map(dataService::getMetrics)
                       .orElse(Collections.emptySet());
    }
    
    @ResponseStatus
    @GetMapping("/dbs/{dbName}/sessions/{sessionId}/tests/{testName}/metrics/summary")
    public ResponseEntity<Map<MetricEntity, MetricSummaryValueEntity>> getMetricsSummary(@PathVariable String dbName,
                                                                                         @PathVariable String sessionId,
                                                                                         @PathVariable String testName
    ) {
        return produceDsResponse(dbName, dataService -> dataService
                .getMetricSummary(getMetrics(dataService, sessionId, testName)));
    }
    
    @GetMapping("/dbs/{dbName}/sessions/{sessionId}/tests/{testName}/metrics/plot-data")
    public ResponseEntity<Map<MetricEntity, List<MetricPlotPointEntity>>> getMetricPlotData(@PathVariable String dbName,
                                                                                            @PathVariable String sessionId,
                                                                                            @PathVariable String testName
    ) {
        return produceDsResponse(dbName, dataService -> dataService
                .getMetricPlotData(getMetrics(dataService, sessionId, testName)));
    }
}
