package com.griddynamics.jagger.dbapi.dto;

import com.griddynamics.jagger.util.Decision;

import java.io.Serializable;

public class TaskDecisionDto implements Serializable {

    private Long id;
    private String name;
    private Decision decision;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
