package com.griddynamics.jagger.jaas.service.impl;

import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import com.griddynamics.jagger.jaas.service.TestExecutionService;
import com.griddynamics.jagger.jaas.storage.TestExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.FINISHED;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.PENDING;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.RUNNING;

@Service
public class TestExecutionServiceImpl implements TestExecutionService {

    @Value("${test.execution.default.start.timeout.seconds}")
    private Long testExecutionDefaultStartTimeoutInSeconds;

    private TestExecutionDao testExecutionDao;

    @Autowired
    public TestExecutionServiceImpl(TestExecutionDao testExecutionDao) {
        this.testExecutionDao = testExecutionDao;
    }

    @Override
    public TestExecutionEntity read(Long id) {
        return testExecutionDao.read(id);
    }

    @Override
    public List<TestExecutionEntity> readAll() {
        return newArrayList(testExecutionDao.readAll());
    }

    @Override
    public List<TestExecutionEntity> readAllPending() {
        return newArrayList(testExecutionDao.readAllPending());
    }

    @Override
    public TestExecutionEntity create(TestExecutionEntity testExecution) {
        if (testExecution.getExecutionStartTimeoutInSeconds() == null)
            testExecution.setExecutionStartTimeoutInSeconds(testExecutionDefaultStartTimeoutInSeconds);

        testExecution.setAuditEntities(newHashSet(new TestExecutionAuditEntity(testExecution, System.currentTimeMillis(), null, PENDING)));
        testExecution.setStatus(PENDING);
        testExecutionDao.create(testExecution);
        return testExecution;
    }

    @Override
    public void delete(Long testExecutionId) {
        testExecutionDao.delete(testExecutionId);
    }

    @Override
    public void startExecution(String environmentId, String loadScenarioId) {
        testExecutionDao.readByEnvAndLoadScenario(environmentId, loadScenarioId).stream()
                .filter(exec -> exec.getStatus() == PENDING)
                .findFirst()
                .ifPresent(testExecutionEntity -> updateStatus(testExecutionEntity, RUNNING));
    }

    @Override
    public void finishExecution(String environmentId, String loadScenarioId) {
        testExecutionDao.readByEnvAndLoadScenario(environmentId, loadScenarioId).stream()
                .filter(exec -> exec.getStatus() == RUNNING)
                .findFirst()
                .ifPresent(testExecutionEntity -> updateStatus(testExecutionEntity, FINISHED));
    }

    private void updateStatus(TestExecutionEntity testExec, TestExecutionStatus newStatus) {
        testExec.addAuditEntity(new TestExecutionAuditEntity(testExec, System.currentTimeMillis(), testExec.getStatus(), newStatus));
        testExec.setStatus(newStatus);
        testExecutionDao.update(testExec);
    }
}
