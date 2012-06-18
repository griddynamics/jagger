package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class WorkloadDetailsDto implements Serializable {
    private long id;
    private String name;

    public WorkloadDetailsDto() {
    }

    public WorkloadDetailsDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkloadDetailsDto)) return false;

        WorkloadDetailsDto that = (WorkloadDetailsDto) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "WorkloadDetailsDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
