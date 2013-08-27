package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class MetricNameDto implements Serializable {

    private TaskDataDto tests;
    private String name;

    public MetricNameDto() {
    }

    public MetricNameDto(TaskDataDto tests, String name) {
        this.tests = tests;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskDataDto getTests() {
        return tests;
    }

    public void setTests(TaskDataDto tests) {
        this.tests = tests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricNameDto that = (MetricNameDto) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (tests != null ? !tests.equals(that.tests) : that.tests != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tests != null ? tests.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetricNameDto{" +
                "tests=" + tests +
                ", name='" + name + '\'' +
                '}';
    }
}
