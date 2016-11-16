package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;

import java.util.List;

public interface JobExecutionService {
    List<JobExecutionEntity> readAll();

    List<JobExecutionEntity> readAllPending();

    JobExecutionEntity create(JobExecutionEntity job);

    void delete(Long jobId);
}
