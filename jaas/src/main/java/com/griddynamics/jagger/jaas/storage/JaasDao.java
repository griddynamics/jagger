package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.JaggerTestDbConfig;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hibernate based transactional implementation of {@link JaggerTestDbConfigDao} interface.
 */
@SuppressWarnings("unchecked")
@Repository
public class JaasDao implements JaggerTestDbConfigDao {
    
    @Autowired
    SessionFactory sessionFactory;
    
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    @Transactional
    public JaggerTestDbConfig read(String configName) {
        return (JaggerTestDbConfig) getCurrentSession().get(JaggerTestDbConfig.class, configName);
    }
    
    @Override
    @Transactional
    public List<JaggerTestDbConfig> readAll() {
        return getCurrentSession().createCriteria(JaggerTestDbConfig.class).list();
    }
    
    @Override
    @Transactional
    public void createOrUpdate(JaggerTestDbConfig config) {
        getCurrentSession().saveOrUpdate(config);
    }
    
    @Override
    @Transactional
    public void delete(JaggerTestDbConfig config) {
        getCurrentSession().delete(config);
    }
}
