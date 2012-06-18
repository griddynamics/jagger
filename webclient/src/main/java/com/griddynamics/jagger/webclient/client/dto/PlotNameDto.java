package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class PlotNameDto implements Serializable {
    private Set<Long> taskIds;
    private String plotName;

    public PlotNameDto() {
    }

    public PlotNameDto(long taskId, String plotName) {
        this.taskIds = new HashSet<Long>();
        taskIds.add(taskId);

        this.plotName = plotName;
    }

    public PlotNameDto(Set<Long> taskIds, String plotName) {
        this.taskIds = taskIds;
        this.plotName = plotName;
    }

    public long getTaskId() {
        if (taskIds == null || taskIds.size() != 1) {
            throw new UnsupportedOperationException("Cannot return id because of ids is null or its size is not equal 1");
        }
        return taskIds.iterator().next();
    }

    public Set<Long> getTaskIds() {
        return taskIds;
    }

    public String getPlotName() {
        return plotName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlotNameDto)) return false;

        PlotNameDto that = (PlotNameDto) o;

        if (plotName != null ? !plotName.equals(that.plotName) : that.plotName != null) return false;
        if (taskIds != null ? !taskIds.equals(that.taskIds) : that.taskIds != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = taskIds != null ? taskIds.hashCode() : 0;
        result = 31 * result + (plotName != null ? plotName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlotNameDto{" +
                "taskIds=" + taskIds +
                ", plotName='" + plotName + '\'' +
                '}';
    }
}
