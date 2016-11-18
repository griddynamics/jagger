package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.storage.AbstractCrudDao;
import com.griddynamics.jagger.jaas.storage.TestExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.PENDING;
import static org.hibernate.criterion.CriteriaSpecification.DISTINCT_ROOT_ENTITY;

/**
 * Hibernate based transactional implementation of {@link com.griddynamics.jagger.jaas.storage.CrudDao} interface for {@link TestExecutionEntity}.
 */
@SuppressWarnings("unchecked")
@Repository
public class TestExecutionDaoImpl extends AbstractCrudDao<TestExecutionEntity, Long> implements TestExecutionDao {

    @Override
    @Transactional
    public TestExecutionEntity read(Long testExecutionId) {
        return (TestExecutionEntity) getCurrentSession().get(TestExecutionEntity.class, testExecutionId);
    }

    @Override
    @Transactional
    public List<TestExecutionEntity> readAll() {
        return getCurrentSession().createCriteria(TestExecutionEntity.class).setResultTransformer(DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    @Transactional
    public List<TestExecutionEntity> readByEnvAndLoadScenario(String envId, String loadScenarioId) {
        Query query = getCurrentSession().createQuery("select t from TestExecutionEntity t where t.envId=:envId and t.loadScenarioId=:scenarioId");
        query.setString("envId", envId);
        query.setString("scenarioId", loadScenarioId);
        return (List<TestExecutionEntity>) query.list();
    }

    @Override
    @Transactional
    public List<TestExecutionEntity> readAllPending() {
        Query query = getCurrentSession().createQuery("select t from TestExecutionEntity t where t.status = :status");
        query.setString("status", PENDING.name());
        return query.list();
    }

    @Override
    @Transactional
    public void create(TestExecutionEntity testExecution) {
        getCurrentSession().save(testExecution);
    }

    @Override
    @Transactional
    public void create(Iterable<TestExecutionEntity> entities) {
        Objects.requireNonNull(entities);
        entities.forEach(testExecution -> getCurrentSession().save(testExecution));
    }

    @Override
    @Transactional
    public void update(TestExecutionEntity testExecution) {
        getCurrentSession().update(testExecution);
    }

    @Override
    @Transactional
    public void createOrUpdate(TestExecutionEntity testExecution) {
        getCurrentSession().saveOrUpdate(testExecution);
    }

    @Override
    @Transactional
    public void delete(Long testExecutionId) {
        TestExecutionEntity testExecution = read(testExecutionId);
        delete(testExecution);
    }

    @Override
    @Transactional
    public void delete(TestExecutionEntity testExecution) {
        getCurrentSession().delete(testExecution);
    }

    @Override
    @Transactional
    public boolean exists(Long testExecutionId) {
        Query query = getCurrentSession().createQuery("select 1 from TestExecutionEntity t where t.id = :id");
        query.setLong("id", testExecutionId);
        return query.uniqueResult() != null;
    }
}
