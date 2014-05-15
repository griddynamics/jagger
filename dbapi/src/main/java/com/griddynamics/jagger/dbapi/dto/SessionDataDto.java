package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class SessionDataDto implements Serializable {
    private String comment;
    private String userComment;
    private String sessionId;
    private String startDate;
    private String endDate;
    private int activeKernelsCount;
    private int tasksExecuted;
    private int tasksFailed;
    private Long id;
    private List<TagDto> tags;

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

    public SessionDataDto(Long id,String sessionId, String startDate, String endDate, int activeKernelsCount, int tasksExecuted, int tasksFailed, String comment, String userComment, List<TagDto> tags) {
        this.id = id;
        this.sessionId = sessionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activeKernelsCount = activeKernelsCount;
        this.tasksExecuted = tasksExecuted;
        this.tasksFailed = tasksFailed;
        this.comment = comment;
        this.userComment = userComment;
        this.tags = tags;
    }

    public List<TagDto> getTags() {
        if (tags != null)
            return tags;
        else
            return new ArrayList<TagDto>();
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
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

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        String tagStr="";
        for (TagDto tagDto : tags)
            tagStr+=tagDto.getName()+" ";
        return "SessionDataDto{" +
                "name='" + getName() + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", activeKernelsCount=" + activeKernelsCount +
                ", tasksExecuted=" + tasksExecuted +
                ", tasksFailed=" + tasksFailed +
                ", comment=" + comment +
                ", userComment=" + userComment +
                ", tags="+ tagStr +
                '}';
    }
}
