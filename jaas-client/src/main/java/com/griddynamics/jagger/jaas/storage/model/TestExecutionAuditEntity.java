package com.griddynamics.jagger.jaas.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus;

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

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "test_execution_audit_entity")
public class TestExecutionAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "`test_execution_id`", nullable = false)
    private TestExecutionEntity testExecutionEntity;
    
    @Column(nullable = false)
    private long timestamp;

    @Column(name = "`old_status`")
    @Enumerated(EnumType.STRING)
    private TestExecutionStatus oldStatus;

    @Column(name = "`new_status`", nullable = false)
    @Enumerated(EnumType.STRING)
    private TestExecutionStatus newStatus;

    public TestExecutionAuditEntity() {}

    public TestExecutionAuditEntity(TestExecutionEntity testExecutionEntity, long timestamp, TestExecutionStatus oldStatus,
                                    TestExecutionStatus newStatus) {
        this.testExecutionEntity = testExecutionEntity;
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

    public TestExecutionEntity getTestExecutionEntity() {
        return testExecutionEntity;
    }

    public void setTestExecutionEntity(TestExecutionEntity testExecutionEntity) {
        this.testExecutionEntity = testExecutionEntity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public TestExecutionStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(TestExecutionStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public TestExecutionStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(TestExecutionStatus newStatus) {
        this.newStatus = newStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TestExecutionAuditEntity that = (TestExecutionAuditEntity) obj;

        if (timestamp != that.timestamp) return false;
        if (testExecutionEntity != null ? !testExecutionEntity.equals(that.testExecutionEntity) : that.testExecutionEntity != null)
            return false;
        if (oldStatus != that.oldStatus) return false;
        return newStatus == that.newStatus;

    }

    @Override
    public int hashCode() {
        int result = testExecutionEntity != null ? testExecutionEntity.hashCode() : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (oldStatus != null ? oldStatus.hashCode() : 0);
        result = 31 * result + (newStatus != null ? newStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestExecutionAuditEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", oldStatus=" + oldStatus +
                ", newStatus=" + newStatus +
                '}';
    }
}
