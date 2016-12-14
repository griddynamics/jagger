package com.griddynamics.jagger.dbapi.dto;

import com.griddynamics.jagger.util.Decision;

import java.io.Serializable;
import java.util.Date;

/**
 * User: amikryukov
 * Date: 2/14/14
 */
public class TestInfoDto implements Serializable {

    private String termination;
    private String clock;
    private Integer clockValue;
    private Date startTime;
    private Date endTime;
    // index of test group where this test was executed
    private Integer number;
    // status of this test (FATAL if task failed during configuration - f.e. by timeout)
    private Decision status;

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Decision getStatus() {
        return status;
    }

    public void setStatus(Decision status) {
        this.status = status;
    }

    public Integer getClockValue() {
        return clockValue;
    }

    public void setClockValue(Integer clockValue) {
        this.clockValue = clockValue;
    }

}
