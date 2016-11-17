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

    @Scheduled(fixedRateString = "${job.execution.termination.periodicity.milliseconds}")
    public void terminateOutdatedJobsTask() {
        jobExecutionDao.readAllPending().stream()
                .filter(this::isOutdated)
                .peek(this::terminate)
                .peek(jobExec -> LOGGER.info("{} has been terminated.", jobExec.getJob()));
    }

    private boolean isOutdated(JobExecutionEntity jobExec) {
        long jobStartTimeoutInSeconds = jobExec.getJob().getJobStartTimeoutInSeconds();
        long jobCreated = jobExec.getAuditEntities().get(0).getTimestamp();

        long expirationTimestamp = jobCreated + jobStartTimeoutInSeconds * 1000;
        return expirationTimestamp <= System.currentTimeMillis();
    }

    private void terminate(JobExecutionEntity jobExec) {
        JobExecutionStatus oldStatus = jobExec.getStatus();
        jobExec.addAuditEntity(new JobExecutionAuditEntity(jobExec, System.currentTimeMillis(), oldStatus, TIMEOUT));
        jobExec.setStatus(TIMEOUT);
        jobExecutionDao.update(jobExec);
    }
}
