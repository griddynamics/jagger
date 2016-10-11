package com.griddynamics.jagger.master.database;

import com.griddynamics.jagger.dbapi.entity.DiagnosticResultEntity;
import com.griddynamics.jagger.dbapi.entity.MetricDetails;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: kgribov
 * Date: 1/17/14
 */
@Deprecated
public class MetricTablesChecker extends HibernateDaoSupport {

    private static final Logger log = LoggerFactory.getLogger(MetricTablesChecker.class);

    @Deprecated
    public void checkMetricDetailsIndex() {
        //check if metricId record is already in IdGenerator table
        IdGeneratorEntity metricIdGenerator = null;
        try {
            metricIdGenerator = getHibernateTemplate().get(IdGeneratorEntity.class, MetricDetails.METRIC_ID);
        } catch (Exception ex) {
            log.debug("IdGenerator is missing for entity {}", "MetricDetails");
        }
        if (metricIdGenerator != null) {
            return;
        }

        List<Long> maxMetricId = Collections.emptyList();
        try { //try to get the last MetricDetails entity
            maxMetricId = (List<Long>) getHibernateTemplate().findByCriteria(
                    DetachedCriteria.forClass(MetricDetails.class).setProjection(Projections.max("id"))
            );
        } catch (Exception ex) {
            log.debug("Database is new, use annotation @TableGenerator to create IdGenerator table");
        }
        // if database already exist
        if (maxMetricId != null && !maxMetricId.isEmpty()) {
            Long lastIndex = maxMetricId.iterator().next();
            if (lastIndex == null) {
                return;
            }

            final long initialValue = ++lastIndex + MetricDetails.ALLOCATION_SIZE;
            getHibernateTemplate().execute(
                    new HibernateCallback<Void>() {
                        @Override
                        public Void doInHibernate(Session session) throws HibernateException, SQLException {
                            session.persist(new IdGeneratorEntity(MetricDetails.METRIC_ID, initialValue));
                            session.flush();
                            return null;
                        }
                    }
            );
        }
    }

    @Deprecated
    public void checkMetricColumnsHaveDoubleType(){
        boolean needToPrintMessage = false;
        List<ColumnType> metricColumnsToCheck = Arrays.asList(new ColumnType(MetricDetails.class.getSimpleName(), "value", "double"),
                                                              new ColumnType(DiagnosticResultEntity.class.getSimpleName(), "total", "double"));
        for (ColumnType metricColumn : metricColumnsToCheck){
            String oldType = validateType(metricColumn);
            if (oldType != null){
                needToPrintMessage = true;
                log.warn(
                        "Your database is out of date. In column {}.{} expected {}, but found {}",
                        new Object[]{metricColumn.getEntityName(), metricColumn.getPropertyName(), metricColumn.getExpectedType(), oldType}
                );
            }
        }

        if (needToPrintMessage){
            log.warn(warningMessage);
            // sleep for 4 sec to show warning message
            // TODO: why do we need to sleep here???
            try {
                Thread.currentThread().sleep(4*1000);
            } catch (InterruptedException e) {
                log.error("Error during try to sleep",e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private String validateType(final ColumnType expectedType){
        return getHibernateTemplate().execute(new HibernateCallback<String>() {
                                                  @Override
                                                  public String doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                                                      SQLQuery query = session.createSQLQuery("SELECT DATA_TYPE " +
                                                                                              "FROM information_schema.COLUMNS " +
                                                                                              "WHERE TABLE_SCHEMA=DATABASE() " +
                                                                                              "AND TABLE_NAME='"+expectedType.entityName+"'" +
                                                                                              "AND column_name='"+expectedType.propertyName+"'");
                                                      List results = query.list();
                                                      if (!results.isEmpty()){
                                                          String value = (String)results.iterator().next();
                                                          if (!value.equals(expectedType.getExpectedType())){
                                                              return value;
                                                          }
                                                      }
                                                      return null;
                                                  }
                                              });
    }

    String line = "------------------------------------------------------------------------------------------------------------------------------\n";
    private String warningMessage = "\n" + line +
                                    "W A R N I N G \n" +
                                    line +
                                    "Starting from Jagger release 1.2.2 it is possible to save double, long, int metric values (before - only int). \n" +
                                    "In future all new Jagger metrics will be stored as double values.\n" +

                                    "To support this option we recommend to update type of two columns in your DB.\n" +

                                    "To update, please execute following SQL queries:\n"+
                                    "\n" +
                                    "          ALTER TABLE `SCHEMA_NAME`.`MetricDetails` CHANGE COLUMN `value` `value` DOUBLE NULL DEFAULT NULL ;\n" +
                                    "          ALTER TABLE `SCHEMA_NAME`.`DiagnosticResultEntity` CHANGE COLUMN `total` `total` DOUBLE NULL DEFAULT NULL ;\n \n" +
                                    "          where SCHEMA_NAME is a name of your database schema\n"+
                                    "\n" +

                                    "No previously saved data will be affected\n" +
                                    line;

    private class ColumnType {
        private String entityName;
        private String propertyName;
        private String expectedType;

        private ColumnType(String entityName, String propertyName, String expectedType) {
            this.entityName = entityName;
            this.propertyName = propertyName;
            this.expectedType = expectedType;
        }

        private String getEntityName() {
            return entityName;
        }

        private String getPropertyName() {
            return propertyName;
        }

        private String getExpectedType() {
            return expectedType;
        }
    }
}