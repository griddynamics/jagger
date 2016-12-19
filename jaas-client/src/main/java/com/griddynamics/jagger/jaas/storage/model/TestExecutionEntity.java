package com.griddynamics.jagger.jaas.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "test_execution_entity")
public class TestExecutionEntity {
    public enum TestExecutionStatus {
        PENDING, RUNNING, FINISHED, TIMEOUT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`env_id`", nullable = false)
    private String envId;

    @Column(name = "`load_scenario_id`", nullable = false)
    private String loadScenarioId;

    @Column(name = "`test_project_url`")
    private String testProjectURL;

    @Column(name = "`execution_start_timeout_in_seconds`")
    private Long executionStartTimeoutInSeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestExecutionStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "testExecutionEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestExecutionAuditEntity> auditEntities;

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

    public String getLoadScenarioId() {
        return loadScenarioId;
    }

    public void setLoadScenarioId(String loadScenarioId) {
        this.loadScenarioId = loadScenarioId;
    }

    public String getTestProjectURL() {
        return testProjectURL;
    }

    public void setTestProjectURL(String testProjectURL) {
        this.testProjectURL = testProjectURL;
    }

    public Long getExecutionStartTimeoutInSeconds() {
        return executionStartTimeoutInSeconds;
    }

    public void setExecutionStartTimeoutInSeconds(Long executionStartTimeoutInSeconds) {
        this.executionStartTimeoutInSeconds = executionStartTimeoutInSeconds;
    }

    public TestExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(TestExecutionStatus status) {
        this.status = status;
    }

    public Set<TestExecutionAuditEntity> getAuditEntities() {
        return auditEntities;
    }

    public void setAuditEntities(Set<TestExecutionAuditEntity> auditEntities) {
        this.auditEntities = auditEntities;
    }

    public void addAuditEntity(TestExecutionAuditEntity auditEntity) {
        if (auditEntities == null)
            auditEntities = new HashSet<>();
        auditEntities.add(auditEntity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestExecutionEntity that = (TestExecutionEntity) o;

        if (envId != null ? !envId.equals(that.envId) : that.envId != null) return false;
        if (loadScenarioId != null ? !loadScenarioId.equals(that.loadScenarioId) : that.loadScenarioId != null) return false;
        if (executionStartTimeoutInSeconds != null ? !executionStartTimeoutInSeconds.equals(that.executionStartTimeoutInSeconds) :
                that.executionStartTimeoutInSeconds != null) return false;
        if (testProjectURL != null ? !testProjectURL.equals(that.testProjectURL) : that.testProjectURL != null) return false;
        return status == that.status;

    }

    @Override
    public int hashCode() {
        int result = envId != null ? envId.hashCode() : 0;
        result = 31 * result + (loadScenarioId != null ? loadScenarioId.hashCode() : 0);
        result = 31 * result + (testProjectURL != null ? testProjectURL.hashCode() : 0);
        result = 31 * result + (executionStartTimeoutInSeconds != null ? executionStartTimeoutInSeconds.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestExecutionEntity{" +
                "id=" + id +
                ", envId='" + envId + '\'' +
                ", loadScenarioId='" + loadScenarioId + '\'' +
                ", testProjectURL='" + testProjectURL + '\'' +
                ", executionStartTimeoutInSeconds=" + executionStartTimeoutInSeconds +
                ", status=" + status +
                ", auditEntities=" + auditEntities +
                '}';
    }
}
