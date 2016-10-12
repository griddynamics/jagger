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
package com.griddynamics.jagger.master;

import com.griddynamics.jagger.dbapi.entity.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Nikolay Musienko
 *         Date: 15.05.13
 */

public class TaskExecutionStatusProvider {

    private static final Logger log = LoggerFactory.getLogger(TaskExecutionStatusProvider.class);

    private final ConcurrentMap<String, TaskData.ExecutionStatus> statusMap = new ConcurrentHashMap<String, TaskData.ExecutionStatus>();
    
    private final TaskIdProvider taskIdProvider;
    
    public TaskExecutionStatusProvider(TaskIdProvider taskIdProvider) {this.taskIdProvider = taskIdProvider;}
    
    public TaskData.ExecutionStatus getStatus(final String taskId) {
        return statusMap.get(taskId);
    }
    
    public TaskData.ExecutionStatus getStatus(final Integer taskId) {
        return getStatus(taskIdProvider.stringify(taskId));
    }

    public void setStatus(final String taskId, final TaskData.ExecutionStatus status){
        statusMap.put(taskId, status);
    }

    public Set<String> getTaskIdsWithStatus(final TaskData.ExecutionStatus status) {
        Preconditions.checkNotNull(status);
    
        Set<String> ret = Sets.newHashSet();
        for (Map.Entry<String, TaskData.ExecutionStatus> entry : statusMap.entrySet()) {
            if (status.equals(entry.getValue())) {
                ret.add(entry.getKey());
            }
        }
        return ret;
    }
}
