package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.config.TestPersistenceConfig;
import com.griddynamics.jagger.jaas.storage.TestExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.FINISHED;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.PENDING;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.RUNNING;
import static com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity.TestExecutionStatus.TIMEOUT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPersistenceConfig.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TestExecutionDaoTest {

    private static final String ENVIRONMENT_ID_1 = "env1";
    private static final String ENVIRONMENT_ID_2 = "env2";
    private static final String TEST_SUITE_ID_1 = "test1";
    private static final String TEST_SUITE_ID_2 = "test2";

    @Autowired
    private TestExecutionDao testExecutionDao;

    @Test
    public void idGeneratorTest() {
        List<TestExecutionEntity> expected = getTestExecutionEntities();
        testExecutionDao.create(expected);

        TestExecutionEntity actual1 = testExecutionDao.read(1L);
        TestExecutionEntity actual2 = testExecutionDao.read(2L);

        assertThat(testExecutionDao.readAll().size(), is(expected.size()));
        assertThat(actual1, is(notNullValue()));
        assertThat(actual1.getId(), is(1L));
        assertThat(actual1, is(expected.get(0)));

        assertThat(actual2, is(notNullValue()));
        assertThat(actual2.getId(), is(2L));
        assertThat(actual2, is(expected.get(1)));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createWithInvalidAuditTest() {
        TestExecutionEntity expected = new TestExecutionEntity();
        expected.setStatus(PENDING);
        expected.addAuditEntity(new TestExecutionAuditEntity(null, System.currentTimeMillis(), PENDING, RUNNING));
        testExecutionDao.create(expected);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createWithInvalidAuditTest2() {
        TestExecutionEntity expected = new TestExecutionEntity();
        expected.setStatus(PENDING);
        expected.addAuditEntity(new TestExecutionAuditEntity(getTestExecutionEntity(), System.currentTimeMillis(), PENDING, RUNNING));
        testExecutionDao.create(expected);
    }
    
    @Test
    public void readTest() {
        TestExecutionEntity expected = getTestExecutionEntity();
        testExecutionDao.create(expected);

        TestExecutionEntity actual = testExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
    }

    @Test
    public void readAllTest() {
        List<TestExecutionEntity> expected = getTestExecutionEntities();
        testExecutionDao.create(expected);

        List<TestExecutionEntity> actuals = (List<TestExecutionEntity>) testExecutionDao.readAll();

        assertThat(testExecutionDao.readAll().size(), is(expected.size()));
        for (int i = 0; i < actuals.size(); i++) {
            assertThat(actuals.get(i), is(notNullValue()));
            assertThat(actuals.get(i), is(expected.get(i)));
        }
    }

    @Test
    public void readAllPendingTest() {
        List<TestExecutionEntity> expected = getTestExecutionEntities();
        testExecutionDao.create(expected);

        List<TestExecutionEntity> actual = testExecutionDao.readAllPending();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(2));
        assertThat(testExecutionDao.readAll().size(), is(2));
    }

    @Test
    public void readByEnvAndLoadScenarioTest() {
        TestExecutionEntity expected = getTestExecutionEntity();
        testExecutionDao.create(expected);

        List<TestExecutionEntity> actual = testExecutionDao.readByEnvAndLoadScenario(ENVIRONMENT_ID_1, TEST_SUITE_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.get(0), is(expected));
        assertThat(actual.size(), is(1));
    }

    @Test
    public void updateTest() {
        TestExecutionEntity expected = getTestExecutionEntity();
        testExecutionDao.create(expected);

        expected.setExecutionStartTimeoutInSeconds(1000L);
        testExecutionDao.update(expected);

        TestExecutionEntity actual = testExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void updateAddAuditTest() {
        TestExecutionEntity expected = getTestExecutionEntity();
        testExecutionDao.create(expected);

        expected.addAuditEntity(new TestExecutionAuditEntity(expected, System.currentTimeMillis(), PENDING, FINISHED));
        expected.addAuditEntity(new TestExecutionAuditEntity(expected, System.currentTimeMillis(), FINISHED, TIMEOUT));
        testExecutionDao.update(expected);

        TestExecutionEntity actual = testExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getAuditEntities(), containsInAnyOrder(expected.getAuditEntities().toArray()));
        assertThat(testExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void updateDeleteAuditTest() {
        TestExecutionEntity expected = getTestExecutionEntity();
        testExecutionDao.create(expected);

        expected.getAuditEntities().clear();
        testExecutionDao.update(expected);

        TestExecutionEntity actual = testExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getAuditEntities().size(), is(0));
        assertThat(testExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Create_Test() {
        TestExecutionEntity expected = getTestExecutionEntity();
        testExecutionDao.createOrUpdate(expected);

        TestExecutionEntity actual = testExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Update_Test() {
        TestExecutionEntity expected = getTestExecutionEntity();
        testExecutionDao.create(expected);

        expected.setExecutionStartTimeoutInSeconds(1000L);
        testExecutionDao.createOrUpdate(expected);

        TestExecutionEntity actual = testExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void deleteByIdTest() {
        List<TestExecutionEntity> expected = getTestExecutionEntities();
        testExecutionDao.create(expected);
        testExecutionDao.delete(1L);

        TestExecutionEntity actual = testExecutionDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(testExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void deleteTest() {
        List<TestExecutionEntity> expected = getTestExecutionEntities();
        testExecutionDao.create(expected);
        testExecutionDao.delete(expected.get(0));

        TestExecutionEntity actual = testExecutionDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(testExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void existsTest() {
        TestExecutionEntity expected = getTestExecutionEntity();
        testExecutionDao.create(expected);

        boolean actual = testExecutionDao.exists(1L);
        boolean actual2 = testExecutionDao.exists(2L);

        assertThat(actual, is(true));
        assertThat(actual2, is(false));
    }


    private TestExecutionEntity getTestExecutionEntity() {
        TestExecutionEntity testExecutionEntity = new TestExecutionEntity();

        testExecutionEntity.setEnvId(ENVIRONMENT_ID_1);
        testExecutionEntity.setLoadScenarioId(TEST_SUITE_ID_1);
        testExecutionEntity.setExecutionStartTimeoutInSeconds(0L);
        testExecutionEntity.setStatus(PENDING);
        testExecutionEntity.addAuditEntity(new TestExecutionAuditEntity(testExecutionEntity, System.currentTimeMillis(), PENDING, RUNNING));
        return testExecutionEntity;
    }

    private List<TestExecutionEntity> getTestExecutionEntities() {
        TestExecutionEntity testExecutionEntity1 = new TestExecutionEntity();
        testExecutionEntity1.setEnvId(ENVIRONMENT_ID_1);
        testExecutionEntity1.setLoadScenarioId(TEST_SUITE_ID_1);
        testExecutionEntity1.setExecutionStartTimeoutInSeconds(0L);
        testExecutionEntity1.setStatus(PENDING);
        testExecutionEntity1.addAuditEntity(new TestExecutionAuditEntity(testExecutionEntity1, System.currentTimeMillis(), PENDING, RUNNING));

        TestExecutionEntity testExecutionEntity2 = new TestExecutionEntity();
        testExecutionEntity2.setEnvId(ENVIRONMENT_ID_2);
        testExecutionEntity2.setLoadScenarioId(TEST_SUITE_ID_2);
        testExecutionEntity2.setExecutionStartTimeoutInSeconds(50L);
        testExecutionEntity2.setStatus(PENDING);
        testExecutionEntity2.addAuditEntity(new TestExecutionAuditEntity(testExecutionEntity2, System.currentTimeMillis(), PENDING, RUNNING));

        return newArrayList(testExecutionEntity1, testExecutionEntity2);
    }
}