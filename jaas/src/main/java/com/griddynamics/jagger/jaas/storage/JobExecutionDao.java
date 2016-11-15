package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;

import java.util.List;

public interface JobExecutionDao  extends CrudDao<JobExecutionEntity, Long> {
    List<JobExecutionEntity> readAllPending();
}