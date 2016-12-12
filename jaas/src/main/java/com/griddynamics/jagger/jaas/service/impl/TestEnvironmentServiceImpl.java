package com.griddynamics.jagger.jaas.service.impl;

import static com.google.common.collect.Lists.newArrayList;

import com.griddynamics.jagger.jaas.service.TestEnvironmentService;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestEnvironmentServiceImpl implements TestEnvironmentService {
    
    private TestEnvironmentDao testEnvironmentDao;
    
    @Autowired
    public TestEnvironmentServiceImpl(TestEnvironmentDao testEnvironmentDao) {
        this.testEnvironmentDao = testEnvironmentDao;
    }
    
    @Override
    public TestEnvironmentEntity read(String envId) {
        return testEnvironmentDao.read(envId);
    }
    
    @Override
    public List<TestEnvironmentEntity> readAll() {
        return newArrayList(testEnvironmentDao.readAll());
    }
    
    @Override
    public TestEnvironmentEntity create(TestEnvironmentEntity testEnvironment) {
        testEnvironmentDao.create(testEnvironment);
        return testEnvironment;
    }
    
    @Override
    public TestEnvironmentEntity update(TestEnvironmentEntity newTestEnv) {
        testEnvironmentDao.reCreate(newTestEnv);
        return newTestEnv;
    }
    
    @Override
    public void delete(String envId) {
        testEnvironmentDao.delete(envId);
    }
    
    @Override
    public boolean exists(String envId) {
        return testEnvironmentDao.exists(envId);
    }
    
    @Override
    public boolean existsWithSessionId(String envId, String sessionId) {
        return testEnvironmentDao.existsWithSessionId(envId, sessionId);
    }
}
