package com.griddynamics.jagger.webclient.client.dto;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class MetricNameDto extends MetricName {

    private TaskDataDto test;

    public MetricNameDto(){}

    public MetricNameDto(TaskDataDto test, String metricName) {
        super(metricName);
        this.test = test;
    }

    public MetricNameDto(TaskDataDto test, String metricName, String metricDisplayName) {
        super(metricName,metricDisplayName);
        this.test = test;
    }

    public long getTaskId() {
        if (test.getIds() == null || test.getIds().size() != 1) {
            throw new UnsupportedOperationException("Cannot return id because of ids is null or its size is not equal 1");
        }
        return test.getIds().iterator().next();
    }

    public Set<Long> getTaskIds() {
        return test.getIds();
    }

    public TaskDataDto getTest() {
        return test;
    }

    public void setTest(TaskDataDto test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "MetricNameDto{" +
                (test != null ? "taskIds=" + test.getIds() : "") +
                ", metricName='" + metricName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricNameDto that = (MetricNameDto) o;

        if (metricName != null ? !metricName.equals(that.metricName) : that.metricName != null) return false;
        if (test != null ? !test.equals(that.test) : that.test != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = test != null ? test.hashCode() : 0;
        result = 31 * result + (metricName != null ? metricName.hashCode() : 0);
        return result;
    }
}
