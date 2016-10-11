package com.griddynamics.jagger.master.configuration;

/**
 * User: nmusienko
 * Date: 12.02.13
 * Time: 11:27
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
