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

import javax.persistence.*;

/**
 * User: nmusienko
 * Date: 18.03.13
 * Time: 17:10
 */

@Deprecated
@Entity
public class MetricDetails {

    public static final int ALLOCATION_SIZE = 100;
    public static final String METRIC_ID = "MetricDetails_ID";

    @TableGenerator(name="GENERATOR",
                    table="IdGeneratorEntity",

                    pkColumnName="tableName",
                    valueColumnName="idValue",
                    pkColumnValue=METRIC_ID,

                    //do not change allocationSize value, it will cause duplicated key problem
                    allocationSize=ALLOCATION_SIZE,
                    initialValue = 0
    )
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="GENERATOR")
    private Long id;

    @Column
    private long time;

    @Column
    private String metric;

    @Column
    private Double value;

    @ManyToOne
    private TaskData taskData;

    public MetricDetails(long time, String metric, Double value, TaskData taskData) {
        this.time = time;
        this.metric = metric;
        this.value = value;
        this.taskData = taskData;
    }

    public MetricDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }
}
