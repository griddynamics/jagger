package com.griddynamics.jagger.jaas.storage.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "envId")
    private TestEnvironmentEntity testEnvironment;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "testSuiteId")
    private TestSuiteEntity testSuite;

    private Long jobStartTimeoutInSeconds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty(required = true, value = "envId")
    public String getTestEnvironmentId() {
        if (testEnvironment == null) return null;
        return testEnvironment.getEnvironmentId();
    }

    public void setTestEnvironmentId(String testEnvironmentId) {
        TestEnvironmentEntity testEnvironment = new TestEnvironmentEntity();
        testEnvironment.setEnvironmentId(testEnvironmentId);
        this.testEnvironment = testEnvironment;
    }

    @JsonProperty(required = true, value = "testSuiteId")
    public String getTestSuiteId() {
        if (testSuite == null) return null;
        return testSuite.getTestSuiteId();
    }

    public void setTestSuiteId(String testSuiteId) {
        TestSuiteEntity testSuite = new TestSuiteEntity();
        testSuite.setTestSuiteId(testSuiteId);
        this.testSuite = testSuite;
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

        String testEnvironmentId = testEnvironment != null ? testEnvironment.getEnvironmentId() : null;
        String otherTestEnvironmentId = jobEntity.getTestEnvironmentId();
        if (testEnvironmentId != null ? !testEnvironmentId.equals(otherTestEnvironmentId) : otherTestEnvironmentId != null) return false;

        String testSuiteId = testSuite != null ? testSuite.getTestSuiteId() : null;
        String otherTestSuiteId = jobEntity.getTestSuiteId();
        if (testSuiteId != null ? !testSuiteId.equals(otherTestSuiteId) : otherTestSuiteId != null) return false;

        return jobStartTimeoutInSeconds != null ? jobStartTimeoutInSeconds.equals(jobEntity.jobStartTimeoutInSeconds) : jobEntity
                .jobStartTimeoutInSeconds == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        String testEnvironmentId = testEnvironment != null ? testEnvironment.getEnvironmentId() : null;
        result = 31 * result + (testEnvironmentId != null ? testEnvironmentId.hashCode() : 0);
        String testSuiteId = testSuite != null ? testSuite.getTestSuiteId() : null;
        result = 31 * result + (testSuiteId != null ? testSuiteId.hashCode() : 0);
        result = 31 * result + (jobStartTimeoutInSeconds != null ? jobStartTimeoutInSeconds.hashCode() : 0);
        return result;
    }
}
