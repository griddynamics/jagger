package com.griddynamics.jagger.dbapi.dto;

import com.griddynamics.jagger.dbapi.entity.DecisionPerTaskEntity;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.util.Decision;

import java.util.List;

/**
 * This class is used for /session/{id}/decision rest call in JaaS.
 * It represents a part of decision JSON - decision for test group.
 * Its parent element is {@link DecisionPerSessionDto} and children are {@link DecisionPerTestDto} and {@link DecisionPerMetricDto}.
 */
public class DecisionPerTestGroupDto {
    private Long id;
    private TaskData taskData;
    private Decision decision;
    private List<DecisionPerTestDto> testDecisions;
    private List<DecisionPerMetricDto> metricDecisions;

    public DecisionPerTestGroupDto(DecisionPerTaskEntity decisionPerTaskEntity) {
        this.id = decisionPerTaskEntity.getId();
        this.taskData = decisionPerTaskEntity.getTaskData();
        this.decision = Decision.valueOf(decisionPerTaskEntity.getDecision());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public List<DecisionPerTestDto> getTestDecisions() {
        return testDecisions;
    }

    public void setTestDecisions(List<DecisionPerTestDto> testDecisions) {
        this.testDecisions = testDecisions;
    }

    public List<DecisionPerMetricDto> getMetricDecisions() {
        return metricDecisions;
    }

    public void setMetricDecisions(List<DecisionPerMetricDto> metricDecisions) {
        this.metricDecisions = metricDecisions;
    }
}
