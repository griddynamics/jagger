package com.griddynamics.jagger.dbapi.dto;


import com.griddynamics.jagger.dbapi.entity.DecisionPerSessionEntity;
import com.griddynamics.jagger.util.Decision;

import java.util.List;

public class DecisionPerSessionDto {
    private Long id;
    private String sessionId;
    private Decision decision;
    private List<DecisionPerTestGroupDto> testGroupDecisions;

    public DecisionPerSessionDto(DecisionPerSessionEntity decisionPerSessionEntity) {
        this.id = decisionPerSessionEntity.getId();
        this.sessionId = decisionPerSessionEntity.getSessionId();
        this.decision = Decision.valueOf(decisionPerSessionEntity.getDecision());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public List<DecisionPerTestGroupDto> getTestGroupDecisions() {
        return testGroupDecisions;
    }

    public void setTestGroupDecisions(List<DecisionPerTestGroupDto> testGroupDecisions) {
        this.testGroupDecisions = testGroupDecisions;
    }
}
