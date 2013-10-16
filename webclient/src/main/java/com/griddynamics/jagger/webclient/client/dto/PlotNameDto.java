package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class PlotNameDto implements Serializable {

    private TaskDataDto test;
    private String plotName;
    private String displayName;

    public PlotNameDto(){}

    public PlotNameDto(TaskDataDto test, String plotName) {
        this.test = test;
        this.plotName = plotName;
    }

    public PlotNameDto(TaskDataDto test, String plotName, String displayName) {
        this.test = test;
        this.plotName = plotName;
        this.displayName = displayName;
    }

    public PlotNameDto(PlotNameDto other) {
        this.plotName = other.getPlotName();
        this.displayName = other.getDisplayName();
        this.test = other.getTest();
    }

    public long getTaskId() {
        if (test.getIds() == null || test.getIds().size() != 1) {
            throw new UnsupportedOperationException("Cannot return id because of ids is null or its size is not equal 1");
        }
        return test.getIds().iterator().next();
    }

    public String getDisplayName() {
        if (displayName == null || displayName.isEmpty()) {
            displayName = plotName;
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<Long> getTaskIds() {
        return test.getIds();
    }

    public String getPlotName() {
        return plotName;
    }

    public TaskDataDto getTest() {
        return test;
    }

    public void setTest(TaskDataDto test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "PlotNameDto{" +
                "taskIds=" + test.getIds() +
                ", plotName='" + plotName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlotNameDto that = (PlotNameDto) o;

        if (plotName != null ? !plotName.equals(that.plotName) : that.plotName != null) return false;
        if (test != null ? !test.equals(that.test) : that.test != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = test != null ? test.hashCode() : 0;
        result = 31 * result + (plotName != null ? plotName.hashCode() : 0);
        return result;
    }
}
