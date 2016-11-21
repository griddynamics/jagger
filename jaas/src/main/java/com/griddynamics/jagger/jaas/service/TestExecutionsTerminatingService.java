package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.TestExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.TIMEOUT;

@Service
public class TestExecutionsTerminatingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestExecutionsTerminatingService.class);

    private TestExecutionDao testExecutionDao;

    @Autowired
    public TestExecutionsTerminatingService(TestExecutionDao testExecutionDao) {
        this.testExecutionDao = testExecutionDao;
    }

    @Scheduled(fixedRateString = "${test.execution.termination.periodicity.milliseconds}")
    public void terminateOutdatedTestExecutionsTask() {
        testExecutionDao.readAllPending().stream()
                .filter(this::isOutdated)
                .peek(this::terminate)
                .peek(testExec -> LOGGER.info("{} has been terminated.", testExec));
    }

    private boolean isOutdated(TestExecutionEntity testExec) {
        long testExecStartTimeoutInSeconds = testExec.getExecutionStartTimeoutInSeconds();
        long testExecCreated = testExec.getAuditEntities().stream().findFirst().get().getTimestamp();

        long expirationTimestamp = testExecCreated + testExecStartTimeoutInSeconds * 1000;
        return expirationTimestamp <= System.currentTimeMillis();
    }

    private void terminate(TestExecutionEntity testExec) {
        TestExecutionStatus oldStatus = testExec.getStatus();
        testExec.addAuditEntity(new TestExecutionAuditEntity(testExec, System.currentTimeMillis(), oldStatus, TIMEOUT));
        testExec.setStatus(TIMEOUT);
        testExecutionDao.update(testExec);
    }
}
