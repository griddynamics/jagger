package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.ProjectEntityDao;
import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;

import java.util.List;

/**
 * @author asokol
 *         created 9/28/16
 */
public class ProjectService implements ProjectEntityDao{
    @Override
    public ProjectEntity read(Long projectId) {
        return null;
    }

    @Override
    public List<ProjectEntity> readAll() {
        return null;
    }

    @Override
    public void create(ProjectEntity config) {

    }

    @Override
    public void update(ProjectEntity config) {

    }

    @Override
    public void createOrUpdate(ProjectEntity config) {

    }

    @Override
    public void delete(ProjectEntity config) {

    }
}
