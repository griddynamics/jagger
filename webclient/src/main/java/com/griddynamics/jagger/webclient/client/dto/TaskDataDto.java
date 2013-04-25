package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class TaskDataDto implements Serializable {
    private Set<Long> ids;
    private String taskName;

    public TaskDataDto() {
    }

    public TaskDataDto(long id, String taskName) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskDataDto)) return false;

        TaskDataDto that = (TaskDataDto) o;

        if (ids != null ? !ids.equals(that.ids) : that.ids != null) return false;
        if (taskName != null ? !taskName.equals(that.taskName) : that.taskName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ids != null ? ids.hashCode() : 0;
        result = 31 * result + (taskName != null ? taskName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaskDataDto{" +
                "ids=" + ids +
                ", taskName='" + taskName + '\'' +
                '}';
    }
}
