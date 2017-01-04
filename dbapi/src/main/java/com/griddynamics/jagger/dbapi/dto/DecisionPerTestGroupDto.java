package com.griddynamics.jagger.dbapi.dto;

import com.griddynamics.jagger.dbapi.entity.DecisionPerTaskEntity;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.util.Decision;

import java.util.List;

public class DecisionPerTestGroupDto {
    private Long id;
    private TaskData taskData;
    private Decision decision;
    private List<DecisionPerTaskDto> taskDecisions;

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

    public List<DecisionPerTaskDto> getTaskDecisions() {
        return taskDecisions;
    }

    public void setTaskDecisions(List<DecisionPerTaskDto> taskDecisions) {
        this.taskDecisions = taskDecisions;
    }
}
