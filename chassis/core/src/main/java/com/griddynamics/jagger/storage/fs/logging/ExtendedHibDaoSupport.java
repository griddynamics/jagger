package com.griddynamics.jagger.storage.fs.logging;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.CollectorDescription;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.SQLException;

public class ExtendedHibDaoSupport extends HibernateDaoSupport {

    /**
     * Helps to fetch CollectorDescription.
     * @param name id of metric
     * @param displayName to display on pdf and in web ui
     * @return if CollectorDescription was found return it, otherwise persist new one and return
     */
    public CollectorDescription getCollectorDescription (String name, String displayName) {

        // to avoid storing same strings
        if (name.equals(displayName)) {
            displayName = null;
        }

        CollectorDescription description = fetchCollectorDescription(name, displayName);
        if (description == null) {
            description = new CollectorDescription(name, displayName);
            getHibernateTemplate().persist(description);
        }
        return description;
    }

    public CollectorDescription getCollectorDescription (CollectorDescription description) {
        return getCollectorDescription(description.getName(), description.getDisplayName());
    }

    private CollectorDescription fetchCollectorDescription (final String name, final String displayName) {

        return getHibernateTemplate().execute(new HibernateCallback<CollectorDescription>() {
            @Override
            public CollectorDescription doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria cr = session.createCriteria(CollectorDescription.class);
                cr.add(Restrictions.eq(Strings.NAME, name));
                cr.add(displayName == null ? Restrictions.isNull(Strings.DISPLAY_NAME) : Restrictions.eq(Strings.DISPLAY_NAME, displayName));
                return (CollectorDescription)cr.uniqueResult();
            }
        });
    }

    private static class Strings {
        private static final String DISPLAY_NAME = "displayName";
        private static final String NAME = "name";
    }
}
