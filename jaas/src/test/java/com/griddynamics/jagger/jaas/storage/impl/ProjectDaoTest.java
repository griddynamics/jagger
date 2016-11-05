package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.config.TestPersistenceConfig;
import com.griddynamics.jagger.jaas.storage.DbConfigDao;
import com.griddynamics.jagger.jaas.storage.ProjectDao;
import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import com.griddynamics.jagger.jaas.storage.model.ProjectEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.jaas.storage.impl.DbConfigDaoTest.getDbConfigEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPersistenceConfig.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ProjectDaoTest {

    private static final long DB_ID = 1L;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private DbConfigDao dbConfigDao;

    @Before
    public void setUp() {
        DbConfigEntity dbConfigEntity = getDbConfigEntity();
        dbConfigDao.create(dbConfigEntity);
    }

    @Test
    public void readTest() {
        ProjectEntity expected = getProjectEntity();
        projectDao.create(expected);

        ProjectEntity actual = projectDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
    }

    @Test
    public void readAllTest() {
        List<ProjectEntity> expected = newArrayList(getProjectEntity(), getProjectEntity(), getProjectEntity());
        expected.forEach(project -> projectDao.create(project));

        List<ProjectEntity> actuals = (List<ProjectEntity>) projectDao.readAll();

        assertThat(actuals.size(), is(expected.size()));
        for (int i = 0; i < actuals.size(); i++) {
            assertThat(actuals.get(i), is(notNullValue()));
            assertThat(actuals.get(i), is(expected.get(i)));
        }
    }

    @Test
    public void updateTest() {
        ProjectEntity expected = getProjectEntity();
        projectDao.create(expected);
        expected.setDescription(null);
        expected.setVersion("2.0");

        projectDao.update(expected);

        ProjectEntity actual = projectDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(projectDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Create_Test() {
        ProjectEntity expected = getProjectEntity();
        projectDao.createOrUpdate(expected);

        ProjectEntity actual = projectDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(projectDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Update_Test() {
        ProjectEntity expected = getProjectEntity();
        projectDao.create(expected);

        expected.setDescription(null);
        expected.setVersion("2.0");
        projectDao.createOrUpdate(expected);

        ProjectEntity actual = projectDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(projectDao.readAll().size(), is(1));
    }

    @Test
    public void deleteByIdTest() {
        ProjectEntity expected = getProjectEntity();
        projectDao.create(expected);
        projectDao.delete(1L);

        ProjectEntity actual = projectDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(projectDao.readAll().size(), is(0));
    }

    @Test
    public void deleteTest() {
        ProjectEntity expected = getProjectEntity();
        projectDao.create(expected);
        projectDao.delete(expected);

        ProjectEntity actual = projectDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(projectDao.readAll().size(), is(0));
    }

    private ProjectEntity getProjectEntity() {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setDescription("Description");
        projectEntity.setVersion("1.0");
        projectEntity.setZipPath("path");
        projectEntity.setDbId(DB_ID);
        return projectEntity;
    }
}
