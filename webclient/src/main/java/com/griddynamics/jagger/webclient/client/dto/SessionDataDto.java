package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class SessionDataDto implements Serializable {
    private String comment;
    private String sessionId;
    private String startDate;
    private String endDate;
    private int activeKernelsCount;
    private int tasksExecuted;
    private int tasksFailed;

    public SessionDataDto() {
    }

    public SessionDataDto(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionDataDto(String sessionId, String startDate, String endDate, int activeKernelsCount, int tasksExecuted, int tasksFailed, String comment) {
        this.sessionId = sessionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activeKernelsCount = activeKernelsCount;
        this.tasksExecuted = tasksExecuted;
        this.tasksFailed = tasksFailed;
        this.comment = comment;
    }

    public String getName() {
        return "Session " + sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getActiveKernelsCount() {
        return activeKernelsCount;
    }

    public int getTasksExecuted() {
        return tasksExecuted;
    }

    public int getTasksFailed() {
        return tasksFailed;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionDataDto)) return false;

        SessionDataDto that = (SessionDataDto) o;

        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sessionId != null ? sessionId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SessionDataDto{" +
                "name='" + getName() + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", activeKernelsCount=" + activeKernelsCount +
                ", tasksExecuted=" + tasksExecuted +
                ", tasksFailed=" + tasksFailed +
                '}';
    }
}
