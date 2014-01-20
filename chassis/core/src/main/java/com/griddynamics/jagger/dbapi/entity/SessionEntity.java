package com.griddynamics.jagger.dbapi.entity;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionEntity {
    private String id;
    private String comment;
    private Long startTime;
    private Long endTime;
    private Integer kernels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getKernels() {
        return kernels;
    }

    public void setKernels(Integer kernels) {
        this.kernels = kernels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionEntity that = (SessionEntity) o;

        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (kernels != null ? !kernels.equals(that.kernels) : that.kernels != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (kernels != null ? kernels.hashCode() : 0);
        return result;
    }
}
