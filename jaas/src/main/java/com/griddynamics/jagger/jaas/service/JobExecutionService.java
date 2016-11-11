package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;

import java.util.List;

public interface JobExecutionService {
    JobExecutionEntity read(Long jobId);

    List<JobExecutionEntity> readAll();

    JobExecutionEntity create(JobExecutionEntity job);

    JobExecutionEntity update(JobExecutionEntity job);

    void delete(Long jobId);
}
