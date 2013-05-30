package com.griddynamics.jagger.master.configuration;

/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 12.02.13
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public enum SessionExecutionStatus {

    EMPTY(null), TERMINATED("terminated"), TASK_FAILED("some tasks failed");

    private String message;

    private SessionExecutionStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
