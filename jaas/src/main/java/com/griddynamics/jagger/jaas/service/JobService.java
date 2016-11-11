package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.model.JobEntity;

import java.util.List;

public interface JobService {

    JobEntity read(Long jobId);

    List<JobEntity> readAll();

    JobEntity create(JobEntity job);

    JobEntity update(JobEntity job);

    void delete(Long jobId);
}
