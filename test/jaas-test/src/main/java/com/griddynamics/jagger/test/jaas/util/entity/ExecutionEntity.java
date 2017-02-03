package com.griddynamics.jagger.test.jaas.util.entity;

import com.alibaba.fastjson.JSON;


public class ExecutionEntity {
    public enum TestExecutionStatus {
        PENDING, RUNNING, FINISHED, TIMEOUT
    }

    private Long id;
    private String envId;
    private String loadScenarioId;
    private Long executionTimeToStartInSeconds;
    private ExecutionEntity.TestExecutionStatus status;

    public static ExecutionEntity getDefault() {
        ExecutionEntity e = new ExecutionEntity();
        e.setEnvId("envId");
        e.setLoadScenarioId("sid");
        e.setExecutionTimeToStartInSeconds(600L);
        return e;
    }

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

    public Long getExecutionTimeToStartInSeconds() {
        return executionTimeToStartInSeconds;
    }

    public void setExecutionTimeToStartInSeconds(Long executionTimeToStartInSeconds) {
        this.executionTimeToStartInSeconds = executionTimeToStartInSeconds;
    }

    public ExecutionEntity.TestExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionEntity.TestExecutionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutionEntity that = (ExecutionEntity) o;

        if (envId != null ? !envId.equals(that.envId) : that.envId != null) return false;
        if (loadScenarioId != null ? !loadScenarioId.equals(that.loadScenarioId) : that.loadScenarioId != null)
            return false;
        if (executionTimeToStartInSeconds != null ? !executionTimeToStartInSeconds.equals(that.executionTimeToStartInSeconds) :
                that.executionTimeToStartInSeconds != null) return false;
        return status == that.status;

    }

    @Override
    public int hashCode() {
        int result = envId != null ? envId.hashCode() : 0;
        result = 31 * result + (loadScenarioId != null ? loadScenarioId.hashCode() : 0);
        result = 31 * result + (executionTimeToStartInSeconds != null ? executionTimeToStartInSeconds.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestExecutionEntity{" +
                "id=" + id +
                ", envId='" + envId + '\'' +
                ", loadScenarioId='" + loadScenarioId + '\'' +
                ", executionTimeToStartInSeconds=" + executionTimeToStartInSeconds +
                ", status=" + status +
                '}';
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

}
