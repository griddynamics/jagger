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
 * This entity represents Load Scenario configuration. Currently it has only loadScenarioId field, but in future it will be extended
 * with more fields.
 */
@Entity
@Table(name = "load_scenario_entity", uniqueConstraints = @UniqueConstraint(columnNames = {"`load_scenario_id`", "`environment_id`"}))
public class LoadScenarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "`load_scenario_id`", nullable = false)
    private String loadScenarioId;
    
    public LoadScenarioEntity() {
    }
    
    public LoadScenarioEntity(String loadScenarioId) {
        this.loadScenarioId = loadScenarioId;
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

    public String getLoadScenarioId() {
        return loadScenarioId;
    }

    public void setLoadScenarioId(String loadScenarioId) {
        this.loadScenarioId = loadScenarioId;
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

        LoadScenarioEntity that = (LoadScenarioEntity) obj;

        if (loadScenarioId != null ? !loadScenarioId.equals(that.loadScenarioId) : that.loadScenarioId != null) return false;
        String envId = testEnvironmentEntity != null ? testEnvironmentEntity.getEnvironmentId() : null;
        String thatEnvId = that.testEnvironmentEntity != null ? that.testEnvironmentEntity.getEnvironmentId() : null;
        return envId != null ? envId.equals(thatEnvId) : thatEnvId == null;

    }

    @Override
    public int hashCode() {
        int result = loadScenarioId != null ? loadScenarioId.hashCode() : 0;
        String envId = testEnvironmentEntity != null ? testEnvironmentEntity.getEnvironmentId() : null;
        result = 31 * result + (envId != null ? envId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoadScenarioEntity{" +
                "loadScenarioId='" + loadScenarioId + '\'' +
                ", testEnvironmentId=" + (testEnvironmentEntity != null ? testEnvironmentEntity.getEnvironmentId() : null) +
                '}';
    }
}
