package com.griddynamics.jagger.jaas.storage.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_execution_entity")
public class JobExecutionEntity {
    public enum JobExecutionStatus {
        PENDING, RUNNING, FINISHED, TIMEOUT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "`job_id`")
    private JobEntity job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobExecutionStatus status;

    @OneToMany(mappedBy = "jobExecutionEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobExecutionAuditEntity> auditEntities;

    public JobExecutionEntity() {}

    public JobExecutionEntity(Long jobId) {
        setJobId(jobId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return job != null ? job.getId() : null;
    }

    public JobEntity getJob() {
        return job;
    }

    public void setJobId(Long jobId) {
        JobEntity jobEntity = new JobEntity();
        jobEntity.setId(jobId);
        this.job = jobEntity;
    }

    public JobExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(JobExecutionStatus status) {
        this.status = status;
    }

    public List<JobExecutionAuditEntity> getAuditEntities() {
        return auditEntities;
    }

    public void setAuditEntities(List<JobExecutionAuditEntity> auditEntities) {
        this.auditEntities = auditEntities;
    }

    public void addAuditEntity(JobExecutionAuditEntity auditEntity) {
        if (auditEntities == null)
            auditEntities = new ArrayList<>();
        auditEntities.add(auditEntity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        JobExecutionEntity that = (JobExecutionEntity) obj;

        Long jobId = job != null ? job.getId() : null;
        Long otherJobId = that.getId();
        if (jobId != null ? !jobId.equals(otherJobId) : otherJobId != null) return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        Long jobId = job != null ? job.getId() : null;
        int result = jobId != null ? jobId.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobExecutionEntity{" +
                "id=" + id +
                ", job=" + (job != null ? job.getId() : null) +
                ", status=" + status +
                ", auditEntities=" + auditEntities +
                '}';
    }
}
