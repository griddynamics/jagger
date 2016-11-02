package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.config.TestPersistenceConfig;
import com.griddynamics.jagger.jaas.storage.DbConfigDao;
import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPersistenceConfig.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class DbConfigDaoTest {

    @Autowired
    private DbConfigDao dbConfigDao;

    @Test
    public void readTest() {
        DbConfigEntity expected = getDbConfigEntity();
        dbConfigDao.create(expected);

        DbConfigEntity actual = dbConfigDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
    }

    @Test
    public void readAllTest() {
        List<DbConfigEntity> expected = newArrayList(getDbConfigEntity(), getDbConfigEntity(), getDbConfigEntity());
        expected.forEach(project -> dbConfigDao.create(project));

        List<DbConfigEntity> actuals = (List<DbConfigEntity>) dbConfigDao.readAll();

        assertThat(actuals.size(), is(expected.size()));
        for (int i = 0; i < actuals.size(); i++) {
            assertThat(actuals.get(i), is(notNullValue()));
            assertThat(actuals.get(i), is(expected.get(i)));
        }
    }

    @Test
    public void updateTest() {
        DbConfigEntity expected = getDbConfigEntity();
        dbConfigDao.create(expected);

        expected.setDesc(null);
        expected.setUser("new_user");
        dbConfigDao.update(expected);

        DbConfigEntity actual = dbConfigDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(dbConfigDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Create_Test() {
        DbConfigEntity expected = getDbConfigEntity();
        dbConfigDao.createOrUpdate(expected);

        DbConfigEntity actual = dbConfigDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(dbConfigDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Update_Test() {
        DbConfigEntity expected = getDbConfigEntity();
        dbConfigDao.create(expected);

        expected.setDesc(null);
        expected.setUser("new_user");
        dbConfigDao.createOrUpdate(expected);

        DbConfigEntity actual = dbConfigDao.read(1L);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(dbConfigDao.readAll().size(), is(1));
    }

    @Test
    public void deleteByIdTest() {
        DbConfigEntity expected = getDbConfigEntity();
        dbConfigDao.create(expected);
        dbConfigDao.delete(1L);

        DbConfigEntity actual = dbConfigDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(dbConfigDao.readAll().size(), is(0));
    }

    @Test
    public void deleteTest() {
        DbConfigEntity expected = getDbConfigEntity();
        dbConfigDao.create(expected);
        dbConfigDao.delete(expected);

        DbConfigEntity actual = dbConfigDao.read(1L);

        assertThat(actual, is(nullValue()));
        assertThat(dbConfigDao.readAll().size(), is(0));
    }


    protected static DbConfigEntity getDbConfigEntity() {
        DbConfigEntity dbConfigEntity = new DbConfigEntity();
        dbConfigEntity.setDesc("Desc");
        dbConfigEntity.setHibernateDialect("Dialect");
        dbConfigEntity.setJdbcDriver("Driver");
        dbConfigEntity.setPass("pass");
        dbConfigEntity.setUser("user");
        dbConfigEntity.setUrl("url");
        return dbConfigEntity;
    }
}
