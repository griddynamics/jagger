package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class ProjectDaoImpl implements ProjectDao {

    @Autowired
    SessionFactory sessionFactory;

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

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
        getCurrentSession().delete(project);
    }
}