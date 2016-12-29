package com.griddynamics.jagger.jaas.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

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

@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private List<LoadScenarioEntity> loadScenarios;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TestEnvironmentStatus status = TestEnvironmentStatus.PENDING;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "`running_load_scenario`")
    private LoadScenarioEntity runningLoadScenario;

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

    public List<LoadScenarioEntity> getLoadScenarios() {
        return loadScenarios;
    }

    public void setLoadScenarios(List<LoadScenarioEntity> loadScenarios) {
        this.loadScenarios = loadScenarios;
    }

    public TestEnvironmentStatus getStatus() {
        return status;
    }

    public void setStatus(TestEnvironmentStatus status) {
        this.status = status;
    }

    public LoadScenarioEntity getRunningLoadScenario() {
        return runningLoadScenario;
    }

    public void setRunningLoadScenario(LoadScenarioEntity runningLoadScenario) {
        this.runningLoadScenario = runningLoadScenario;
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
        if (loadScenarios != null && that.loadScenarios == null || loadScenarios == null && that.loadScenarios != null) return false;
        if (loadScenarios != null && that.getLoadScenarios() != null && !isEqualCollection(loadScenarios, that.loadScenarios)) return false;
        if (status != that.status) return false;
        if (expirationTimestamp != that.expirationTimestamp) return false;
        return runningLoadScenario != null ? runningLoadScenario.equals(that.runningLoadScenario) : that.runningLoadScenario == null;

    }

    @Override
    public int hashCode() {
        int result = environmentId.hashCode();
        result = 31 * result + (loadScenarios != null ? loadScenarios.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + (runningLoadScenario != null ? runningLoadScenario.hashCode() : 0);
        result = 31 * result + Long.hashCode(expirationTimestamp);
        result = 31 * result + sessionId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TestEnvironmentEntity{"
                + "environmentId='" + environmentId + '\''
                + ", loadScenarios=" + loadScenarios
                + ", status=" + status
                + ", runningLoadScenario=" + runningLoadScenario
                + ", expirationTimestamp=" + expirationTimestamp
                + ", sessionId=" + sessionId
                + '}';
    }
}
