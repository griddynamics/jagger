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

package com.griddynamics.jagger.engine.e1.aggregator.session;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.END_TIME;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.ERROR_MESSAGE;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.FAILED;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.KERNELS_COUNT;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.SESSION;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.START_TIME;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.TASK_EXECUTED;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.dbapi.entity.SessionData;
import com.griddynamics.jagger.dbapi.entity.TagEntity;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.engine.e1.services.SessionMetaDataStorage;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.TaskExecutionStatusProvider;
import com.griddynamics.jagger.master.configuration.SessionExecutionStatus;
import com.griddynamics.jagger.master.configuration.SessionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.monitoring.MonitoringTask;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Aggregates generic session/task data from key-value storage and stores to
 * relation structure. See {@link com.griddynamics.jagger.dbapi.entity.SessionData},
 * {@link com.griddynamics.jagger.dbapi.entity.TaskData} for table structure.
 *
 * @author Mairbek Khadikov
 */
public class BasicAggregator extends HibernateDaoSupport implements DistributionListener, SessionListener {
    private static final Logger log = LoggerFactory.getLogger(BasicAggregator.class);

    private KeyValueStorage keyValueStorage;
    private SessionMetaDataStorage sessionMetaDataStorage;

    TaskExecutionStatusProvider taskExecutionStatusProvider;

    public void setTaskExecutionStatusProvider(TaskExecutionStatusProvider taskExecutionStatusProvider) {
        this.taskExecutionStatusProvider = taskExecutionStatusProvider;
    }

    @Override
    public void onSessionStarted(String sessionId, Multimap<NodeType, NodeId> nodes) {
        log.debug("onSessionStarted invoked");
    }

    @Override
    public void onSessionExecuted(String sessionId, String sessionComment) {
        onSessionExecuted(sessionId, sessionComment, SessionExecutionStatus.EMPTY);
    }

    @Override
    public void onSessionExecuted(String sessionId, String sessionComment, SessionExecutionStatus status) {
        log.debug("onSessionExecuted invoked");

        Namespace namespace = Namespace.of(SESSION, sessionId);
        Multimap<String, Object> all = keyValueStorage.fetchAll(namespace);

        SessionData sessionData = new SessionData();

        sessionData.setSessionId(sessionId);
        sessionData.setComment(sessionComment);

        Long startTime = (Long) getFirst(all, START_TIME);
        sessionData.setStartTime(new Date(startTime));

        Long endTime = (Long) getFirst(all, END_TIME);
        sessionData.setEndTime(new Date(endTime));

        Integer taskExecuted = (Integer) getFirst(all, TASK_EXECUTED);
        sessionData.setTaskExecuted(taskExecuted);

        Integer tasksFailed = (Integer) getFirst(all, FAILED);
        sessionData.setTaskFailed(tasksFailed);

        Integer activeKernels = (Integer) getFirst(all, KERNELS_COUNT);
        sessionData.setActiveKernels(activeKernels);

        String errorMessage = (String) getFirst(all, ERROR_MESSAGE);
        sessionData.setErrorMessage(errorMessage);
        getHibernateTemplate().persist(sessionData);
        persistTags(sessionId, sessionMetaDataStorage);
    }

    public void setKeyValueStorage(KeyValueStorage keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
    }

    public void setSessionMetaDataStorage(SessionMetaDataStorage sessionMetaDataStorage) {
        this.sessionMetaDataStorage = sessionMetaDataStorage;
    }

    private static Object getFirst(Multimap<String, Object> all, String key) {
        Iterator<Object> iterator = all.get(key).iterator();
        if (!iterator.hasNext()) {
            throw new IllegalStateException("key " + key + " has not been saved during test execution");
        }
        return iterator.next();
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        log.debug("onTaskStarted invoked");
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        if (task instanceof MonitoringTask){
            return;
        }

        log.debug("onTaskFinished invoked {} {}", sessionId, taskId);
        persistData(sessionId, taskId, task);
    }

    private void persistData(String sessionId, String taskId, Task task) {
        TaskData taskData = new TaskData();
        taskData.setTaskId(taskId);
        taskData.setSessionId(sessionId);
        taskData.setTaskName(task.getTaskName());
        taskData.setNumber(task.getNumber());
        taskData.setStatus(taskExecutionStatusProvider.getStatus(taskId));
        getHibernateTemplate().persist(taskData);
    }

    public void persistTags(String sessionId, SessionMetaDataStorage metaDataStorage) {
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        for (TagEntity tagEntity : metaDataStorage.getTagsForSaveOrUpdate()) {
            try {
                hibernateTemplate.saveOrUpdate(tagEntity);
            } catch (HibernateException e) {
                log.error("Cannot add new tag", e);
            }
        }

        if (!metaDataStorage.getSessionTags().isEmpty()) {
            Set<TagEntity> sessionTagList = new HashSet<TagEntity>();
            sessionTagList.addAll((Collection<? extends TagEntity>) hibernateTemplate.findByNamedParam("select tags from TagEntity as tags " +
                    "where tags.name in (:sTagsName)", "sTagsName", metaDataStorage.getSessionTags()));

            if (!sessionTagList.isEmpty()) {
                List<SessionData> sessionsById = (List<SessionData>) hibernateTemplate.find("from SessionData s where s.sessionId=?", sessionId);
                if (sessionsById.size()==1)
                    for (SessionData sessionData : sessionsById) {
                        sessionData.setTags(sessionTagList);
                        hibernateTemplate.saveOrUpdate(sessionData);
                    }
                else
                    log.error("Must be one session's id which is equals {}, but got {} ids",sessionId,sessionsById.size());
            } else
                log.info("No tags for mark session {}", sessionId);
        }
    }

}
