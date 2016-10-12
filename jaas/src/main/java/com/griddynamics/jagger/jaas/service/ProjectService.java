package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.ProjectDao;
import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService implements ProjectDao {

    @Autowired
    private ProjectDao projectDao;

    @Override
    public ProjectEntity read(Long projectId) {
        return projectDao.read(projectId);
    }

    @Override
    public List<ProjectEntity> readAll() {
        return projectDao.readAll();
    }

    @Override
    public void create(ProjectEntity project) {
        projectDao.create(project);
    }

    @Override
    public void update(ProjectEntity project) {
        projectDao.update(project);
    }

    @Override
    public void createOrUpdate(ProjectEntity project) {
        projectDao.createOrUpdate(project);
    }

    @Override
    public void delete(Long projectId) {
        projectDao.delete(projectId);
    }
}
