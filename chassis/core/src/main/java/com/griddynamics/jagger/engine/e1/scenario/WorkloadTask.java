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

package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.collector.limits.LimitSet;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.invoker.ScenarioFactory;
import com.griddynamics.jagger.master.CompositableTask;

import java.util.List;

/**
 * Workload task configuration.
 *
 * @author Mairbek Khadikov
 */
public class WorkloadTask implements CompositableTask {
    private int number;
    private String name;
    private String version;
    private String description = "";
    private ScenarioFactory<Object, Object, Object> scenarioFactory;
    private List<KernelSideObjectProvider<Validator>> validators = Lists.newLinkedList();
    private List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> collectors = Lists.newLinkedList();
    private List<Provider<InvocationListener<Object, Object, Object>>> listeners = Lists.newLinkedList();
    private WorkloadClockConfiguration clockConfiguration;
    private TerminateStrategyConfiguration terminateStrategyConfiguration;
    private String parentTaskId;
    private Calibrator calibrator = new OneNodeCalibrator();
    private long startDelay = 0;
    private List<Provider<TestListener>> testListeners = Lists.newLinkedList();
    private LimitSet limits = null;

    public long getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(long waitBefore) {
        this.startDelay = waitBefore;
    }

    @Override
    public String getTaskName() {
        return name + " " + version;
    }

    @Override
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ScenarioFactory<Object, Object, Object> getScenarioFactory() {
        return scenarioFactory;
    }

    public void setScenarioFactory(ScenarioFactory<Object, Object, Object> scenarioFactory) {
        this.scenarioFactory = scenarioFactory;
    }

    public List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> getCollectors() {
        return collectors;
    }

    public void setCollectors(List<KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>>> collectors) {
        this.collectors = collectors;
    }

    public WorkloadClockConfiguration getClockConfiguration() {
        return clockConfiguration;
    }

    public void setClockConfiguration(WorkloadClockConfiguration clockConfiguration) {
        this.clockConfiguration = clockConfiguration;
    }

    public TerminateStrategyConfiguration getTerminateStrategyConfiguration() {
        return terminateStrategyConfiguration;
    }

    public void setTerminateStrategyConfiguration(TerminateStrategyConfiguration terminateStrategyConfiguration) {
        this.terminateStrategyConfiguration = terminateStrategyConfiguration;
    }

    public WorkloadClock getClock() {
        return clockConfiguration.getClock();
    }

    public TerminationStrategy getTerminationStrategy() {
        return terminateStrategyConfiguration.getTerminateStrategy();
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

    public WorkloadTask copy() {
        WorkloadTask task = new WorkloadTask();
        task.setNumber(number);
        task.setName(name);
        task.setVersion(version);
        task.setValidators(validators);
        task.setCollectors(collectors);
        task.setListeners(listeners);
        task.setScenarioFactory(scenarioFactory);
        task.setClockConfiguration(clockConfiguration);
        task.setTerminateStrategyConfiguration(terminateStrategyConfiguration);
        task.setCalibrator(calibrator);
        task.setStartDelay(startDelay);
        task.setDescription(description);
        return task;
    }

    @Override
    public String getParentTaskId() {
        return parentTaskId;
    }

    @Override
    public void setParentTaskId(String taskId) {
        this.parentTaskId = taskId;
    }


    public Calibrator getCalibrator() {
        return calibrator;
    }

    public void setCalibrator(Calibrator calibrator) {
        this.calibrator = calibrator;
    }

    public List<Provider<TestListener>> getTestListeners() {
        return testListeners;
    }

    public void setTestListeners(List<Provider<TestListener>> testListeners) {
        this.testListeners = testListeners;
    }

    @Override
    public String toString() {
        return "WorkloadTask {\n" +
                "   number                          = '" + number + "\',\n" +
                "   name                            = '" + name + "\',\n" +
                "   version                         = '" + version + "\',\n" +
                "   scenarioFactory                 = " + scenarioFactory + ",\n" +
                "   listeners                       = " + listeners + ",\n" +
                "   collectors                      = " + collectors + ",\n" +
                "   clockConfiguration              = " + clockConfiguration + ",\n" +
                "   terminateStrategyConfiguration  = " + terminateStrategyConfiguration + ",\n" +
                "   parentTaskId                    = '" + parentTaskId + "',\n" +
                "   startDelay                      = '" + startDelay +
                "'}";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LimitSet getLimits() {
        return limits;
    }

    public void setLimits(LimitSet limits) {
        this.limits = limits;
    }

}
