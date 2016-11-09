package com.griddynamics.jagger.jaas.storage.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String envId;

    @NotNull
    private String testSuiteId;

    private Long jobStartTimeoutInSeconds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    public Long getJobStartTimeoutInSeconds() {
        return jobStartTimeoutInSeconds;
    }

    public void setJobStartTimeoutInSeconds(Long jobStartTimeoutInSeconds) {
        this.jobStartTimeoutInSeconds = jobStartTimeoutInSeconds;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        JobEntity jobEntity = (JobEntity) obj;

        if (id != null ? !id.equals(jobEntity.id) : jobEntity.id != null) return false;
        if (envId != null ? !envId.equals(jobEntity.envId) : jobEntity.envId != null) return false;
        if (testSuiteId != null ? !testSuiteId.equals(jobEntity.testSuiteId) : jobEntity.testSuiteId != null) return false;

        return jobStartTimeoutInSeconds != null ? jobStartTimeoutInSeconds.equals(jobEntity.jobStartTimeoutInSeconds) : jobEntity
                .jobStartTimeoutInSeconds == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (envId != null ? envId.hashCode() : 0);
        result = 31 * result + (testSuiteId != null ? testSuiteId.hashCode() : 0);
        result = 31 * result + (jobStartTimeoutInSeconds != null ? jobStartTimeoutInSeconds.hashCode() : 0);
        return result;
    }
}
