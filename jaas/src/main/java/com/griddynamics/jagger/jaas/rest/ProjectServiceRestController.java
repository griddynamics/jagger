package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import com.griddynamics.jagger.jaas.service.DynamicReportingService;
import com.griddynamics.jagger.jaas.service.ProjectService;
import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * JaaS REST API controller based on Spring MVC which exposes project resources.
 */
@RequestMapping(value = "/projects")
@RestController
public class ProjectServiceRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceRestController.class);

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private DynamicReportingService dynamicReportingService;
    
    private <T, R> ResponseEntity<R> produceGetResponse(T responseSource, Function<T, R> responseFunction) {
        ResponseEntity<R> responseEntity = HttpGetResponseProducer.produce(responseSource, responseFunction);
        LOGGER.debug("Produced response: {}", responseEntity);
        return responseEntity;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProjectEntity>> getProjects() {
        return produceGetResponse(projectService, t -> projectService.readAll());
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProject(@RequestBody ProjectEntity project) {
        projectService.create(project);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{projectId}")
                        .buildAndExpand(project.getId())
                        .toUri())
                .build();
    }

    @PutMapping(value = "/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProject(@PathVariable Long projectId, @RequestBody ProjectEntity project) {
        project.setId(projectId);
        projectService.update(project);
        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectEntity> getProject(@PathVariable Long projectId) {
        return produceGetResponse(projectService, t -> projectService.read(projectId));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId) {
        projectService.delete(projectId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping(value = "/{projectId}/sessions/{sessionId}/report", produces = MediaType.APPLICATION_PDF_VALUE)
    public WebAsyncTask<ResponseEntity<Resource>> getReport(@PathVariable Long projectId, @PathVariable String sessionId)
            throws IOException {
    
        // Given up to 10 minutes to generate a report before timeout failure.
        return new WebAsyncTask<>(1000 * 60 * 10, () -> {
            Long dbId = Optional.ofNullable(projectService.read(projectId))
                                .orElseThrow(ResourceNotFoundException::getProjectResourceNfe).getDbId();
            Resource reportResource = dynamicReportingService.generateReportFor(dbId, sessionId);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                                              "inline; filename=\"" + reportResource.getFilename() + "\""
            ).body(reportResource);
        });
    }
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> foreignKeyException(DataIntegrityViolationException error) {
        ResponseEntity<?> responseEntity;
        if (error.getMessage().contains("FOREIGN KEY")) {
            responseEntity = new ResponseEntity<>("There is no such database.", HttpStatus.BAD_REQUEST);
        } else {
            responseEntity = new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}
