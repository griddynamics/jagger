package com.griddynamics.jagger.engine.e1.aggregator.workload.model;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;

import javax.persistence.*;

@Entity
public class CollectorDescription {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String displayName;

    @ManyToOne
    private TaskData taskData;

    public String getDisplay() {
        return displayName == null ? name : displayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectorDescription that = (CollectorDescription) o;

        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (taskData != null ? !taskData.equals(that.taskData) : that.taskData != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (taskData != null ? taskData.hashCode() : 0);
        return result;
    }
}
