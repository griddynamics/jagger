package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;

import java.util.List;

public interface ProjectService {
    ProjectEntity read(Long projectId);

    List<ProjectEntity> readAll();

    void create(ProjectEntity project);

    void update(ProjectEntity project);

    void createOrUpdate(ProjectEntity project);

    void delete(Long projectId);
}
