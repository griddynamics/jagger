package com.griddynamics.jagger.jaas.service.impl;

import com.griddynamics.jagger.jaas.exceptions.InvalidJobException;
import com.griddynamics.jagger.jaas.service.JobService;
import com.griddynamics.jagger.jaas.storage.JobDao;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.JobEntity;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
public class JobServiceImpl implements JobService {

    @Value("${job.default.start.timeout.seconds}")
    private Long jobDefaultStartTimeoutInSeconds;

    private JobDao jobDao;

    private TestEnvironmentDao testEnvironmentDao;

    @Autowired
    public JobServiceImpl(JobDao jobDao, TestEnvironmentDao testEnvironmentDao) {
        this.jobDao = jobDao;
        this.testEnvironmentDao = testEnvironmentDao;
    }

    @Override
    public JobEntity read(Long jobId) {
        return jobDao.read(jobId);
    }

    @Override
    public List<JobEntity> readAll() {
        return newArrayList(jobDao.readAll());
    }

    @Override
    public JobEntity create(JobEntity job) {
        validateJob(job);
        if (job.getJobStartTimeoutInSeconds() == null)
            job.setJobStartTimeoutInSeconds(jobDefaultStartTimeoutInSeconds);
        jobDao.create(job);
        return job;
    }

    @Override
    public JobEntity update(JobEntity job) {
        validateJob(job);
        if (job.getJobStartTimeoutInSeconds() == null)
            job.setJobStartTimeoutInSeconds(jobDefaultStartTimeoutInSeconds);
        jobDao.update(job);
        return job;
    }

    @Override
    public void delete(Long jobId) {
        jobDao.delete(jobId);
    }

    private void validateJob(final JobEntity job) {
        TestEnvironmentEntity testEnv = testEnvironmentDao.read(job.getTestEnvironmentId());
        if (testEnv == null)
            throw new InvalidJobException(format("Test Environment with id '%s' not exists.", job.getTestEnvironmentId()));

        if (isEmpty(testEnv.getTestSuites()))
            throw new InvalidJobException(format("Test Environment with id '%s' doesn't have any Test Suite.", job.getTestEnvironmentId()));

        testEnv.getTestSuites().stream()
                .filter(test -> test.getTestSuiteId().equals(job.getTestSuiteId()))
                .findFirst()
                .orElseThrow(() -> new InvalidJobException(format("Test Environment with id '%s' doesn't have Test Suite with id '%s'.",
                        job.getTestEnvironmentId(), job.getTestSuiteId())));
    }
}
