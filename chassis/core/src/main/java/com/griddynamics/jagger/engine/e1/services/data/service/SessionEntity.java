package com.griddynamics.jagger.engine.e1.services.data.service;

/** Class is a model of session
 *
 * @details
 * SessionEntity is used to get test results from database with use of @ref DataService
 *
 * @author
 * Gribov Kirill
 */
public class SessionEntity {

    /** Session id */
    private String id;

    /** Session comment */
    private String comment;

    /** Start time */
    private String startDate;

    /** Stop time */
    private String endDate;

    /** Number of kernels used for workload generation */
    private Integer kernels;

    /** Get session id */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** Get session comment */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /** Get start time */
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /** Get stop time */
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /** Get number of kernels used for workload generation */
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
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (kernels != null ? !kernels.equals(that.kernels) : that.kernels != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (kernels != null ? kernels.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SessionEntity{" +
                "id='" + id + '\'' +
                ", comment='" + comment + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", kernels=" + kernels +
                '}';
    }
}
