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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Stores data in one table in relation database.
 *
 * @author Mairbek Khadikov
 */
public class OneTableJdbcKeyValueStorage implements KeyValueStorage {
    private static final Logger log = LoggerFactory.getLogger(OneTableJdbcKeyValueStorage.class);

    private static String TABLE_NAME = "keyvalue";

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isAvailable() {
        List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("SHOW TABLES");
        log.debug("{}", queryForList);

        for (Map<String, Object> map : queryForList) {
            if (TABLE_NAME.equalsIgnoreCase((String) map.get("TABLE_NAME"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize() {
        try {
            createTable();
        } catch (Throwable e) {
            throw new TechnicalException(e);
        }
    }

    private void createTable() {
        jdbcTemplate.execute("create table " + TABLE_NAME
                + " (id int primary key AUTO_INCREMENT, name varchar, key varchar, value blob)");
    }

    @Override
    public void put(Namespace namespace, String key, Object value) {
        jdbcTemplate.update("insert into " + TABLE_NAME + " (name, key, value) values (?, ?, ?)", new Object[] {
                namespace.toString(), key, SerializationUtils.serialize(value) });
    }

    @Override
    public void putAll(Namespace namespace, Multimap<String, Object> valuesMap) {
        StringBuilder sb = new StringBuilder();
        for (String key : valuesMap.keySet()) {
            for (Object value : valuesMap.get(key)) {
                sb.append("insert into ");
                sb.append("TABLE_NAME ");
                sb.append(" (name, key, value) values ");
                sb.append("(");
                sb.append(SerializationUtils.serialize(value));
                sb.append(namespace);
                sb.append(",");
                sb.append(key);
                sb.append(",");
                sb.append(")");
                sb.append(",");
            }
        }
        jdbcTemplate.update(sb.toString().substring(0, sb.length() - 1));
    }

    @Override
    public Object fetch(Namespace namespace, String key) {
        try {
            Object obj = jdbcTemplate.queryForObject("select value from " + TABLE_NAME + " where name=? and key=?",
                    new Object[] { namespace.toString(), key }, Object.class);
            return SerializationUtils.deserialize((byte[]) obj);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<Object> fetchAll(Namespace namespace, String key) {
        List<Map<String, Object>> fetch;
        try {
            fetch = jdbcTemplate.queryForList(
                    "select value as value from " + TABLE_NAME + " where name=? and key=? ",
                    new Object[] { namespace.toString(), key });
        } catch (EmptyResultDataAccessException e) {
            return ImmutableList.of();
        }
        List<Object> result = Lists.newLinkedList();
        for (Map<String, Object> map : fetch) {
            byte[] data = (byte[]) map.get("value");
            Object value = SerializationUtils.deserialize(data);
            result.add(value);
        }
        return result;
    }

    @Override
    public Multimap<String, Object> fetchAll(Namespace namespace) {
        List<Map<String, Object>> fetch;
        try {
            fetch = jdbcTemplate.queryForList(
                    "select key as key, value as value from " + TABLE_NAME + " where name=? ",
                    new Object[] { namespace.toString()});
        } catch (EmptyResultDataAccessException e) {
            return ImmutableMultimap.of();
        }
        Multimap<String, Object> result = ArrayListMultimap.create();
        for (Map<String, Object> map : fetch) {
            String key = (String) map.get("key");
            byte[] data = (byte[]) map.get("value");
            Object value = SerializationUtils.deserialize(data);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Object fetchNotNull(Namespace namespace, String key) {
        // TODO avoid copy-paste
        Object result = fetch(namespace, key);
        if (result == null) {
            throw new IllegalStateException("Cannot find value for namespace " + namespace + " and key " + key);
        }
        return result;
    }
}
