package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;

import java.util.List;

/**
 * @author asokol
 *         created 9/28/16
 */
public interface ProjectEntityDao {

    ProjectEntity read(Long projectId);

    List<ProjectEntity> readAll();

    void create(ProjectEntity config);

    void update(ProjectEntity config);

    void createOrUpdate(ProjectEntity config);

    void delete(ProjectEntity config);
}
