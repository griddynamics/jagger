package com.griddynamics.jagger.dbapi.dto;

import com.griddynamics.jagger.util.Decision;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class SummaryMetricValueDto implements Serializable {

    private long sessionId;
    private String value;
    private String valueRepresentation;
    private Decision decision = null; //null => no decision

    public String getValueRepresentation() {
        return valueRepresentation;
    }

    public void setValueRepresentation(String valueRepresentation) {
        this.valueRepresentation = valueRepresentation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (valueRepresentation == null) {
            this.valueRepresentation = value;
        }
        this.value = value;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }
}
