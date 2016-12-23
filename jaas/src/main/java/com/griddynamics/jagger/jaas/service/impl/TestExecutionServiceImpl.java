package com.griddynamics.jagger.jaas.service.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.PENDING;

import com.griddynamics.jagger.jaas.service.TestExecutionService;
import com.griddynamics.jagger.jaas.storage.TestExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import java.util.List;

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
        if (testExecution.getExecutionStartTimeoutInSeconds() == null || testExecution.getExecutionStartTimeoutInSeconds() == 0) {
            testExecution.setExecutionStartTimeoutInSeconds(testExecutionDefaultStartTimeoutInSeconds);
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
