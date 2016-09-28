package com.griddynamics.jagger.jaas.storage.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author asokol
 *         created 9/28/16
 */
@Entity
public class ProjectEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String description;

    @NotNull
    @Column(nullable = false)
    private String zipPath;

    @Column
    private Long dbId;

    @Column
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
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ProjectEntity{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", zipPath='" + zipPath + '\'' +
                ", dbId=" + dbId +
                ", version='" + version + '\'' +
                '}';
    }
}
