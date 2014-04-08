package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/19/12
 */
public class MarkingDto implements Serializable, Comparable<MarkingDto> {
    private double value;
    private String color;
    private String taskName;

    public MarkingDto() {
    }

    public MarkingDto(double value, String color, String taskName) {
        this.value = value;
        this.color = color;
        this.taskName = taskName;
    }

    public double getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public String getTaskName() {
        return taskName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarkingDto)) return false;

        MarkingDto that = (MarkingDto) o;

        if (Double.compare(that.value, value) != 0) return false;
        if (taskName != null ? !taskName.equals(that.taskName) : that.taskName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = value != +0.0d ? new Double(value).hashCode() : 0L;
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (taskName != null ? taskName.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(MarkingDto o) {
        return Double.compare(value, o.value);
    }
}
