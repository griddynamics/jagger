package com.griddynamics.jagger.jaas.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEqualCollection;

@Entity
@Table(name = "test_environment_entity")
public class TestEnvironmentEntity {
    public enum TestEnvironmentStatus {
        PENDING, RUNNING
    }

    @Id
    @Column(name = "`environment_id`")
    private String environmentId;

    @OneToMany(mappedBy = "testEnvironmentEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSuiteEntity> testSuites;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TestEnvironmentStatus status = TestEnvironmentStatus.PENDING;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "`running_test_suite`")
    private TestSuiteEntity runningTestSuite;

    @JsonIgnore
    @Column(name = "`expiration_timestamp`")
    private long expirationTimestamp;

    @JsonIgnore
    @Column(name = "`session_id`")
    private String sessionId;

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public List<TestSuiteEntity> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestSuiteEntity> testSuites) {
        this.testSuites = testSuites;
    }

    public TestEnvironmentStatus getStatus() {
        return status;
    }

    public void setStatus(TestEnvironmentStatus status) {
        this.status = status;
    }

    public TestSuiteEntity getRunningTestSuite() {
        return runningTestSuite;
    }

    public void setRunningTestSuite(TestSuiteEntity runningTestSuite) {
        this.runningTestSuite = runningTestSuite;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TestEnvironmentEntity that = (TestEnvironmentEntity) obj;

        if (!environmentId.equals(that.environmentId)) return false;
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;
        if (testSuites != null && that.testSuites == null || testSuites == null && that.testSuites != null) return false;
        if (testSuites != null && that.getTestSuites() != null && !isEqualCollection(testSuites, that.testSuites)) return false;
        if (status != that.status) return false;
        if (expirationTimestamp != that.expirationTimestamp) return false;
        return runningTestSuite != null ? runningTestSuite.equals(that.runningTestSuite) : that.runningTestSuite == null;

    }

    @Override
    public int hashCode() {
        int result = environmentId.hashCode();
        result = 31 * result + (testSuites != null ? testSuites.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + (runningTestSuite != null ? runningTestSuite.hashCode() : 0);
        result = 31 * result + Long.hashCode(expirationTimestamp);
        result = 31 * result + sessionId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TestEnvironmentEntity{"
                + "environmentId='" + environmentId + '\''
                + ", testSuites=" + testSuites
                + ", status=" + status
                + ", runningTestSuite=" + runningTestSuite
                + ", expirationTimestamp=" + expirationTimestamp
                + ", sessionId=" + sessionId
                + '}';
    }
}
