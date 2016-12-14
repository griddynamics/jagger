/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
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

package com.griddynamics.jagger.engine.e1.collector;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.AVAILABLE_KERNELS;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.END_TIME;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.ERROR_MESSAGE;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.FAILED;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.KERNELS_COUNT;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.SESSION;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.START_TIME;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.TASK_EXECUTED;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.TaskExecutionStatusProvider;
import com.griddynamics.jagger.master.configuration.SessionExecutionStatus;
import com.griddynamics.jagger.master.configuration.SessionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * Collects basic information on master side. Stores session start/end time,
 * available kernels, task count.
 *
 * @author Mairbek Khadikov
 */
public class BasicSessionCollector implements SessionListener, DistributionListener {
    private KeyValueStorage keyValueStorage;
    private Integer taskCounter;

    TaskExecutionStatusProvider taskExecutionStatusProvider;

    public void setTaskExecutionStatusProvider(TaskExecutionStatusProvider taskExecutionStatusProvider) {
        this.taskExecutionStatusProvider = taskExecutionStatusProvider;
    }

    public void setKeyValueStorage(KeyValueStorage keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
    }

    @Override
    public void onSessionStarted(String sessionId, Multimap<NodeType, NodeId> nodes) {
        taskCounter = 0;

        Namespace namespace = Namespace.of(SESSION, sessionId);
        Multimap<String, Object> objectsMap = HashMultimap.create();
        objectsMap.put(START_TIME, System.currentTimeMillis());
        Collection<NodeId> kernels = nodes.get(NodeType.KERNEL);
        objectsMap.put(KERNELS_COUNT, kernels.size());

        for (NodeId nodeId : kernels) {
            objectsMap.put(AVAILABLE_KERNELS, nodeId.toString());
        }
        keyValueStorage.putAll(namespace, objectsMap);
    }

    @Override
    public void onSessionExecuted(String sessionId, String sessionComment) {
        onSessionExecuted(sessionId, sessionComment, SessionExecutionStatus.EMPTY);
    }

    @Override
    public void onSessionExecuted(String sessionId, String sessionComment, SessionExecutionStatus status) {
        Namespace namespace = Namespace.of(SESSION, sessionId);
        Multimap<String, Object> objectsMap = HashMultimap.create();
        objectsMap.put(END_TIME, System.currentTimeMillis());
        objectsMap.put(TASK_EXECUTED, taskCounter);
        Integer failedTasks = taskExecutionStatusProvider.getTaskIdsWithStatus(TaskData.ExecutionStatus.FAILED).size();
        objectsMap.put(FAILED, failedTasks);
        if(failedTasks > 0) {
            if(SessionExecutionStatus.EMPTY.equals(status)){
                status = SessionExecutionStatus.TASK_FAILED;
            }
        }
        objectsMap.put(ERROR_MESSAGE, status.getMessage());

        keyValueStorage.putAll(namespace, objectsMap);
    }


    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        // do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        taskCounter++;
    }
}
