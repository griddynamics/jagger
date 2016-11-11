package com.griddynamics.jagger.jaas.storage.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "`job_execution_audit_entity`")
public class JobExecutionAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


}
