package com.griddynamics.jagger.jaas.storage.impl;


import com.griddynamics.jagger.jaas.config.TestPersistenceConfig;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import com.griddynamics.jagger.jaas.storage.model.TestSuiteEntity;
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
    private static final String TEST_SUITE_ID_1 = "test1";
    private static final String TEST_SUITE_ID_2 = "test2";
    private static final String TEST_SUITE_ID_3 = "test3";
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
        expected.setRunningTestSuite(null);
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void updateRemoveTestSuitesTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        expected.setRunningTestSuite(null);
        expected.getTestSuites().clear();
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void updateSetTestSuitesTest() {
        TestEnvironmentEntity expected = new TestEnvironmentEntity();
        expected.setEnvironmentId(ENVIRONMENT_ID_1);
        testEnvironmentDao.create(expected);

        TestSuiteEntity runningTestSuite = new TestSuiteEntity();
        runningTestSuite.setTestSuiteId(TEST_SUITE_ID_1);
        runningTestSuite.setTestEnvironmentEntity(expected);
        expected.setRunningTestSuite(runningTestSuite);
        expected.setTestSuites(newArrayList(runningTestSuite));
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getTestSuites().size(), is(expected.getTestSuites().size()));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void updateTestSuitesTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        expected.setRunningTestSuite(null);
        TestSuiteEntity testSuiteEntity1 = new TestSuiteEntity();
        testSuiteEntity1.setTestSuiteId(TEST_SUITE_ID_3);
        testSuiteEntity1.setTestEnvironmentEntity(expected);
        expected.getTestSuites().add(testSuiteEntity1);
        testEnvironmentDao.create(expected);

        TestSuiteEntity testSuiteEntity = new TestSuiteEntity();
        testSuiteEntity.setTestSuiteId(TEST_SUITE_ID_2);
        testSuiteEntity.setTestEnvironmentEntity(expected);

        expected.getTestSuites().clear();
        expected.getTestSuites().add(testSuiteEntity);
        expected.getTestSuites().add(testSuiteEntity1);
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void createWithSameTestSuitesTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntities().get(0);
        testEnvironmentDao.create(expected);

        TestEnvironmentEntity expected2 = getTestEnvironmentEntities().get(1);
        expected2.setRunningTestSuite(null);
        expected2.getTestSuites().clear();

        TestSuiteEntity testSuiteEntity = new TestSuiteEntity();
        testSuiteEntity.setTestSuiteId(TEST_SUITE_ID_1);
        testSuiteEntity.setTestEnvironmentEntity(expected2);
        expected2.getTestSuites().add(testSuiteEntity);

        testEnvironmentDao.create(expected2);

        TestEnvironmentEntity actual1 = testEnvironmentDao.read(ENVIRONMENT_ID_1);
        TestEnvironmentEntity actual2 = testEnvironmentDao.read(ENVIRONMENT_ID_2);

        assertThat(actual1, is(notNullValue()));
        assertThat(actual1, is(expected));
        assertThat(actual2, is(notNullValue()));
        assertThat(actual2, is(expected2));
        assertThat(testEnvironmentDao.readAll().size(), is(2));
        assertThat(actual1.getTestSuites().size(), is(1));
        assertThat(actual2.getTestSuites().size(), is(1));
        assertThat(actual1.getTestSuites().get(0), is(expected.getTestSuites().get(0)));
        assertThat(actual2.getTestSuites().get(0), is(expected2.getTestSuites().get(0)));
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
        expected.setRunningTestSuite(null);
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
        TestSuiteEntity testSuiteEntity = new TestSuiteEntity();
        testSuiteEntity.setTestSuiteId(TEST_SUITE_ID_1);
        testSuiteEntity.setTestEnvironmentEntity(testEnvironmentEntity);
        testEnvironmentEntity.setTestSuites(newArrayList(testSuiteEntity));
        testEnvironmentEntity.setRunningTestSuite(testSuiteEntity);
        testEnvironmentEntity.setExpirationTimestamp(now().plusSeconds(5).withZoneSameInstant(UTC).toInstant().toEpochMilli());
        testEnvironmentEntity.setSessionId(SESSION_1);
        return testEnvironmentEntity;
    }

    private List<TestEnvironmentEntity> getTestEnvironmentEntities() {
        TestEnvironmentEntity testEnvironmentEntity1 = new TestEnvironmentEntity();
        testEnvironmentEntity1.setEnvironmentId(ENVIRONMENT_ID_1);
        testEnvironmentEntity1.setStatus(RUNNING);
        TestSuiteEntity testSuiteEntity = new TestSuiteEntity();
        testSuiteEntity.setTestSuiteId(TEST_SUITE_ID_1);
        testSuiteEntity.setTestEnvironmentEntity(testEnvironmentEntity1);
        testEnvironmentEntity1.setTestSuites(newArrayList(testSuiteEntity));
        testEnvironmentEntity1.setRunningTestSuite(testSuiteEntity);
        testEnvironmentEntity1.setExpirationTimestamp(now().plusSeconds(5).withZoneSameInstant(UTC).toInstant().toEpochMilli());
        testEnvironmentEntity1.setSessionId(SESSION_1);

        TestEnvironmentEntity testEnvironmentEntity2 = new TestEnvironmentEntity();
        testEnvironmentEntity2.setEnvironmentId(ENVIRONMENT_ID_2);
        testEnvironmentEntity2.setStatus(PENDING);
        TestSuiteEntity testSuiteEntity2 = new TestSuiteEntity();
        testSuiteEntity2.setTestSuiteId(TEST_SUITE_ID_2);
        testSuiteEntity2.setTestEnvironmentEntity(testEnvironmentEntity2);
        TestSuiteEntity testSuiteEntity3 = new TestSuiteEntity();
        testSuiteEntity3.setTestSuiteId(TEST_SUITE_ID_3);
        testSuiteEntity3.setTestEnvironmentEntity(testEnvironmentEntity2);
        testEnvironmentEntity2.setTestSuites(newArrayList(testSuiteEntity2, testSuiteEntity3));
        testEnvironmentEntity2.setExpirationTimestamp(now().withZoneSameInstant(UTC).toInstant().toEpochMilli());
        testEnvironmentEntity2.setSessionId(SESSION_2);

        return newArrayList(testEnvironmentEntity1, testEnvironmentEntity2);
    }
}
