package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;

import java.util.List;

public interface ProjectEntityDao {

    ProjectEntity read(Long projectId);

    List<ProjectEntity> readAll();

    void create(ProjectEntity project);

    void update(ProjectEntity project);

    void createOrUpdate(ProjectEntity project);

    void delete(ProjectEntity project);
}
