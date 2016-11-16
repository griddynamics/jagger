package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.config.TestPersistenceConfig;
import com.griddynamics.jagger.jaas.storage.JobDao;
import com.griddynamics.jagger.jaas.storage.JobExecutionDao;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionAuditEntity;
import com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity;
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
import static com.griddynamics.jagger.jaas.storage.impl.JobDaoTest.getJobEntities;
import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.FINISHED;
import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.PENDING;
import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.RUNNING;
import static com.griddynamics.jagger.jaas.storage.model.JobExecutionEntity.JobExecutionStatus.TIMEOUT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPersistenceConfig.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class JobExecutionDaoTest {

    @Autowired
    private JobDao jobDao;

    @Autowired
    private JobExecutionDao jobExecutionDao;

    @Before
    public void setUp() {
        jobDao.create(getJobEntities());
    }

    @Test
    public void idGeneratorTest() {
        List<JobExecutionEntity> expected = getJobExecutionEntities();
        jobExecutionDao.create(expected);

        JobExecutionEntity actual1 = jobExecutionDao.read(1L);
        JobExecutionEntity actual2 = jobExecutionDao.read(2L);

        assertThat(jobExecutionDao.readAll().size(), is(expected.size()));
        assertThat(actual1, is(notNullValue()));
        assertThat(actual1.getId(), is(1L));

        assertThat(actual2, is(notNullValue()));
        assertThat(actual2.getId(), is(2L));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createWithNotExistingJobTest() {
        JobExecutionEntity expected = getJobExecutionEntity();
        expected.setJobId(5L);
        jobExecutionDao.create(expected);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createWithInvalidAuditTest() {
        JobExecutionEntity expected = new JobExecutionEntity();
        expected.setJobId(1L);
        expected.setStatus(PENDING);
        expected.addAuditEntity(new JobExecutionAuditEntity(null, System.currentTimeMillis(), PENDING, RUNNING));
        jobExecutionDao.create(expected);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createWithInvalidAuditTest2() {
        JobExecutionEntity expected = new JobExecutionEntity();
        expected.setJobId(1L);
        expected.setStatus(PENDING);
        expected.addAuditEntity(new JobExecutionAuditEntity(getJobExecutionEntity(), System.currentTimeMillis(), PENDING, RUNNING));
        jobExecutionDao.create(expected);
    }

    @Test
    public void readTest() {
        JobExecutionEntity expected = getJobExecutionEntity();
        jobExecutionDao.create(expected);

        JobExecutionEntity actual = jobExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getAuditEntities(), containsInAnyOrder(expected.getAuditEntities().toArray()));
    }

    @Test
    public void readAllTest() {
        List<JobExecutionEntity> expected = getJobExecutionEntities();
        jobExecutionDao.create(expected);

        List<JobExecutionEntity> actuals = (List<JobExecutionEntity>) jobExecutionDao.readAll();

        assertThat(jobExecutionDao.readAll().size(), is(expected.size()));
        for (int i = 0; i < actuals.size(); i++) {
            assertThat(actuals.get(i), is(notNullValue()));
            assertThat(actuals.get(i), is(expected.get(i)));
            assertThat(actuals.get(i).getAuditEntities(), containsInAnyOrder(expected.get(i).getAuditEntities().toArray()));
        }
    }

    @Test
    public void updateTest() {
        JobExecutionEntity expected = getJobExecutionEntity();
        jobExecutionDao.create(expected);

        expected.setStatus(RUNNING);
        jobExecutionDao.update(expected);

        JobExecutionEntity actual = jobExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(jobExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void updateAddAuditTest() {
        JobExecutionEntity expected = getJobExecutionEntity();
        jobExecutionDao.create(expected);

        expected.addAuditEntity(new JobExecutionAuditEntity(expected, System.currentTimeMillis(), PENDING, FINISHED));
        expected.addAuditEntity(new JobExecutionAuditEntity(expected, System.currentTimeMillis(), FINISHED, TIMEOUT));
        jobExecutionDao.update(expected);

        JobExecutionEntity actual = jobExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getAuditEntities(), containsInAnyOrder(expected.getAuditEntities().toArray()));
        assertThat(jobExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void updateDeleteAuditTest() {
        JobExecutionEntity expected = getJobExecutionEntity();
        jobExecutionDao.create(expected);

        expected.getAuditEntities().clear();
        jobExecutionDao.update(expected);

        JobExecutionEntity actual = jobExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getAuditEntities().size(), is(0));
        assertThat(jobExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Create_Test() {
        JobExecutionEntity expected = getJobExecutionEntity();
        jobExecutionDao.createOrUpdate(expected);

        JobExecutionEntity actual = jobExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getAuditEntities(), containsInAnyOrder(expected.getAuditEntities().toArray()));
    }

    @Test
    public void createOrUpdate_Update_Test() {
        JobExecutionEntity expected = getJobExecutionEntity();
        jobExecutionDao.createOrUpdate(expected);

        expected.setStatus(RUNNING);
        jobExecutionDao.createOrUpdate(expected);

        JobExecutionEntity actual = jobExecutionDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(actual.getAuditEntities(), containsInAnyOrder(expected.getAuditEntities().toArray()));
        assertThat(jobExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void deleteByIdTest() {
        List<JobExecutionEntity> expected = getJobExecutionEntities();
        jobExecutionDao.create(expected);
        jobExecutionDao.delete(1L);

        JobExecutionEntity actual = jobExecutionDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(jobExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void deleteTest() {
        List<JobExecutionEntity> expected = getJobExecutionEntities();
        jobExecutionDao.create(expected);
        jobExecutionDao.delete(expected.get(0));

        JobExecutionEntity actual = jobExecutionDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(jobExecutionDao.readAll().size(), is(1));
    }

    @Test
    public void readAllPendingTest() {
        List<JobExecutionEntity> expected = getJobExecutionEntities();
        jobExecutionDao.create(expected);

        List<JobExecutionEntity> actual = jobExecutionDao.readAllPending();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(2));
        assertThat(jobExecutionDao.readAll().size(), is(2));
    }

    private JobExecutionEntity getJobExecutionEntity() {
        JobExecutionEntity jobExecution = new JobExecutionEntity();
        jobExecution.setJobId(1L);
        jobExecution.setStatus(PENDING);
        jobExecution.addAuditEntity(new JobExecutionAuditEntity(jobExecution, System.currentTimeMillis(), PENDING, RUNNING));
        return jobExecution;
    }

    private List<JobExecutionEntity> getJobExecutionEntities() {
        JobExecutionEntity jobExecution1 = new JobExecutionEntity();
        jobExecution1.setJobId(1L);
        jobExecution1.setStatus(PENDING);
        jobExecution1.addAuditEntity(new JobExecutionAuditEntity(jobExecution1, System.currentTimeMillis(), null, PENDING));

        JobExecutionEntity jobExecution2 = new JobExecutionEntity();
        jobExecution2.setJobId(2L);
        jobExecution2.setStatus(PENDING);
        jobExecution2.addAuditEntity(new JobExecutionAuditEntity(jobExecution2, System.currentTimeMillis(), null, PENDING));

        return newArrayList(jobExecution1, jobExecution2);
    }

}