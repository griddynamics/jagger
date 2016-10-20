package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class TaskDataDto implements Serializable {
    
    private Map<Long, String> idToSessionId = new HashMap<Long, String>();
    private String taskName;
    private String description;
    private int uniqueId;

    public TaskDataDto() {
    }

    public TaskDataDto(long id, String sessionId, String taskName, String description) {
        this.description = description;
        idToSessionId.put(id, sessionId);

        this.taskName = taskName;
    }

    public TaskDataDto(Map<Long, String> id2SessionId, String taskName, String description) {
        this.description = description;
        idToSessionId.putAll(id2SessionId);

        this.taskName = taskName;
    } 

    public Map<Long, String> getIdToSessionId() {
        return idToSessionId;
    }

    public long getId() {
        Set<Long> ids = getIds();

        if (ids == null || ids.size() != 1) {
            throw new UnsupportedOperationException("Cannot return id because ids is null or its size is not equal 1");
        }
        return ids.iterator().next();
    }

    public String getSessionId() {
        Set<String> sessionIds = getSessionIds();

        if (sessionIds == null || sessionIds.size() != 1) {
            throw new UnsupportedOperationException("Cannot return sessionId because sessionIds is null or its size is not equal 1");
        }
        return sessionIds.iterator().next();
    }

    public Set<Long> getIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.addAll(idToSessionId.keySet());

        return ids;
    }

    public String getTaskName() {
        return taskName;
    }

    public Set<String> getSessionIds() {
        Set<String> sessionIds = new HashSet<String>();
        for (Map.Entry<Long, String> entry : idToSessionId.entrySet()) {
            sessionIds.add(entry.getValue());
        }

        return sessionIds;
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
                "idToSessionId=" + idToSessionId +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", uniqueId=" + uniqueId +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
