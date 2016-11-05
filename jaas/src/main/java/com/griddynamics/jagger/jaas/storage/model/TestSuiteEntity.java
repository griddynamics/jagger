package com.griddynamics.jagger.jaas.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * This entity represents Test Suite configuration. Currently it has only testSuiteId field, but in future it will be extended
 * with more fields.
 */
@Entity
public class TestSuiteEntity {

    @Id
    private String testSuiteId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "environmentId")
    private TestEnvironmentEntity testEnvironmentEntity;

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    public TestEnvironmentEntity getTestEnvironmentEntity() {
        return testEnvironmentEntity;
    }

    public void setTestEnvironmentEntity(TestEnvironmentEntity testEnvironmentEntity) {
        this.testEnvironmentEntity = testEnvironmentEntity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TestSuiteEntity that = (TestSuiteEntity) obj;
        return testSuiteId.equals(that.testSuiteId);
    }

    @Override
    public int hashCode() {
        return testSuiteId.hashCode();
    }

    @Override
    public String toString() {
        return testSuiteId;
    }
}
