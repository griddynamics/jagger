package com.griddynamics.jagger.jaas.service.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus.PENDING;
import static com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus.RUNNING;
import static java.time.ZonedDateTime.now;
import static org.apache.commons.collections.CollectionUtils.isEqualCollection;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import com.griddynamics.jagger.jaas.exceptions.WrongTestEnvironmentStatusException;
import com.griddynamics.jagger.jaas.service.TestEnvironmentService;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.LoadScenarioEntity;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class TestEnvironmentServiceImpl implements TestEnvironmentService {

    @Value("${environments.ttl.minutes}")
    private int environmentsTtlMinutes;

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
        fillLoadScenarios(testEnvironment);
        testEnvironment.setExpirationTimestamp(getExpirationTimestamp());
        testEnvironment.setSessionId(UUID.randomUUID().toString());
        if (testEnvironment.getRunningLoadScenario() == null) {
            testEnvironmentDao.create(testEnvironment);
        } else {
            // Due to unique constraint in LoadScenario runningLoadScenario cannot be persisted until all Load Scenarios are persisted.
            // Hence, firstly test env is persisted without running Load Scenario and after running Load Scenario can be set
            LoadScenarioEntity runningLoadScenario = testEnvironment.getRunningLoadScenario();
            testEnvironment.setRunningLoadScenario(null);
            testEnvironment.setStatus(PENDING);
            testEnvironmentDao.create(testEnvironment);

            testEnvironment.setRunningLoadScenario(runningLoadScenario);
            testEnvironment.setStatus(RUNNING);
            update(testEnvironment);
        }
        return testEnvironment;
    }

    @Override
    public TestEnvironmentEntity update(TestEnvironmentEntity newTestEnv) {
        TestEnvironmentEntity testEnvToUpdate = read(newTestEnv.getEnvironmentId());
        fillLoadScenarios(newTestEnv, testEnvToUpdate);

        if (newTestEnv.getRunningLoadScenario() != null && newTestEnv.getStatus() == RUNNING
                || newTestEnv.getRunningLoadScenario() == null && newTestEnv.getStatus() == PENDING)
            testEnvToUpdate.setStatus(newTestEnv.getStatus());
        else
            throw new WrongTestEnvironmentStatusException(newTestEnv.getStatus(), newTestEnv.getRunningLoadScenario());

        if (testEnvToUpdate.getLoadScenarios() == null)
            testEnvToUpdate.setLoadScenarios(newTestEnv.getLoadScenarios());
        else if (newTestEnv.getLoadScenarios() == null)
            testEnvToUpdate.getLoadScenarios().clear();
        else if (!isEqualCollection(testEnvToUpdate.getLoadScenarios(), newTestEnv.getLoadScenarios())) {
            HashSet<LoadScenarioEntity> newLoadScenarios = newHashSet(testEnvToUpdate.getLoadScenarios());
            // add all new LoadScenarios
            newLoadScenarios.addAll(newTestEnv.getLoadScenarios());

            // remove all LoadScenarios which must be deleted
            newHashSet(newLoadScenarios).stream().filter(s -> !newTestEnv.getLoadScenarios().contains(s)).forEach(newLoadScenarios::remove);

            testEnvToUpdate.getLoadScenarios().clear();
            testEnvToUpdate.getLoadScenarios().addAll(newLoadScenarios);
        }

        if (testEnvToUpdate.getRunningLoadScenario() != newTestEnv.getRunningLoadScenario()) {
            testEnvToUpdate.setRunningLoadScenario(newTestEnv.getRunningLoadScenario());
        }
        fillLoadScenarios(testEnvToUpdate);
        testEnvToUpdate.setExpirationTimestamp(getExpirationTimestamp());
        testEnvironmentDao.update(testEnvToUpdate);
        return testEnvToUpdate;
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

    private void fillLoadScenarios(TestEnvironmentEntity testEnv) {
        fillLoadScenarios(testEnv, testEnv);
    }

    private void fillLoadScenarios(TestEnvironmentEntity testEnvToBeFilled, TestEnvironmentEntity testEnvToSet) {
        if (isNotEmpty(testEnvToBeFilled.getLoadScenarios()))
            testEnvToBeFilled.getLoadScenarios().stream()
                    .filter(suite -> suite.getTestEnvironmentEntity() == null)
                    .forEach(suite -> suite.setTestEnvironmentEntity(testEnvToSet));
        if (testEnvToBeFilled.getRunningLoadScenario() != null && testEnvToBeFilled.getRunningLoadScenario().getTestEnvironmentEntity() == null)
            testEnvToBeFilled.getRunningLoadScenario().setTestEnvironmentEntity(testEnvToSet);
    }

    private long getExpirationTimestamp() {
        return now().plusMinutes(environmentsTtlMinutes).toInstant().toEpochMilli();
    }
}
