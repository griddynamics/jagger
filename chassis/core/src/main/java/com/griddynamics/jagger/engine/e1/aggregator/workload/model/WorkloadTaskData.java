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

package com.griddynamics.jagger.engine.e1.aggregator.workload.model;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
public class WorkloadTaskData {
	private Long id;
	private String sessionId;
	private String taskId;
    private Integer number;
	private WorkloadDetails scenario;
    private Integer samples;
    private String clock;
    private Integer clockValue;
    private String termination;
	private Integer kernels;
	private BigDecimal totalDuration;
	private BigDecimal throughput;
	private Integer failuresCount;
	private BigDecimal successRate;
	private BigDecimal avgLatency;
	private BigDecimal stdDevLatency;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @ManyToOne
	public WorkloadDetails getScenario() {
		return scenario;
	}

	public void setScenario(WorkloadDetails scenario) {
		this.scenario = scenario;
	}

    public Integer getSamples() {
        return samples;
    }

    public void setSamples(Integer samples) {
        this.samples = samples;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public void setKernels(Integer kernels) {
		this.kernels = kernels;
	}

	public Integer getKernels() {
		return kernels;
	}

	public BigDecimal getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(BigDecimal totalDuration) {
		this.totalDuration = totalDuration;
	}

	public BigDecimal getThroughput() {
		return throughput;
	}

	public void setThroughput(BigDecimal throughput) {
		this.throughput = throughput;
	}

	public Integer getFailuresCount() {
		return failuresCount;
	}

	public void setFailuresCount(Integer failesCount) {
		this.failuresCount = failesCount;
	}

    @Column(precision=10, scale=4)
	public BigDecimal getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(BigDecimal successRate) {
		this.successRate = successRate;
	}

    @Column(precision=10, scale=4)
	public BigDecimal getAvgLatency() {
		return avgLatency;
	}

	public void setAvgLatency(BigDecimal avgLatency) {
		this.avgLatency = avgLatency;
	}

    @Column(precision=10, scale=4)
	public BigDecimal getStdDevLatency() {
		return stdDevLatency;
	}

	public void setStdDevLatency(BigDecimal stdDevLatency) {
		this.stdDevLatency = stdDevLatency;
	}

    public Integer getClockValue() {
        return clockValue;
    }

    public void setClockValue(Integer clockValue) {
        this.clockValue = clockValue;
    }
}
