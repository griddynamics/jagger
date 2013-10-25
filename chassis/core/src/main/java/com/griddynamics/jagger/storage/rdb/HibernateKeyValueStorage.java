/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.storage.rdb;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.util.SerializationUtils;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HibernateKeyValueStorage extends HibernateDaoSupport implements KeyValueStorage {

    private static final Logger log = LoggerFactory.getLogger(HibernateKeyValueStorage.class);

    private int hibernateBatchSize;

    public int getHibernateBatchSize() {
        return hibernateBatchSize;
    }

    @Required
    public void setHibernateBatchSize(int hibernateBatchSize) {
        this.hibernateBatchSize = hibernateBatchSize;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void initialize() {
        validate();
    }

    @Override
    public void put(Namespace namespace, String key, Object value) {
        getHibernateTemplate().persist(createKeyValue(namespace, key, value));
    }

    public void putAll(Namespace namespace, Multimap<String, Object> valuesMap) {
        Session session = null;
        int count = 0;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            session.beginTransaction();
            for (String key : valuesMap.keySet()) {
                Collection<Object> values = valuesMap.get(key);
                for (Object val : values) {
                    session.save(createKeyValue(namespace, key, val));
                    count++;
                    if (count % getHibernateBatchSize() == 0) {
                        session.flush();
                        session.clear();
                    }
                }
            }
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object fetch(Namespace namespace, String key) {
        List<KeyValue> values = getHibernateTemplate().find("from KeyValue kv where kv.namespace = ? and kv.key = ?",
                namespace.toString(), key);
        if (values.isEmpty()) {
            return null;
        }
        if (values.size() > 1) {
            throw new IllegalStateException("Use fetchAll");
        }
        return SerializationUtils.deserialize(values.get(0).getData());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object> fetchAll(Namespace namespace, String key) {
        List<KeyValue> entities = (List<KeyValue>) getHibernateTemplate().find(
                "from KeyValue kv where kv.namespace = ? and kv.key = ?", namespace.toString(), key);
        Collection<Object> result = Lists.newLinkedList();
        for (KeyValue entity : entities) {
            result.add(SerializationUtils.deserialize(entity.getData()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Multimap<String, Object> fetchAll(Namespace namespace) {
        List<KeyValue> entities = (List<KeyValue>) getHibernateTemplate().find(
                "from KeyValue kv where kv.namespace = ?", namespace.toString());
        Multimap<String, Object> result = ArrayListMultimap.create();
        for (KeyValue entity : entities) {
            result.put(entity.getKey(), SerializationUtils.deserialize(entity.getData()));
        }
        return result;
    }

    @Override
    public Object fetchNotNull(Namespace namespace, String key) {
        Object result = fetch(namespace, key);
        if (result == null) {
            throw new IllegalStateException("Cannot find value for namespace " + namespace + " and key " + key);
        }
        return result;
    }

    private KeyValue createKeyValue(Namespace namespace, String key, Object value) {
        KeyValue keyvalue = new KeyValue();
        keyvalue.setNamespace(namespace.toString());
        keyvalue.setKey(key);
        keyvalue.setData(SerializationUtils.serialize(value));
        return keyvalue;
    }

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

    private final List<ColumnType> types = Arrays.asList(new ColumnType("MetricDetails",          "value", "double"),
                                                         new ColumnType("DiagnosticResultEntity", "total", "double"));


    private void validate() {
        boolean needToPrintMessage = false;
        for (ColumnType type : types){
            String oldType = validateType(type);
            if (oldType != null){
                needToPrintMessage = true;
                log.warn("Your database is out of date. In column {}.{} expected {}, but found {}", new Object[]{type.getEntityName(), type.getPropertyName(), type.getExpectedType(), oldType});
            }
        }

        if (needToPrintMessage)
            log.warn(warningMessage);

        // sleep for 4 sec
        try {
            Thread.currentThread().sleep(4*1000);
        } catch (InterruptedException e) {
            log.error("Error during try to sleep",e);
            Thread.currentThread().interrupt();
        }
    }

    private String validateType(final ColumnType expectedType){
        return getHibernateTemplate().execute(new HibernateCallback<String>() {
            @Override
            public String doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery("SELECT column_type " +
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

}
