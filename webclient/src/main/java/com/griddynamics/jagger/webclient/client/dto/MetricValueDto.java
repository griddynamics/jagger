package com.griddynamics.jagger.webclient.client.dto;

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
        this.value = value;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}
