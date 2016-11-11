package com.griddynamics.jagger.jaas.service.impl;

import com.griddynamics.jagger.jaas.service.JobExecutionService;
import com.griddynamics.jagger.jaas.storage.JobExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.PENDING;

@Service
public class JobExecutionServiceImpl implements JobExecutionService {

    private JobExecutionDao jobExecutionDao;

    @Autowired
    public JobExecutionServiceImpl(JobExecutionDao jobExecutionDao) {
        this.jobExecutionDao = jobExecutionDao;
    }

    @Override
    public JobExecutionEntity read(Long jobId) {
        return jobExecutionDao.read(jobId);
    }

    @Override
    public List<JobExecutionEntity> readAll() {
        return newArrayList(jobExecutionDao.readAll());
    }

    @Override
    public JobExecutionEntity create(JobExecutionEntity job) {
        job.setAuditEntities(newArrayList(new JobExecutionAuditEntity(job, System.currentTimeMillis(), null, PENDING)));
        jobExecutionDao.create(job);
        return job;
    }

    @Override
    public JobExecutionEntity update(JobExecutionEntity job) {
        jobExecutionDao.update(job);
        return job;
    }

    @Override
    public void delete(Long jobId) {
        jobExecutionDao.delete(jobId);
    }
}
