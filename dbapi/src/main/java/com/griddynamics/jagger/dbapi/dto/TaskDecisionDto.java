package com.griddynamics.jagger.dbapi.dto;

import com.griddynamics.jagger.util.Decision;

import java.io.Serializable;

public class TaskDecisionDto implements Serializable {

    private Long id;
    private Decision decision;

    public TaskDecisionDto(Long id, Decision decision) {
        this.id = id;
        this.decision = decision;
    }

    public TaskDecisionDto() {}

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
