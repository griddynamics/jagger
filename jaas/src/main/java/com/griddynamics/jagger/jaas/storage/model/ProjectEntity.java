package com.griddynamics.jagger.jaas.storage.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class ProjectEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String description;

    @NotNull
    @Column(nullable = false)
    private String zipPath;

    @ManyToOne
    @JoinColumn(name = "dbId")
    private DbConfigEntity dbId;

    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public Long getDbId() {
        return dbId.getId();
    }


    public void setDbId(Long dbId) {
        DbConfigEntity dbConfigEntity = new DbConfigEntity();
        dbConfigEntity.setId(dbId);
        this.dbId = dbConfigEntity;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ProjectEntity{"
                + "id=" + id
                + ", description='" + description + '\''
                + ", zipPath='" + zipPath + '\''
                + ", dbId=" + dbId
                + ", version='" + version + '\''
                + '}';
    }
}
