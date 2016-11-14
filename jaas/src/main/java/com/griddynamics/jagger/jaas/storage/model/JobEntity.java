package com.griddynamics.jagger.jaas.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "job_entity")
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`env_id`", nullable = false)
    private String envId;

    @Column(name = "`test_suite_id`", nullable = false)
    private String testSuiteId;

    @Column(name = "`job_start_timeout_in_seconds`")
    private Long jobStartTimeoutInSeconds;

    @JsonIgnore
    @OneToOne(mappedBy = "job", cascade = CascadeType.REMOVE)
    private JobExecutionEntity jobExecutionEntity;

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

    public JobExecutionEntity getJobExecutionEntity() {
        return jobExecutionEntity;
    }

    public void setJobExecutionEntity(JobExecutionEntity jobExecutionEntity) {
        this.jobExecutionEntity = jobExecutionEntity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        JobEntity jobEntity = (JobEntity) obj;

        if (envId != null ? !envId.equals(jobEntity.envId) : jobEntity.envId != null) return false;
        if (testSuiteId != null ? !testSuiteId.equals(jobEntity.testSuiteId) : jobEntity.testSuiteId != null) return false;

        return jobStartTimeoutInSeconds != null ? jobStartTimeoutInSeconds.equals(jobEntity.jobStartTimeoutInSeconds) : jobEntity
                .jobStartTimeoutInSeconds == null;

    }

    @Override
    public int hashCode() {
        int result = envId != null ? envId.hashCode() : 0;
        result = 31 * result + (testSuiteId != null ? testSuiteId.hashCode() : 0);
        result = 31 * result + (jobStartTimeoutInSeconds != null ? jobStartTimeoutInSeconds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobEntity{" +
                "id=" + id +
                ", envId='" + envId + '\'' +
                ", testSuiteId='" + testSuiteId + '\'' +
                ", jobStartTimeoutInSeconds=" + jobStartTimeoutInSeconds +
                '}';
    }
}
