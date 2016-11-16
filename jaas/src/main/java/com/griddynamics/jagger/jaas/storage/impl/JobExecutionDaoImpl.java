package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.storage.AbstractCrudDao;
import com.griddynamics.jagger.jaas.storage.JobExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.PENDING;
import static org.hibernate.criterion.CriteriaSpecification.DISTINCT_ROOT_ENTITY;

/**
 * Hibernate based transactional implementation of {@link com.griddynamics.jagger.jaas.storage.CrudDao} interface for
 * {@link JobExecutionEntity}.
 */
@SuppressWarnings("unchecked")
@Repository
public class JobExecutionDaoImpl extends AbstractCrudDao<JobExecutionEntity, Long> implements JobExecutionDao {
    @Override
    @Transactional
    public JobExecutionEntity read(Long jobExecutionId) {
        return (JobExecutionEntity) getCurrentSession().get(JobExecutionEntity.class, jobExecutionId);
    }

    @Override
    @Transactional
    public List<JobExecutionEntity> readAll() {
        return getCurrentSession().createCriteria(JobExecutionEntity.class).setResultTransformer(DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    @Transactional
    public List<JobExecutionEntity> readAllPending() {
        Query query = getCurrentSession().createQuery("select t from JobExecutionEntity t where t.status = :status");
        query.setString("status", PENDING.name());
        return query.list();
    }

    @Override
    @Transactional
    public void create(JobExecutionEntity jobExecution) {
        getCurrentSession().save(jobExecution);
    }

    @Override
    @Transactional
    public void create(Iterable<JobExecutionEntity> entities) {
        Objects.requireNonNull(entities);
        entities.forEach(jobExecution -> getCurrentSession().save(jobExecution));
    }

    @Override
    @Transactional
    public void update(JobExecutionEntity jobExecution) {
        getCurrentSession().update(jobExecution);
    }

    @Override
    @Transactional
    public void createOrUpdate(JobExecutionEntity jobExecution) {
        getCurrentSession().saveOrUpdate(jobExecution);
    }

    @Override
    @Transactional
    public void delete(Long jobExecutionId) {
        JobExecutionEntity jobExecutionEntity = read(jobExecutionId);
        delete(jobExecutionEntity);
    }

    @Override
    @Transactional
    public void delete(JobExecutionEntity jobExecution) {
        getCurrentSession().delete(jobExecution);
    }
}
