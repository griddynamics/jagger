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

package com.griddynamics.jagger.dbapi.entity;

import com.griddynamics.jagger.dbapi.parameter.MonitoringParameterBean;
import org.hibernate.annotations.Index;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Deprecated
public class MonitoringStatistics {
    @Id
    // Identity strategy is not supported by Oracle DB from the box
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "boxIdentifier")
    @Index(name = "boxIdentifier_index")
    private String boxIdentifier;

    @Column(name = "systemUnderTestUrl")
    @Index(name = "systemUnderTestUrl_index")
    private String systemUnderTestUrl;

    @Column(name = "sessionId")
    @Index(name = "sessionId_index")
    private String sessionId;

    @Column(name = "time")
    private long time;

    @Column(name = "averageValue")
    private Double averageValue;

    @ManyToOne
    private TaskData taskData;

    @Embedded
    private MonitoringParameterBean parameterId;

    public MonitoringStatistics() {
    }

    public MonitoringStatistics(String boxIdentifier, String systemUnderTestUrl, String sessionId, TaskData taskData, long time,
                                MonitoringParameterBean parameterId, Double value) {
        this.boxIdentifier = boxIdentifier;
        this.systemUnderTestUrl = systemUnderTestUrl;
        this.sessionId = sessionId;
        this.taskData = taskData;
        this.time = time;
        this.parameterId = parameterId;
        this.averageValue = value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setAverageValue(Double averageValue) {
        this.averageValue = averageValue;
    }

    public Double getAverageValue() {
        return averageValue;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    public TaskData getTaskData() {
        return this.taskData;
    }

    public void setParameterId(MonitoringParameterBean parameterId) {
        this.parameterId = parameterId;
    }

    public MonitoringParameterBean getParameterId() {
        return this.parameterId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getBoxIdentifier() {
        return boxIdentifier;
    }

    public void setBoxIdentifier(String boxIdentifier) {
        this.boxIdentifier = boxIdentifier;
    }

    public String getSystemUnderTestUrl() {
        return systemUnderTestUrl;
    }

    public void setSystemUnderTestUrl(String systemUnderTestUrl) {
        this.systemUnderTestUrl = systemUnderTestUrl;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("boxIdentifier", boxIdentifier)
                .add("systemUnderTestUrl", systemUnderTestUrl)
                .add("sessionId", sessionId)
                .add("taskData", taskData)
                .add("time", time)
                .add("parameterId", parameterId)
                .add("averageValue", averageValue)
                .toString();
    }

}
