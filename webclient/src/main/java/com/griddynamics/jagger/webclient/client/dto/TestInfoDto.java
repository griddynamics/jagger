package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * User: amikryukov
 * Date: 2/14/14
 */
public class TestInfoDto implements Serializable {

    private String termination;
    private String clock;

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
}
