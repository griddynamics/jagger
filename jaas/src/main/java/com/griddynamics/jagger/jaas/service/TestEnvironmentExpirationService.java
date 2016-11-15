package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TestEnvironmentExpirationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEnvironmentExpirationService.class);

    private TestEnvironmentDao testEnvironmentDao;

    @Autowired
    public TestEnvironmentExpirationService(TestEnvironmentDao testEnvironmentDao) {
        this.testEnvironmentDao = testEnvironmentDao;
    }

    @Scheduled(fixedRateString = "${environments.cleaning.job.periodicity.milliseconds}")
    public void deleteExpiredEnvironmentsTask() {
        int deleted = testEnvironmentDao.deleteExpired(System.currentTimeMillis());
        if (deleted > 0) {
            LOGGER.info("{} expired test environments has been deleted.", deleted);
        } else {
            LOGGER.debug("{} expired test environments has been deleted.", deleted);
        }
    }
}
