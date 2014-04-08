package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class TaskDataDto implements Serializable {
    private Set<Long> ids;
    private Set<String> sessionIds;
    private String taskName;
    private String description;
    private int uniqueId;

    public TaskDataDto() {
    }

    public TaskDataDto(long id, String taskName, String description) {
        this.description = description;
        this.ids = new HashSet<Long>();
        ids.add(id);

        this.taskName = taskName;
    }

    public TaskDataDto(Set<Long> ids, String taskName) {
        this.ids = ids;
        this.taskName = taskName;
    }

    public long getId() {
        if (ids == null || ids.size() != 1) {
            throw new UnsupportedOperationException("Cannot return id because of ids is null or its size is not equal 1");
        }
        return ids.iterator().next();
    }

    public Set<Long> getIds() {
        return ids;
    }

    public String getTaskName() {
        return taskName;
    }

    public Set<String> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskDataDto that = (TaskDataDto) o;

        if (uniqueId != that.uniqueId) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (taskName != null ? !taskName.equals(that.taskName) : that.taskName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = taskName != null ? taskName.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + uniqueId;
        return result;
    }

    @Override
    public String toString() {
        return "TaskDataDto{" +
                "ids=" + ids +
                ", taskName='" + taskName + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
