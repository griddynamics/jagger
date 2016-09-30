package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.ProjectEntityDao;
import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService implements ProjectEntityDao {

    @Autowired
    private ProjectEntityDao jaasDao;

    @Override
    public ProjectEntity read(Long projectId) {
        return jaasDao.read(projectId);
    }

    @Override
    public List<ProjectEntity> readAll() {
        return jaasDao.readAll();
    }

    @Override
    public void create(ProjectEntity project) {
        jaasDao.create(project);
    }

    @Override
    public void update(ProjectEntity project) {
        jaasDao.update(project);
    }

    @Override
    public void createOrUpdate(ProjectEntity project) {
        jaasDao.createOrUpdate(project);
    }

    @Override
    public void delete(ProjectEntity project) {
        jaasDao.delete(project);
    }
}
