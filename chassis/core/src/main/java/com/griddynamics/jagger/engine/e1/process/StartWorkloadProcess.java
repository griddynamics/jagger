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

package com.griddynamics.jagger.engine.e1.process;

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.invoker.ScenarioFactory;

import java.util.List;

public class StartWorkloadProcess implements Command<String> {
    private ScenarioFactory<Object, Object, Object> scenarioFactory;
    private String sessionId;
    private ScenarioContext scenarioContext;
    private List<KernelSideObjectProvider<Validator>> validators;
    private List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> collectors;
    private List<Provider<InvocationListener<Object, Object, Object>>> listeners;
    private int poolSize;

    public static StartWorkloadProcess create(String sessionId, ScenarioContext scenarioContext, int poolSize) {
        return new StartWorkloadProcess(sessionId, scenarioContext, poolSize);
    }

    private StartWorkloadProcess(String sessionId, ScenarioContext scenarioContext, int poolSize) {
        this.sessionId = sessionId;
        this.scenarioContext = scenarioContext;
        this.poolSize = poolSize;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public String getName() {
        return scenarioContext.getTaskName();
    }

    public String getVersion() {
        return scenarioContext.getVersion();
    }

    public ScenarioFactory<Object, Object, Object> getScenarioFactory() {
        return scenarioFactory;
    }

    public void setScenarioFactory(ScenarioFactory<Object, Object, Object> scenarioFactory) {
        this.scenarioFactory = scenarioFactory;
    }

    public int getThreads() {
        return scenarioContext.getWorkloadConfiguration().getThreads();
    }

    public List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> getCollectors() {
        return collectors;
    }

    public void setCollectors(List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> collectors) {
        this.collectors = collectors;
    }

    public List<KernelSideObjectProvider<Validator>> getValidators() {
        return validators;
    }

    public void setValidators(List<KernelSideObjectProvider<Validator>> validators) {
        this.validators = validators;
    }

    public List<Provider<InvocationListener<Object, Object, Object>>> getListeners() {
        return listeners;
    }

    public void setListeners(List<Provider<InvocationListener<Object, Object, Object>>> listeners) {
        this.listeners = listeners;
    }

    public String getTaskId() {
        return scenarioContext.getTaskId();
    }

    public ScenarioContext getScenarioContext() {
        return this.scenarioContext;
    }

    @Override
    public String toString() {
        return "StartWorkloadProcess{" +
                "scenarioFactory=" + scenarioFactory +
                ", sessionId='" + sessionId + '\'' +
                ", scenarioContext=" + scenarioContext +
                ", collectors=" + collectors +
                ", listeners=" + listeners +
                ", poolSize=" + poolSize +
                '}';
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
