package com.griddynamics.jagger.jaas.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * This entity represents Test Suite configuration. Currently it has only testSuiteId field, but in future it will be extended
 * with more fields.
 */
@Entity
@Table(name = "test_suite_entity", uniqueConstraints = @UniqueConstraint(columnNames = {"`test_suite_id`", "`environment_id`"}))
public class TestSuiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "`test_suite_id`", nullable = false)
    private String testSuiteId;
    
    public TestSuiteEntity() {
    }
    
    public TestSuiteEntity(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "`environment_id`")
    private TestEnvironmentEntity testEnvironmentEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

        if (testSuiteId != null ? !testSuiteId.equals(that.testSuiteId) : that.testSuiteId != null) return false;
        String envId = testEnvironmentEntity != null ? testEnvironmentEntity.getEnvironmentId() : null;
        String thatEnvId = that.testEnvironmentEntity != null ? that.testEnvironmentEntity.getEnvironmentId() : null;
        return envId != null ? envId.equals(thatEnvId) : thatEnvId == null;

    }

    @Override
    public int hashCode() {
        int result = testSuiteId != null ? testSuiteId.hashCode() : 0;
        String envId = testEnvironmentEntity != null ? testEnvironmentEntity.getEnvironmentId() : null;
        result = 31 * result + (envId != null ? envId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestSuiteEntity{" +
                "testSuiteId='" + testSuiteId + '\'' +
                ", testEnvironmentId=" + (testEnvironmentEntity != null ? testEnvironmentEntity.getEnvironmentId() : null) +
                '}';
    }
}
