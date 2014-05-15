package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class MetricValueDto implements Serializable {

    private long testId;
    private long sessionId;
    private String value;
    private String valueRepresentation;

    public String getValueRepresentation() {
        return valueRepresentation;
    }

    public void setValueRepresentation(String valueRepresentation) {
        this.valueRepresentation = valueRepresentation;
    }

    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
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
}
