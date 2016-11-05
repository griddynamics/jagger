package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.storage.AbstractCrudDao;
import com.griddynamics.jagger.jaas.storage.DbConfigDao;
import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hibernate based transactional implementation of {@link com.griddynamics.jagger.jaas.storage.CrudDao} interface for
 * {@link DbConfigEntity}.
 */
@SuppressWarnings("unchecked")
@Repository
public class DbConfigDaoImpl extends AbstractCrudDao<DbConfigEntity, Long> implements DbConfigDao {

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
    public void delete(Long configId) {
        DbConfigEntity dbConfigEntity = new DbConfigEntity();
        dbConfigEntity.setId(configId);
        delete(dbConfigEntity);
    }

    @Override
    @Transactional
    public void delete(DbConfigEntity config) {
        getCurrentSession().delete(config);
    }
}
