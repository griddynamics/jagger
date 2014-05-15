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

import java.util.List;

@Entity
public class TimeInvocationStatistics {
    @Id
    // Identity strategy is not supported by Oracle DB from the box
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long time;

    @Column
    private Double latency;

    @Column
    private Double latencyStdDev;

    @Column
    private Double throughput;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "timeInvocationStatistics")
    private List<TimeLatencyPercentile> percentiles;

    @ManyToOne
    private TaskData taskData;

    public TimeInvocationStatistics() {
    }

    public TimeInvocationStatistics(long time, Double latency, Double latencyStdDev, Double throughput, TaskData taskData) {
        this.time = time;
        this.latency = latency;
        this.latencyStdDev = latencyStdDev;
        this.throughput = throughput;
        this.taskData = taskData;
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

    public Double getLatency() {
        return latency;
    }

    public void setLatency(Double latency) {
        this.latency = latency;
    }

    public Double getLatencyStdDev() {
        return latencyStdDev;
    }

    public void setLatencyStdDev(Double latencyStdDev) {
        this.latencyStdDev = latencyStdDev;
    }

    public Double getThroughput() {
        return throughput;
    }

    public void setThroughput(Double throughput) {
        this.throughput = throughput;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    public List<TimeLatencyPercentile> getPercentiles() {
        return percentiles;
    }

    public void setPercentiles(List<TimeLatencyPercentile> percentiles) {
        this.percentiles = percentiles;
    }
}
