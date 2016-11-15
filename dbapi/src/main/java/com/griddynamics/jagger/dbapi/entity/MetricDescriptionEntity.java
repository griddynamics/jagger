package com.griddynamics.jagger.dbapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class MetricDescriptionEntity {

    @Id
    // Identity strategy is not supported by Oracle DB from the box
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String metricId;

    @Column(length = 100)
    private String displayName;

    @ManyToOne
    private TaskData taskData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String name) {
        this.metricId = name;
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

    public String getDisplay() {
        return displayName == null ? metricId : displayName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricDescriptionEntity that = (MetricDescriptionEntity) o;

        if (metricId != null ? !metricId.equals(that.metricId) : that.metricId != null) return false;
        if (taskData != null ? !taskData.equals(that.taskData) : that.taskData != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = metricId != null ? metricId.hashCode() : 0;
        result = 31 * result + (taskData != null ? taskData.hashCode() : 0);
        return result;
    }
}
