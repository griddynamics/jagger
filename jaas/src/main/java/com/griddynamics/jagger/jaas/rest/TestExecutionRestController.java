package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.exceptions.InvalidTestExecutionException;
import com.griddynamics.jagger.jaas.service.TestExecutionService;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/executions")
@Api(description = "Jagger Test Execution API. It provides endpoints for reading, creating, updating and deleting Test Executions. "
        + "This API can be used manually or via separate Jenkins plugin, to run particular performance test projects on the "
        + "selected test environment")
public class TestExecutionRestController extends AbstractController {

    private TestExecutionService testExecutionService;

    @Autowired
    public TestExecutionRestController(TestExecutionService testExecutionService) {
        this.testExecutionService = testExecutionService;
    }

    @GetMapping(value = "/{testExecutionId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TestExecutionEntity> getTestExecution(@PathVariable Long testExecutionId) {
        return produceGetResponse(testExecutionService, t -> testExecutionService.read(testExecutionId));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestExecutionEntity>> getTestExecutions(@RequestParam(name = "envId", required = false) String envId) {
        if (StringUtils.isBlank(envId))
            return produceGetResponse(testExecutionService, t -> testExecutionService.readAll());
        else
            return produceGetResponse(testExecutionService, t -> testExecutionService.readByEnv(envId));
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TestExecutionEntity> createTestExecution(@RequestBody TestExecutionEntity testExecution) {
        validateTestExecution(testExecution);

        testExecutionService.create(testExecution);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{testExecutionId}")
                        .buildAndExpand(testExecution.getId())
                        .toUri())
                .body(testExecution);
    }
    
    @PutMapping(value = "/{testExecutionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTestExecution(@PathVariable Long testExecutionId,
                                                 @RequestBody TestExecutionEntity executionEntity) {
        executionEntity.setId(testExecutionId);
        testExecutionService.update(executionEntity);
        return ResponseEntity.accepted().build();
    }

    private void validateTestExecution(TestExecutionEntity testExecution) {
    
        if (StringUtils.isEmpty(testExecution.getEnvId())) {
            throw new InvalidTestExecutionException(
                    "envId must be specified");
        }
        
        if (testExecution.getLoadScenarioId() == null && testExecution.getTestProjectURL() == null) {
            throw new InvalidTestExecutionException(
                    "Neither a test project URL nor a load scenario name are specified");
        }
        
        if (StringUtils.isNotBlank(testExecution.getTestProjectURL())) {
            try {
                new URL(testExecution.getTestProjectURL());
            } catch (MalformedURLException e) {
                throw new InvalidTestExecutionException(
                        format("Test project URL '%s' is not valid URL! %s", testExecution.getTestProjectURL(), e.getMessage()), e);
            }
        }
    }

    @DeleteMapping(value = "/{testExecutionId}")
    public ResponseEntity<?> deleteTestExecution(@PathVariable Long testExecutionId) {
        testExecutionService.delete(testExecutionId);
        return ResponseEntity.noContent().build();
    }
}
