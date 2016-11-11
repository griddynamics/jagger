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
    public JobExecutionEntity read(Long jobExecutionId) {
        return jobExecutionDao.read(jobExecutionId);
    }

    @Override
    public List<JobExecutionEntity> readAll() {
        return newArrayList(jobExecutionDao.readAll());
    }

    @Override
    public JobExecutionEntity create(JobExecutionEntity jobExecution) {
        jobExecution.setAuditEntities(newArrayList(new JobExecutionAuditEntity(jobExecution, System.currentTimeMillis(), null, PENDING)));
        jobExecution.setStatus(PENDING);
        jobExecutionDao.create(jobExecution);
        return jobExecution;
    }

    @Override
    public JobExecutionEntity update(JobExecutionEntity jobExecution) {
        jobExecutionDao.update(jobExecution);
        return jobExecution;
    }

    @Override
    public void delete(Long jobExecutionId) {
        jobExecutionDao.delete(jobExecutionId);
    }
}
