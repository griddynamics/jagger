package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.service.ProjectService;
import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * JaaS REST API controller based on Spring MVC which exposes project resources.
 */
@RequestMapping(value = "/projects")
@RestController
public class ProjectServiceRestController extends AbstractController {

    private final ProjectService projectService;
    
    @Autowired
    public ProjectServiceRestController(ProjectService projectService) {
        this.projectService = projectService;
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
}
