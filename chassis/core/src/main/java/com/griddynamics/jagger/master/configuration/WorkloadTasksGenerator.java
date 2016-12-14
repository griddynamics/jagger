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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.exception.ConfigurationException;
import com.griddynamics.jagger.master.CompositableTask;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.monitoring.MonitoringTask;

import java.util.List;
import java.util.Set;

public class WorkloadTasksGenerator {
    private List<WorkloadTask> prototypes;
    private List<WorkloadClockConfiguration> clocks;
    private List<TerminateStrategyConfiguration> terminations;
    private MonitoringTask attendantMonitoring;

    public List<Task> generate() {
        validate();

        List<Task> result = Lists.newLinkedList();
        int number = 0;
        for (WorkloadClockConfiguration clock : clocks) {
            for (WorkloadTask prototype : prototypes) {
                for (TerminateStrategyConfiguration termination : terminations) {
                    WorkloadTask workloadTask = prototype.copy();
                    workloadTask.setNumber(++number);
                    workloadTask.setName(workloadTask.getName() + "---" + stringOf(termination));
                    workloadTask.setTerminateStrategyConfiguration(termination);
                    workloadTask.setClockConfiguration(clock);

                    Task task = workloadTask;
                    if (attendantMonitoring != null) {
                        CompositeTask composite = new CompositeTask();
                        composite.setLeading(ImmutableList.<CompositableTask>of(workloadTask));
                        composite.setAttendant(ImmutableList.<CompositableTask>of(attendantMonitoring));

                        task = composite;
                    }

                    result.add(task);
                }
            }
        }
        return result;
    }

    private String stringOf(TerminateStrategyConfiguration termination) {
        String result = termination.toString();
        result = result.toLowerCase();
        result = result.replace(" ", "-");
        return result;
    }

    private void validate() {
        Set<Class<? extends WorkloadClockConfiguration>> classes = Sets.newHashSet();
        for (WorkloadClockConfiguration clock : clocks) {
            classes.add(clock.getClass());
            if (classes.size() > 1) {
                throw new ConfigurationException("Different clock types specified");
            }
        }
    }

    public void setPrototypes(List<WorkloadTask> prototypes) {
        this.prototypes = prototypes;
    }

    public void setClocks(List<WorkloadClockConfiguration> clocks) {
        this.clocks = clocks;
    }

    public void setTerminations(List<TerminateStrategyConfiguration> terminations) {
        this.terminations = terminations;
    }

    public void setAttendantMonitoring(MonitoringTask attendantMonitoring) {
        this.attendantMonitoring = attendantMonitoring;
    }
}
