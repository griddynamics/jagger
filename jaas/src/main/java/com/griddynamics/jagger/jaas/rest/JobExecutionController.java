package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.service.JobExecutionService;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.HttpStatus.CREATED;

@Controller
@RequestMapping("/jobs/execution")
public class JobExecutionController {

    private JobExecutionService jobExecutionService;

    @Autowired
    public JobExecutionController(JobExecutionService jobExecutionService) {
        this.jobExecutionService = jobExecutionService;
    }

    @PostMapping("/{jobId}/start")
    public ResponseEntity<?> createJobExecution(@PathVariable Long jobId) {
        jobExecutionService.create(new JobExecutionEntity(jobId));
        return ResponseEntity.status(CREATED).build();
    }
}
