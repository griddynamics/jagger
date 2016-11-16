package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.JobEntity;

import java.util.List;

public interface JobDao extends CrudDao<JobEntity, Long> {
    List<JobEntity> readByEnvAndTestSuite(String envId, String testSuiteId);
}