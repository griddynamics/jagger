package com.griddynamics.jagger.jaas.storage.impl;


import com.griddynamics.jagger.jaas.config.TestPersistenceConfig;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.LoadScenarioEntity;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus.PENDING;
import static com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus.RUNNING;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPersistenceConfig.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TestEnvironmentDaoTest {
    private static final String ENVIRONMENT_ID_1 = "env1";
    private static final String ENVIRONMENT_ID_2 = "env2";
    private static final String LOAD_SCENARIO_ID_1 = "test1";
    private static final String LOAD_SCENARIO_ID_2 = "test2";
    private static final String LOAD_SCENARIO_ID_3 = "test3";
    private static final String SESSION_1 = "session1";
    private static final String SESSION_2 = "session2";

    @Autowired
    private TestEnvironmentDao testEnvironmentDao;

    @Test
    public void readTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
    }

    @Test
    public void readAllTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);

        List<TestEnvironmentEntity> actuals = (List<TestEnvironmentEntity>) testEnvironmentDao.readAll();

        assertThat(testEnvironmentDao.readAll().size(), is(expected.size()));
        for (int i = 0; i < actuals.size(); i++) {
            assertThat(actuals.get(i), is(notNullValue()));
            assertThat(actuals.get(i), is(expected.get(i)));
        }
    }

    @Test
    public void updateTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        expected.setStatus(PENDING);
        expected.setRunningLoadScenario(null);
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void updateRemoveLoadScenariosTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        expected.setRunningLoadScenario(null);
        expected.getLoadScenarios().clear();
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void updateSetLoadScenariosTest() {
        TestEnvironmentEntity expected = new TestEnvironmentEntity();
        expected.setEnvironmentId(ENVIRONMENT_ID_1);
        testEnvironmentDao.create(expected);

        LoadScenarioEntity runningLoadScenario = new LoadScenarioEntity();
        runningLoadScenario.setLoadScenarioId(LOAD_SCENARIO_ID_1);
        runningLoadScenario.setTestEnvironmentEntity(expected);
        expected.setRunningLoadScenario(runningLoadScenario);
        expected.setLoadScenarios(newArrayList(runningLoadScenario));
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getLoadScenarios().size(), is(expected.getLoadScenarios().size()));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void updateLoadScenariosTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        expected.setRunningLoadScenario(null);
        LoadScenarioEntity loadScenarioEntity1 = new LoadScenarioEntity();
        loadScenarioEntity1.setLoadScenarioId(LOAD_SCENARIO_ID_3);
        loadScenarioEntity1.setTestEnvironmentEntity(expected);
        expected.getLoadScenarios().add(loadScenarioEntity1);
        testEnvironmentDao.create(expected);

        LoadScenarioEntity loadScenarioEntity = new LoadScenarioEntity();
        loadScenarioEntity.setLoadScenarioId(LOAD_SCENARIO_ID_2);
        loadScenarioEntity.setTestEnvironmentEntity(expected);

        expected.getLoadScenarios().clear();
        expected.getLoadScenarios().add(loadScenarioEntity);
        expected.getLoadScenarios().add(loadScenarioEntity1);
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void createWithSameLoadScenariosTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntities().get(0);
        testEnvironmentDao.create(expected);

        TestEnvironmentEntity expected2 = getTestEnvironmentEntities().get(1);
        expected2.setRunningLoadScenario(null);
        expected2.getLoadScenarios().clear();

        LoadScenarioEntity loadScenarioEntity = new LoadScenarioEntity();
        loadScenarioEntity.setLoadScenarioId(LOAD_SCENARIO_ID_1);
        loadScenarioEntity.setTestEnvironmentEntity(expected2);
        expected2.getLoadScenarios().add(loadScenarioEntity);

        testEnvironmentDao.create(expected2);

        TestEnvironmentEntity actual1 = testEnvironmentDao.read(ENVIRONMENT_ID_1);
        TestEnvironmentEntity actual2 = testEnvironmentDao.read(ENVIRONMENT_ID_2);

        assertThat(actual1, is(notNullValue()));
        assertThat(actual1, is(expected));
        assertThat(actual2, is(notNullValue()));
        assertThat(actual2, is(expected2));
        assertThat(testEnvironmentDao.readAll().size(), is(2));
        assertThat(actual1.getLoadScenarios().size(), is(1));
        assertThat(actual2.getLoadScenarios().size(), is(1));
        assertThat(actual1.getLoadScenarios().get(0), is(expected.getLoadScenarios().get(0)));
        assertThat(actual2.getLoadScenarios().get(0), is(expected2.getLoadScenarios().get(0)));
    }

    @Test
    public void createOrUpdate_Create_Test() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.createOrUpdate(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Update_Test() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        expected.setStatus(PENDING);
        expected.setRunningLoadScenario(null);
        testEnvironmentDao.createOrUpdate(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void deleteByIdTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);
        testEnvironmentDao.delete(ENVIRONMENT_ID_1);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(nullValue()));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void deleteTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);
        testEnvironmentDao.delete(expected.get(0));

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(nullValue()));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void deleteManyTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);

        testEnvironmentDao.delete(expected);

        Collection<TestEnvironmentEntity> actual = testEnvironmentDao.readAll();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(0));
    }

    @Test
    public void countTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);

        int actual = (int) testEnvironmentDao.count();

        assertThat(actual, is(expected.size()));
    }

    @Test
    public void existsTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        boolean actual = testEnvironmentDao.exists(ENVIRONMENT_ID_1);
        boolean actual2 = testEnvironmentDao.exists(ENVIRONMENT_ID_2);

        assertThat(actual, is(true));
        assertThat(actual2, is(false));
    }

    @Test
    public void existsWithSessionIdTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        boolean actual = testEnvironmentDao.existsWithSessionId(ENVIRONMENT_ID_1, SESSION_1);
        boolean actual2 = testEnvironmentDao.existsWithSessionId(ENVIRONMENT_ID_1, SESSION_2);

        assertThat(actual, is(true));
        assertThat(actual2, is(false));
    }

    @Test
    public void readExpiredTest() throws InterruptedException {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);

        TimeUnit.SECONDS.sleep(1);
        List<TestEnvironmentEntity> actual = testEnvironmentDao.readExpired(now().withZoneSameInstant(UTC).toInstant().toEpochMilli());

        TimeUnit.SECONDS.sleep(4);
        List<TestEnvironmentEntity> actual2 = testEnvironmentDao.readExpired(now().withZoneSameInstant(UTC).toInstant().toEpochMilli());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(expected.get(1)));

        assertThat(actual2, is(notNullValue()));
        assertThat(actual2.size(), is(2));
    }

    @Test
    public void deleteExpiredTest() throws InterruptedException {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);

        TimeUnit.SECONDS.sleep(1);
        testEnvironmentDao.deleteExpired(now().withZoneSameInstant(UTC).toInstant().toEpochMilli());
        List<TestEnvironmentEntity> actual = (List<TestEnvironmentEntity>) testEnvironmentDao.readAll();

        TimeUnit.SECONDS.sleep(4);
        testEnvironmentDao.deleteExpired(now().withZoneSameInstant(UTC).toInstant().toEpochMilli());
        List<TestEnvironmentEntity> actual2 = (List<TestEnvironmentEntity>) testEnvironmentDao.readAll();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(expected.get(0)));

        assertThat(actual2, is(notNullValue()));
        assertThat(actual2.size(), is(0));
    }


    private TestEnvironmentEntity getTestEnvironmentEntity() {
        TestEnvironmentEntity testEnvironmentEntity = new TestEnvironmentEntity();
        testEnvironmentEntity.setEnvironmentId(ENVIRONMENT_ID_1);
        testEnvironmentEntity.setStatus(RUNNING);
        LoadScenarioEntity loadScenarioEntity = new LoadScenarioEntity();
        loadScenarioEntity.setLoadScenarioId(LOAD_SCENARIO_ID_1);
        loadScenarioEntity.setTestEnvironmentEntity(testEnvironmentEntity);
        testEnvironmentEntity.setLoadScenarios(newArrayList(loadScenarioEntity));
        testEnvironmentEntity.setRunningLoadScenario(loadScenarioEntity);
        testEnvironmentEntity.setExpirationTimestamp(now().plusSeconds(5).withZoneSameInstant(UTC).toInstant().toEpochMilli());
        testEnvironmentEntity.setSessionId(SESSION_1);
        return testEnvironmentEntity;
    }

    private List<TestEnvironmentEntity> getTestEnvironmentEntities() {
        TestEnvironmentEntity testEnvironmentEntity1 = new TestEnvironmentEntity();
        testEnvironmentEntity1.setEnvironmentId(ENVIRONMENT_ID_1);
        testEnvironmentEntity1.setStatus(RUNNING);
        LoadScenarioEntity loadScenarioEntity = new LoadScenarioEntity();
        loadScenarioEntity.setLoadScenarioId(LOAD_SCENARIO_ID_1);
        loadScenarioEntity.setTestEnvironmentEntity(testEnvironmentEntity1);
        testEnvironmentEntity1.setLoadScenarios(newArrayList(loadScenarioEntity));
        testEnvironmentEntity1.setRunningLoadScenario(loadScenarioEntity);
        testEnvironmentEntity1.setExpirationTimestamp(now().plusSeconds(5).withZoneSameInstant(UTC).toInstant().toEpochMilli());
        testEnvironmentEntity1.setSessionId(SESSION_1);

        TestEnvironmentEntity testEnvironmentEntity2 = new TestEnvironmentEntity();
        testEnvironmentEntity2.setEnvironmentId(ENVIRONMENT_ID_2);
        testEnvironmentEntity2.setStatus(PENDING);
        LoadScenarioEntity loadScenarioEntity2 = new LoadScenarioEntity();
        loadScenarioEntity2.setLoadScenarioId(LOAD_SCENARIO_ID_2);
        loadScenarioEntity2.setTestEnvironmentEntity(testEnvironmentEntity2);
        LoadScenarioEntity loadScenarioEntity3 = new LoadScenarioEntity();
        loadScenarioEntity3.setLoadScenarioId(LOAD_SCENARIO_ID_3);
        loadScenarioEntity3.setTestEnvironmentEntity(testEnvironmentEntity2);
        testEnvironmentEntity2.setLoadScenarios(newArrayList(loadScenarioEntity2, loadScenarioEntity3));
        testEnvironmentEntity2.setExpirationTimestamp(now().withZoneSameInstant(UTC).toInstant().toEpochMilli());
        testEnvironmentEntity2.setSessionId(SESSION_2);

        return newArrayList(testEnvironmentEntity1, testEnvironmentEntity2);
    }
}
