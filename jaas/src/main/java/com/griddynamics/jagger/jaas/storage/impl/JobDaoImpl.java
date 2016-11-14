package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.storage.AbstractCrudDao;
import com.griddynamics.jagger.jaas.storage.JobDao;
import com.griddynamics.jagger.jaas.storage.model.JobEntity;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.hibernate.criterion.CriteriaSpecification.DISTINCT_ROOT_ENTITY;

/**
 * Hibernate based transactional implementation of {@link com.griddynamics.jagger.jaas.storage.CrudDao} interface for {@link JobEntity}.
 */
@SuppressWarnings("unchecked")
@Repository
public class JobDaoImpl extends AbstractCrudDao<JobEntity, Long> implements JobDao {

    @Override
    @Transactional
    public JobEntity read(Long jobId) {
        return (JobEntity) getCurrentSession().get(JobEntity.class, jobId);
    }

    @Override
    @Transactional
    public List<JobEntity> readAll() {
        return getCurrentSession().createCriteria(JobEntity.class).setResultTransformer(DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    @Transactional
    public void create(JobEntity job) {
        getCurrentSession().save(job);
    }

    @Override
    @Transactional
    public void create(Iterable<JobEntity> entities) {
        Objects.requireNonNull(entities);
        entities.forEach(job -> getCurrentSession().save(job));
    }

    @Override
    @Transactional
    public void update(JobEntity job) {
        getCurrentSession().update(job);
    }

    @Override
    @Transactional
    public void createOrUpdate(JobEntity job) {
        getCurrentSession().saveOrUpdate(job);
    }

    @Override
    @Transactional
    public void delete(Long jobId) {
        JobEntity job = new JobEntity();
        job.setId(jobId);
        delete(job);
    }

    @Override
    @Transactional
    public void delete(JobEntity job) {
        getCurrentSession().delete(job);
    }

    @Override
    @Transactional
    public boolean exists(Long jobId) {
        Query query = getCurrentSession().createQuery("select 1 from JobEntity t where t.id = :id");
        query.setLong("id", jobId);
        return query.uniqueResult() != null;
    }
}
