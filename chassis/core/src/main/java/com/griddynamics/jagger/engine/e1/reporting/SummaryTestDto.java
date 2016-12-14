package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.util.Decision;

import java.awt.*;
import java.util.Comparator;

/** Class is used to pass information about tests values to jasper report templates */
public class SummaryTestDto {
    
    private String sessionId;
    private String number;
    private String testName;
    private String Id;

    private Decision testStatus;
    private Image statusImage;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Image getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(Image statusImage) {
        this.statusImage = statusImage;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Decision getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(Decision testStatus) {
        this.testStatus = testStatus;
    }

}
