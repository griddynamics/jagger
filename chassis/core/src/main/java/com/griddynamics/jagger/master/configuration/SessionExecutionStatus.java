package com.griddynamics.jagger.master.configuration;

/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 13.02.13
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */
public class SessionExecutionStatus {

    private SessionErrorStatus status;

    public SessionErrorStatus getStatus() {
        return status;
    }

    public void setStatus(SessionErrorStatus status) {
        this.status = status;
    }
}
