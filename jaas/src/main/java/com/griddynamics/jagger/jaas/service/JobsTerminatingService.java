package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.JobExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.TIMEOUT;

@Service
public class JobsTerminatingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobsTerminatingService.class);

    private JobExecutionDao jobExecutionDao;

    @Autowired
    public JobsTerminatingService(JobExecutionDao jobExecutionDao) {
        this.jobExecutionDao = jobExecutionDao;
    }

    @Scheduled(fixedRateString = "${job.execution.termination.periodicity}")
    public void terminateOutdatedJobsTask() {
        long terminated = jobExecutionDao.readAllPending().stream().filter(this::isOutdated).peek(this::terminate).count();
        if (terminated > 0) {
            LOGGER.info("{} jobs has been terminated.", terminated);
        } else {
            LOGGER.debug("{} jobs has been terminated.", terminated);
        }
    }

    private boolean isOutdated(JobExecutionEntity jobExec) {
        long jobStartTimeoutInSeconds = jobExec.getJob().getJobStartTimeoutInSeconds();
        long jobCreated = jobExec.getAuditEntities().get(0).getTimestamp();

        long expirationTimestamp = jobCreated + jobStartTimeoutInSeconds * 1000;
        return expirationTimestamp <= System.currentTimeMillis();
    }

    private void terminate(JobExecutionEntity jobExec) {
        JobExecutionStatus oldStatus = jobExec.getLastAuditEntity().getNewStatus();
        jobExec.addAuditEntity(new JobExecutionAuditEntity(jobExec, System.currentTimeMillis(), oldStatus, TIMEOUT));
        jobExec.setStatus(TIMEOUT);
        jobExecutionDao.update(jobExec);
    }
}
