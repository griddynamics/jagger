package com.griddynamics.jagger.jaas.service.impl;

import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import com.griddynamics.jagger.jaas.service.JobExecutionService;
import com.griddynamics.jagger.jaas.storage.JobDao;
import com.griddynamics.jagger.jaas.storage.JobExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.FINISHED;
import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.PENDING;
import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.RUNNING;

@Service
public class JobExecutionServiceImpl implements JobExecutionService {

    private JobExecutionDao jobExecutionDao;

    private JobDao jobDao;

    @Autowired
    public JobExecutionServiceImpl(JobExecutionDao jobExecutionDao, JobDao jobDao) {
        this.jobExecutionDao = jobExecutionDao;
        this.jobDao = jobDao;
    }

    @Override
    public List<JobExecutionEntity> readAll() {
        return newArrayList(jobExecutionDao.readAll());
    }

    @Override
    public List<JobExecutionEntity> readAllPending() {
        return newArrayList(jobExecutionDao.readAllPending());
    }

    @Override
    public JobExecutionEntity create(JobExecutionEntity jobExecution) {
        Objects.requireNonNull(jobExecution.getJobId(), "Job id must not be null!");

        if (!jobDao.exists(jobExecution.getJobId()))
            throw new ResourceNotFoundException("Job", jobExecution.getJobId().toString());

        jobExecution.setAuditEntities(newHashSet(new JobExecutionAuditEntity(jobExecution, System.currentTimeMillis(), null, PENDING)));
        jobExecution.setStatus(PENDING);
        jobExecutionDao.create(jobExecution);
        return jobExecution;
    }

    @Override
    public void delete(Long jobExecutionId) {
        jobExecutionDao.delete(jobExecutionId);
    }

    @Override
    public void startExecution(String environmentId, String testSuiteId) {
        JobExecutionEntity jobExecutionEntity = jobDao.readByEnvAndTestSuite(environmentId, testSuiteId).stream()
                .flatMap(job -> job.getJobExecutionEntities().stream())
                .filter(exec -> exec.getStatus() == PENDING)
                .findFirst().get();
        updateStatus(jobExecutionEntity, RUNNING);
    }

    @Override
    public void finishExecution(String environmentId, String testSuiteId) {
        JobExecutionEntity jobExecutionEntity = jobDao.readByEnvAndTestSuite(environmentId, testSuiteId).stream()
                .flatMap(job -> job.getJobExecutionEntities().stream())
                .filter(exec -> exec.getStatus() == RUNNING)
                .findFirst().get();
        updateStatus(jobExecutionEntity, FINISHED);
    }

    private void updateStatus(JobExecutionEntity jobExec, JobExecutionEntity.JobExecutionStatus newStatus) {
        jobExec.addAuditEntity(new JobExecutionAuditEntity(jobExec, System.currentTimeMillis(), jobExec.getStatus(), newStatus));
        jobExec.setStatus(newStatus);
        jobExecutionDao.update(jobExec);
    }
}
