package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.service.JobService;
import com.griddynamics.jagger.jaas.storage.model.JobEntity;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/jobs")
public class JobRestController extends AbstractController {

    private JobService jobService;

    @Autowired
    public JobRestController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping(value = "/{jobId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JobEntity> getJob(@PathVariable Long jobId) {
        return produceGetResponse(jobService, t -> jobService.read(jobId));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobEntity>> getJobs() {
        return produceGetResponse(jobService, t -> jobService.readAll());
    }

    @PutMapping(value = "/{jobId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<JobEntity> updateJob(@PathVariable Long jobId, @RequestBody JobEntity job) {
        job.setId(jobId);
        jobService.update(job);
        return ResponseEntity.accepted().body(job);
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JobEntity> createJob(@RequestBody JobEntity job) {
        jobService.create(job);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{jobId}")
                        .buildAndExpand(job.getId())
                        .toUri())
                .body(job);
    }

    @DeleteMapping(value = "/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId) {
        jobService.delete(jobId);
        return ResponseEntity.noContent().build();
    }
}
