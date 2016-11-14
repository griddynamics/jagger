package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;

public interface JobExecutionService {
    JobExecutionEntity create(JobExecutionEntity job);

    void delete(Long jobId);
}
