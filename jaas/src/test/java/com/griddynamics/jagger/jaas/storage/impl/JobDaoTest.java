package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.config.TestPersistenceConfig;
import com.griddynamics.jagger.jaas.storage.JobDao;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.JobEntity;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.jaas.storage.impl.TestEnvironmentDaoTest.getTestEnvironmentEntities;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPersistenceConfig.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class JobDaoTest {

    private static final String ENVIRONMENT_ID_1 = "env1";
    private static final String ENVIRONMENT_ID_2 = "env2";
    private static final String TEST_SUITE_ID_1 = "test1";
    private static final String TEST_SUITE_ID_2 = "test2";

    @Autowired
    private JobDao jobDao;

    @Autowired
    private TestEnvironmentDao testEnvironmentDao;

    @Before
    public void setUp() {
        List<TestEnvironmentEntity> testEnvironmentEntities = getTestEnvironmentEntities();
        testEnvironmentDao.create(testEnvironmentEntities);
    }

    @Test
    public void idGeneratorTest() {
        List<JobEntity> expected = getJobEntities();
        jobDao.create(expected);

        JobEntity actual1 = jobDao.read(1L);
        JobEntity actual2 = jobDao.read(2L);

        assertThat(jobDao.readAll().size(), is(expected.size()));
        assertThat(actual1, is(notNullValue()));
        assertThat(actual1.getId(), is(1L));
        assertThat(actual1, is(expected.get(0)));

        assertThat(actual2, is(notNullValue()));
        assertThat(actual2.getId(), is(2L));
        assertThat(actual2, is(expected.get(1)));
    }

    @Test
    public void readTest() {
        JobEntity expected = getJobEntity();
        jobDao.create(expected);

        JobEntity actual = jobDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
    }

    @Test
    public void readAllTest() {
        List<JobEntity> expected = getJobEntities();
        jobDao.create(expected);

        List<JobEntity> actuals = (List<JobEntity>) jobDao.readAll();

        assertThat(jobDao.readAll().size(), is(expected.size()));
        for (int i = 0; i < actuals.size(); i++) {
            assertThat(actuals.get(i), is(notNullValue()));
            assertThat(actuals.get(i), is(expected.get(i)));
        }
    }

    @Test
    public void updateTest() {
        JobEntity expected = getJobEntity();
        jobDao.create(expected);

        expected.setJobStartTimeoutInSeconds(1000L);
        jobDao.update(expected);

        JobEntity actual = jobDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(jobDao.readAll().size(), is(1));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void updateSetNotExistingEnvTest() throws Exception {
        JobEntity expected = getJobEntity();
        jobDao.create(expected);

        expected.setTestEnvironmentId("not existing env");
        jobDao.update(expected);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void updateSetNotExistingSuiteTest() throws Exception {
        JobEntity expected = getJobEntity();
        jobDao.create(expected);

        expected.setTestSuiteId("not existing test");
        jobDao.update(expected);
    }

    @Test
    public void createOrUpdate_Create_Test() {
        JobEntity expected = getJobEntity();
        jobDao.createOrUpdate(expected);

        JobEntity actual = jobDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(jobDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Update_Test() {
        JobEntity expected = getJobEntity();
        jobDao.create(expected);

        expected.setJobStartTimeoutInSeconds(1000L);
        jobDao.createOrUpdate(expected);

        JobEntity actual = jobDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(jobDao.readAll().size(), is(1));
    }

    @Test
    public void deleteByIdTest() {
        List<JobEntity> expected = getJobEntities();
        jobDao.create(expected);
        jobDao.delete(1L);

        JobEntity actual = jobDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(jobDao.readAll().size(), is(1));
    }

    @Test
    public void deleteTest() {
        List<JobEntity> expected = getJobEntities();
        jobDao.create(expected);
        jobDao.delete(expected.get(0));

        JobEntity actual = jobDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(jobDao.readAll().size(), is(1));
    }

    private JobEntity getJobEntity() {
        JobEntity jobEntity = new JobEntity();

        jobEntity.setTestEnvironmentId(ENVIRONMENT_ID_1);
        jobEntity.setTestSuiteId(TEST_SUITE_ID_1);
        jobEntity.setJobStartTimeoutInSeconds(0L);

        return jobEntity;
    }

    private List<JobEntity> getJobEntities() {
        JobEntity jobEntity1 = new JobEntity();
        jobEntity1.setTestEnvironmentId(ENVIRONMENT_ID_1);
        jobEntity1.setTestSuiteId(TEST_SUITE_ID_1);
        jobEntity1.setJobStartTimeoutInSeconds(0L);

        JobEntity jobEntity2 = new JobEntity();
        jobEntity1.setTestEnvironmentId(ENVIRONMENT_ID_2);
        jobEntity1.setTestSuiteId(TEST_SUITE_ID_2);
        jobEntity1.setJobStartTimeoutInSeconds(50L);

        return newArrayList(jobEntity1, jobEntity2);
    }
}