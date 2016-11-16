package com.griddynamics.jagger.jaas.storage.model;

import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "job_execution_audit_entity")
public class JobExecutionAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "`job_execution_id`", nullable = false)
    private JobExecutionEntity jobExecutionEntity;

    @Column(nullable = false)
    private long timestamp;

    @Column(name = "`old_status`")
    @Enumerated(EnumType.STRING)
    private JobExecutionStatus oldStatus;

    @Column(name = "`new_status`", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobExecutionStatus newStatus;

    public JobExecutionAuditEntity() {}

    public JobExecutionAuditEntity(JobExecutionEntity jobExecutionEntity, long timestamp, JobExecutionStatus oldStatus,
                                   JobExecutionStatus newStatus) {
        this.jobExecutionEntity = jobExecutionEntity;
        this.timestamp = timestamp;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobExecutionEntity getJobExecutionEntity() {
        return jobExecutionEntity;
    }

    public void setJobExecutionEntity(JobExecutionEntity jobExecutionEntity) {
        this.jobExecutionEntity = jobExecutionEntity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public JobExecutionStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(JobExecutionStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public JobExecutionStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(JobExecutionStatus newStatus) {
        this.newStatus = newStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        JobExecutionAuditEntity that = (JobExecutionAuditEntity) obj;

        if (timestamp != that.timestamp) return false;
        if (jobExecutionEntity != null ? !jobExecutionEntity.equals(that.jobExecutionEntity) : that.jobExecutionEntity != null)
            return false;
        if (oldStatus != that.oldStatus) return false;
        return newStatus == that.newStatus;

    }

    @Override
    public int hashCode() {
        int result = jobExecutionEntity != null ? jobExecutionEntity.hashCode() : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (oldStatus != null ? oldStatus.hashCode() : 0);
        result = 31 * result + (newStatus != null ? newStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobExecutionAuditEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", oldStatus=" + oldStatus +
                ", newStatus=" + newStatus +
                '}';
    }
}
