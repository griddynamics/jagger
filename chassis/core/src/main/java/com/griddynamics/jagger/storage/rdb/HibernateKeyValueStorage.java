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
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.*;

public class HibernateKeyValueStorage extends HibernateDaoSupport implements KeyValueStorage {

    private static final Logger log = LoggerFactory.getLogger(HibernateKeyValueStorage.class);

    private int hibernateBatchSize;

    private int sessionTempDataCount=50;

    private String sessionId;

    public int getHibernateBatchSize() {
        return hibernateBatchSize;
    }

    public int getSessionTempDataCount() {
        return sessionTempDataCount;
    }

    @Required
    public void setSessionTempDataCount(int sessionTempDataCount) {
        if (sessionTempDataCount <0) {
            log.warn("Session count can't be < 0; chassis.storage.temporary.data.session.count is equal {}.", sessionTempDataCount);
            return;
        }
        this.sessionTempDataCount = sessionTempDataCount;
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
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId=sessionId;
    }

    @Override
    public void put(Namespace namespace, String key, Object value) {
        getHibernateTemplate().persist(createKeyValue(namespace, key, value));
    }

    @Override
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

    @Override
    public void deleteAll() {
        ArrayList<String> sessions = (ArrayList) getHibernateTemplate().find("Select distinct k.sessionId from KeyValue k ORDER by k.sessionId");
        if (sessions.size() == 0)
            return;
        if (sessionTempDataCount == 0) {
            log.warn("Session count limit is equal '0', all temporary data about sessions in KeyValue will be delete");
            getHibernateTemplate().bulkUpdate("delete from KeyValue");
            return;
        }
        List<String> sessionForDelete = Lists.newArrayList();
        sessionForDelete.add(sessionId);
        if (sessions.size() > sessionTempDataCount) {
            sessionForDelete.addAll(sessions.subList(0, (sessions.size() - 1) - sessionTempDataCount));
        }
        getHibernateTemplate().bulkUpdate("delete from KeyValue where sessionId in (?)", sessionForDelete.toArray());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object fetch(Namespace namespace, String key) {
        List<KeyValue> values = (List<KeyValue>) getHibernateTemplate()
                .find("from KeyValue kv where kv.namespace = ? and kv.key = ?", namespace.toString(), key);
        if (values.isEmpty()) {
            return null;
        }
        if (values.size() > 1) {
            throw new IllegalStateException("Use fetchAll");
        }
        return SerializationUtils.deserialize(values.get(0).getData());
    }

    @SuppressWarnings("unchecked")
    @Override
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
        keyvalue.setSessionId(sessionId);
        return keyvalue;
    }
}
