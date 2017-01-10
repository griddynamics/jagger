package com.griddynamics.jagger.jaas.service.impl;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.jaas.service.TestExecutionService;
import com.griddynamics.jagger.jaas.storage.TestExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.PENDING;

@Service
public class TestExecutionServiceImpl implements TestExecutionService {

    @Value("${test.execution.default.start.timeout.seconds}")
    private Long testExecutionDefaultTimeToStartInSeconds;

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
    public List<TestExecutionEntity> readByEnv(String envId) {
        return newArrayList(testExecutionDao.readByEnv(envId));
    }

    @Override
    public TestExecutionEntity create(TestExecutionEntity testExecution) {
        if (testExecution.getExecutionTimeToStartInSeconds() == null || testExecution.getExecutionTimeToStartInSeconds() == 0) {
            testExecution.setExecutionTimeToStartInSeconds(testExecutionDefaultTimeToStartInSeconds);
        }
    
        testExecution.setAuditEntities(Lists.newArrayList(new TestExecutionAuditEntity(testExecution,
                                                                          System.currentTimeMillis(),
                                                                          null,
                                                                          PENDING)));
        testExecution.setStatus(PENDING);
        testExecutionDao.create(testExecution);
        return testExecution;
    }

    @Override
    public void delete(Long testExecutionId) {
        testExecutionDao.delete(testExecutionId);
    }
    
    @Override
    public void update(TestExecutionEntity testExecution) {
        TestExecutionEntity dbTestExecutionEntity = testExecutionDao.read(testExecution.getId());
        if (dbTestExecutionEntity.getStatus() != testExecution.getStatus()) {
            testExecution.addAuditEntity(new TestExecutionAuditEntity(testExecution,
                                                                      System.currentTimeMillis(),
                                                                      dbTestExecutionEntity.getStatus(),
                                                                      testExecution.getStatus()));
        }
        testExecution.getAuditEntities().forEach(auditEntity -> auditEntity.setTestExecutionEntity(testExecution));
        testExecutionDao.update(testExecution);
    }
}
