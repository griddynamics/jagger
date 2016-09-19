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

package com.griddynamics.jagger.master.configuration;

import com.griddynamics.jagger.dbapi.entity.TaskData;

/**
 * Basic thread safe Task class which represents Jagger test notion.
 *
 * @author Alexey Kiselyov
 */
public abstract class Task {
    
    private String name;
    private int groupNumber;
    private String sessionId;
    private String taskId;
    private volatile Long startDate;
    private volatile Long endDate;
    private volatile TaskData.ExecutionStatus status;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getGroupNumber() {
        return groupNumber;
    }
    
    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public Long getStartDate() {
        return startDate;
    }
    
    /**
     * Initializes {@link #startDate} to current millis.
     * {@link #startDate} can be initialized only once.
     * @return value of {@link #startDate}
     */
    public Long setStartDate() {
        if (startDate == null) {
            startDate = System.currentTimeMillis();
        }
        return startDate;
    }
    
    public Long getEndDate() {
        return endDate;
    }
    
    /**
     * Initializes {@link #endDate} to current millis.
     * {@link #endDate} can be initialized only once.
     * @return value of {@link #endDate}
     */
    public Long setEndDate() {
        if (endDate == null) {
            endDate = System.currentTimeMillis();
        }
        return endDate;
    }
    
    public TaskData.ExecutionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskData.ExecutionStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Task{" + "name='" + name + '\'' + ", groupNumber=" + groupNumber + ", sessionId='" + sessionId + '\''
               + ", taskId='" + taskId + '\'' + ", startDate=" + startDate + ", endDate=" + endDate + ", status="
               + status + '}';
    }
}
