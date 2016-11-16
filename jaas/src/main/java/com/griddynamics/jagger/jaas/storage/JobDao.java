package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.JobEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JobDao extends CrudDao<JobEntity, Long> {
    @Transactional
    List<JobEntity> readByEnvAndTestSuite(String envId, String testSuiteId);
}