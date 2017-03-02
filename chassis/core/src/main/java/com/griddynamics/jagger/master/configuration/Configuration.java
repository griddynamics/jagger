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

package com.griddynamics.jagger.master.configuration;

import java.util.List;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioListener;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.reporting.ReportingService;

import com.google.common.collect.Lists;

/**
 * The configuration that should be executed by jagger. Provides list of tasks
 * that are intended to be executed step-by-step.
 * 
 * @author Alexey Kiselyov
 */
public class Configuration {
	private List<Task> tasks;
	private List<SessionExecutionListener> sessionExecutionListeners = Lists.newLinkedList();
	private List<DistributionListener> distributionListeners = Lists.newLinkedList();

    private List<Provider<LoadScenarioListener>> loadScenarioListeners = Lists.newLinkedList();

    private ReportingService report;

    public void setReport(ReportingService report){
        this.report = report;
    }

    public ReportingService getReport(){
        return report;
    }



    public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

    public List<Task> getTasks() {
		return this.tasks;
	}

	public List<SessionExecutionListener> getSessionExecutionListeners() {
		return sessionExecutionListeners;
	}

	public void setSessionExecutionListeners(List<SessionExecutionListener> sessionExecutionListeners) {
		this.sessionExecutionListeners = sessionExecutionListeners;
	}

	public void setTaskExecutionListeners(List<DistributionListener> distributionListeners) {
		this.distributionListeners = distributionListeners;
	}

    public List<DistributionListener> getDistributionListeners() {
        return distributionListeners;
    }

    public List<Provider<LoadScenarioListener>> getLoadScenarioListeners() {
        return loadScenarioListeners;
    }

    public void setLoadScenarioListeners(List<Provider<LoadScenarioListener>> loadScenarioListeners) {
        this.loadScenarioListeners = loadScenarioListeners;
    }

}
