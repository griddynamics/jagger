package com.griddynamics.jagger.jaas.service.impl;

import com.griddynamics.jagger.jaas.service.JobService;
import com.griddynamics.jagger.jaas.storage.JobDao;
import com.griddynamics.jagger.jaas.storage.model.JobEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class JobServiceImpl implements JobService {

    @Value("${job.default.start.timeout.seconds}")
    private Long jobDefaultStartTimeoutInSeconds;

    private JobDao jobDao;

    @Autowired
    public JobServiceImpl(JobDao jobDao) {
        this.jobDao = jobDao;
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
        if (job.getJobStartTimeoutInSeconds() == null)
            job.setJobStartTimeoutInSeconds(jobDefaultStartTimeoutInSeconds);
        jobDao.create(job);
        return job;
    }

    @Override
    public JobEntity update(JobEntity job) {
        if (job.getJobStartTimeoutInSeconds() == null)
            job.setJobStartTimeoutInSeconds(jobDefaultStartTimeoutInSeconds);
        jobDao.update(job);
        return job;
    }

    @Override
    public void delete(Long jobId) {
        jobDao.delete(jobId);
    }
}
