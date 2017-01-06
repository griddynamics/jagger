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
        long deleted = testExecutionDao.readAllPending().stream()
                .filter(this::isOutdated)
                .peek(this::terminate)
                .peek(testExec -> LOGGER.info("Test execution {} has been terminated.", testExec.getId()))
                .count();
        if (deleted > 0)
            LOGGER.info("{} test executions has been terminated.", deleted);
    }

    private boolean isOutdated(TestExecutionEntity testExec) {
        long executionTimeToStartInSeconds = testExec.getExecutionTimeToStartInSeconds();
        long testExecCreated = testExec.getAuditEntities().stream().findFirst().get().getTimestamp();

        long expirationTimestamp = testExecCreated + executionTimeToStartInSeconds * 1000;
        return expirationTimestamp <= System.currentTimeMillis();
    }

    private void terminate(TestExecutionEntity testExec) {
        TestExecutionStatus oldStatus = testExec.getStatus();
        testExec.addAuditEntity(new TestExecutionAuditEntity(testExec, System.currentTimeMillis(), oldStatus, TIMEOUT));
        testExec.setStatus(TIMEOUT);
        testExecutionDao.update(testExec);
    }
}
