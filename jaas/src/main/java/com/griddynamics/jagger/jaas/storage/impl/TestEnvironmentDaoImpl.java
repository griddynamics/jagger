package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.storage.AbstractCrudDao;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.hibernate.criterion.CriteriaSpecification.DISTINCT_ROOT_ENTITY;
import static org.hibernate.criterion.Projections.rowCount;

/**
 * Hibernate based transactional implementation of {@link com.griddynamics.jagger.jaas.storage.CrudDao} interface for
 * {@link TestEnvironmentEntity}.
 */
@SuppressWarnings("unchecked")
@Repository
public class TestEnvironmentDaoImpl extends AbstractCrudDao<TestEnvironmentEntity, String> implements TestEnvironmentDao {

    @Override
    @Transactional
    public TestEnvironmentEntity read(String testEnvironmentId) {
        return (TestEnvironmentEntity) getCurrentSession().get(TestEnvironmentEntity.class, testEnvironmentId);
    }

    @Override
    @Transactional
    public List<TestEnvironmentEntity> readAll() {
        return getCurrentSession().createCriteria(TestEnvironmentEntity.class).setResultTransformer(DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    @Transactional
    public void create(TestEnvironmentEntity testEnvironment) {
        getCurrentSession().save(testEnvironment);
    }

    @Override
    @Transactional
    public void create(Iterable<TestEnvironmentEntity> testEnvironments) {
        Objects.requireNonNull(testEnvironments);
        testEnvironments.forEach(env -> getCurrentSession().save(env));
    }

    @Override
    @Transactional
    public void update(TestEnvironmentEntity testEnvironment) {
        getCurrentSession().update(testEnvironment);
    }

    @Override
    @Transactional
    public void createOrUpdate(TestEnvironmentEntity testEnvironment) {
        getCurrentSession().saveOrUpdate(testEnvironment);
    }

    @Override
    @Transactional
    public void delete(String testEnvironmentId) {
        TestEnvironmentEntity testEnvironmentEntity =
                (TestEnvironmentEntity) getCurrentSession().get(TestEnvironmentEntity.class, testEnvironmentId);
        delete(testEnvironmentEntity);
    }

    @Override
    @Transactional
    public void delete(TestEnvironmentEntity testEnvironment) {
        getCurrentSession().delete(testEnvironment);
    }

    @Override
    @Transactional
    public void delete(Iterable<TestEnvironmentEntity> testEnvs) {
        testEnvs.forEach(this::delete);
    }

    @Override
    @Transactional
    public long count() {
        return (Long) getCurrentSession().createCriteria(TestEnvironmentEntity.class).setProjection(rowCount()).uniqueResult();
    }

    @Override
    @Transactional
    public boolean exists(String testEnvironmentId) {
        Query query = getCurrentSession().createQuery("select 1 from TestEnvironmentEntity t where t.environmentId = :id");
        query.setString("id", testEnvironmentId);
        return query.uniqueResult() != null;
    }

    @Override
    @Transactional
    public boolean existsWithSessionId(String testEnvironmentId, String sessionId) {
        Query query = getCurrentSession()
                .createQuery("select 1 from TestEnvironmentEntity t where t.environmentId = :id and t.sessionId = :sessionId" );
        query.setString("id", testEnvironmentId);
        query.setString("sessionId", sessionId);
        return query.uniqueResult() != null;
    }

    @Override
    @Transactional
    public List<TestEnvironmentEntity> readExpired(long timestamp) {
        Query query = getCurrentSession().createQuery("select t from TestEnvironmentEntity t where t.expirationTimestamp < :timestamp");
        query.setLong("timestamp", timestamp);
        return query.list();
    }

    @Override
    @Transactional
    public int deleteExpired(long timestamp) {
        List<TestEnvironmentEntity> expiredEnvs = readExpired(timestamp);
        delete(expiredEnvs);
        return expiredEnvs.size();
    }
}
