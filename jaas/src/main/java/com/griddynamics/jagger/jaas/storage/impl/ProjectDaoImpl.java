package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.storage.AbstractCrudDao;
import com.griddynamics.jagger.jaas.storage.ProjectDao;
import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hibernate based transactional implementation of {@link com.griddynamics.jagger.jaas.storage.CrudDao} interface for
 * {@link ProjectEntity}.
 */
@SuppressWarnings("unchecked")
@Repository
public class ProjectDaoImpl extends AbstractCrudDao<ProjectEntity, Long> implements ProjectDao {

    @Override
    @Transactional
    public ProjectEntity read(Long projectId) {
        return (ProjectEntity) getCurrentSession().get(ProjectEntity.class, projectId);
    }

    @Override
    @Transactional
    public List<ProjectEntity> readAll() {
        return getCurrentSession().createCriteria(ProjectEntity.class).list();
    }

    @Override
    @Transactional
    public void create(ProjectEntity project) {
        getCurrentSession().save(project);
    }

    @Override
    @Transactional
    public void update(ProjectEntity project) {
        getCurrentSession().update(project);
    }

    @Override
    @Transactional
    public void createOrUpdate(ProjectEntity project) {
        getCurrentSession().saveOrUpdate(project);
    }

    @Override
    @Transactional
    public void delete(Long projectId) {
        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);
        delete(project);
    }

    @Override
    @Transactional
    public void delete(ProjectEntity project) {
        getCurrentSession().delete(project);
    }
}