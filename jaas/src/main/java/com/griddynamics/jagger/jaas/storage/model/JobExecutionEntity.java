package com.griddynamics.jagger.jaas.storage.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "`job_execution_entity`")
public class JobExecutionEntity {
    public enum JobExecutionStatus {
        PENDING, RUNNING, FINISHED, TIMEOUT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long jobId;

    @Enumerated(EnumType.STRING)
    JobExecutionStatus status;

    List<JobExecutionAuditEntity> auditEntities;
}
