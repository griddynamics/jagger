package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hibernate based transactional implementation of {@link DbConfigEntityDao} interface.
 */
@SuppressWarnings("unchecked")
@Repository
public class JaasDao implements DbConfigEntityDao {
    
    @Autowired
    SessionFactory sessionFactory;
    
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    @Transactional
    public DbConfigEntity read(Long configId) {
        return (DbConfigEntity) getCurrentSession().get(DbConfigEntity.class, configId);
    }
    
    @Override
    @Transactional
    public List<DbConfigEntity> readAll() {
        return getCurrentSession().createCriteria(DbConfigEntity.class).list();
    }
    
    @Override
    @Transactional
    public void create(DbConfigEntity config) {
        getCurrentSession().save(config);
    }
    
    @Override
    @Transactional
    public void update(DbConfigEntity config) {
        getCurrentSession().update(config);
    }
    
    @Override
    @Transactional
    public void createOrUpdate(DbConfigEntity config) {
        getCurrentSession().saveOrUpdate(config);
    }
    
    @Override
    @Transactional
    public void delete(DbConfigEntity config) {
        getCurrentSession().delete(config);
    }
}
