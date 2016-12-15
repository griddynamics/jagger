package com.griddynamics.jagger.test.jaas.util.entity;

import com.alibaba.fastjson.JSON;


public class ExecutionEntity {
    public enum TestExecutionStatus {
        PENDING, RUNNING, FINISHED, TIMEOUT
    }

    private Long id;
    private String envId;
    private String loadScenarioId;
    private Long executionStartTimeoutInSeconds;
    private ExecutionEntity.TestExecutionStatus status;

    public static ExecutionEntity getDefault() {
        ExecutionEntity e = new ExecutionEntity();
        e.setEnvId("1");
        e.setLoadScenarioId("sid");
        e.setExecutionStartTimeoutInSeconds(0L);
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

    public Long getExecutionStartTimeoutInSeconds() {
        return executionStartTimeoutInSeconds;
    }

    public void setExecutionStartTimeoutInSeconds(Long executionStartTimeoutInSeconds) {
        this.executionStartTimeoutInSeconds = executionStartTimeoutInSeconds;
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
        if (executionStartTimeoutInSeconds != null ? !executionStartTimeoutInSeconds.equals(that.executionStartTimeoutInSeconds) :
                that.executionStartTimeoutInSeconds != null) return false;
        return status == that.status;

    }

    @Override
    public int hashCode() {
        int result = envId != null ? envId.hashCode() : 0;
        result = 31 * result + (loadScenarioId != null ? loadScenarioId.hashCode() : 0);
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
                ", executionStartTimeoutInSeconds=" + executionStartTimeoutInSeconds +
                ", status=" + status +
                '}';
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

}
