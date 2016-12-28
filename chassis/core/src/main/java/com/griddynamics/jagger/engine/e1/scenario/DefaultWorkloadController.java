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

package com.griddynamics.jagger.engine.e1.scenario;

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.Coordination;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.engine.e1.process.AddUrlClassLoader;
import com.griddynamics.jagger.engine.e1.process.ChangeWorkloadConfiguration;
import com.griddynamics.jagger.engine.e1.process.PollWorkloadProcessStatus;
import com.griddynamics.jagger.engine.e1.process.RemoveUrlClassLoader;
import com.griddynamics.jagger.engine.e1.process.ScenarioContext;
import com.griddynamics.jagger.engine.e1.process.StartWorkloadProcess;
import com.griddynamics.jagger.engine.e1.process.StopWorkloadProcess;
import com.griddynamics.jagger.engine.e1.process.WorkloadStatus;
import com.griddynamics.jagger.util.TimeoutsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

public class DefaultWorkloadController implements WorkloadController {
    private static final Logger log = LoggerFactory.getLogger(DefaultWorkloadController.class);
    private final String sessionId;
    private final String taskId;
    private final Map<NodeId, RemoteExecutor> remotes;
    private final Long startTime;
    private final TimeoutsConfiguration timeoutsConfiguration;
    private final WorkloadTask task;
    private Progress progress;
    private Map<NodeId, String> processes;
    private Map<NodeId, Integer> threads;
    private Map<NodeId, Integer> delays;
    private Map<NodeId, Integer> poolSize;
    
    private String classesUrl;
    
    public void setClassesUrl(String classesUrl) {
        this.classesUrl = classesUrl;
    }
    
    public DefaultWorkloadController(final String sessionId,
                                     final String taskId,
                                     final WorkloadTask task,
                                     final Map<NodeId, RemoteExecutor> remotes,
                                     final TimeoutsConfiguration timeoutsConfiguration,
                                     final Long startTime) {
        this.sessionId = Preconditions.checkNotNull(sessionId);
        this.taskId = Preconditions.checkNotNull(taskId);
        this.task = Preconditions.checkNotNull(task);
        this.remotes = ImmutableMap.copyOf(remotes);
        this.timeoutsConfiguration = timeoutsConfiguration;
        this.startTime = startTime;
        
        progress = Progress.IDLE;
        processes = Maps.newHashMap();
        threads = Maps.newHashMap();
        delays = Maps.newHashMap();
    }

    @Override
    public Set<NodeId> getNodes() {
        return remotes.keySet();
    }

    @Override
    public WorkloadExecutionStatus getStatus() {
        Preconditions.checkState(progress == Progress.STARTED, "Workload should be started to get status");

        log.debug("Workload status requested");
        WorkloadExecutionStatusBuilder builder = new WorkloadExecutionStatusBuilder(task);

        for (Map.Entry<NodeId, RemoteExecutor> entry : remotes.entrySet()) {
            Long pollTime = System.currentTimeMillis();
            Long durationTime = System.currentTimeMillis()-startTime;
            NodeId id = entry.getKey();
            RemoteExecutor remote = entry.getValue();

            WorkloadStatus status;
            String processId = processes.get(id);
            if (processId != null) {
                status = remote.runSyncWithTimeout(PollWorkloadProcessStatus.create(sessionId, processId), Coordination.<Command<WorkloadStatus>>doNothing(), timeoutsConfiguration.getWorkloadPollingTimeout());
            } else {
                status = new WorkloadStatus(0,0,0);
            }

            Integer threadsOnNode = threads.get(id);
            Integer delay = delays.get(id);

            log.debug("{} Polled status: node {}, threads on node {}, samples started {}, samples finished {} with delay {}",
                      pollTime,
                      id,
                      threadsOnNode,
                      status.getStartedSamples(),
                      status.getFinishedSamples(),
                      delay);

            builder.addNodeInfo(id, status.getCurrentThreadNumber(), status.getStartedSamples(), status.getFinishedSamples(), delay, pollTime, durationTime);
        }

        return builder.build();
    }

    @Override
    public void startWorkload(Map<NodeId, Integer> poolSize) {
        Preconditions.checkState(progress == Progress.IDLE, "Workload should be idle to get started");

        log.debug("Workload start requested");

        for (NodeId nodeId : remotes.keySet()) {
            threads.put(nodeId, 0);
            delays.put(nodeId, 0);
            
            if (classesUrl != null) {
                RemoteExecutor executor = remotes.get(nodeId);
                AddUrlClassLoader addUrlClassLoaderCommand = AddUrlClassLoader.create(sessionId, classesUrl);
                log.info("Sending command to add a class loader with classes url {} to node {}", classesUrl, nodeId);
                executor.runSyncWithTimeout(addUrlClassLoaderCommand,
                                            Coordination.<Command>doNothing(),
                                            timeoutsConfiguration.getWorkloadStartTimeout());
                log.info("Class loader with classes url {} has been added to node {}", classesUrl, nodeId);
            }
        }

        this.poolSize = poolSize;
        progress = Progress.STARTED;
    }

    @Override
    public void adjustConfiguration(NodeId id, WorkloadConfiguration newConfiguration) {
        Preconditions.checkState(progress == Progress.STARTED, "Workload should be started to get adjust task number");

        log.debug("Adjusting task number with threads {} on node {} is requested", threads, id);

        boolean workloadStarted = processes.containsKey(id);

        if (workloadStarted) {
            log.debug("Process is started. Going to change workload configuration");
            changeWorkload(id, newConfiguration);
        } else {
            log.debug("Process is not started. Going to start workload");
            startWorkload(id, newConfiguration);
        }

    }

    @Override
    public void stopWorkload() {
        Preconditions.checkState(progress == Progress.STARTED, "Workload should be started to stop processes");

        log.debug("Workload stop requested");

        for (Map.Entry<NodeId, String> entry : processes.entrySet()) {
            NodeId id = entry.getKey();
            String processId = entry.getValue();

            RemoteExecutor executor = remotes.get(id);
            StopWorkloadProcess stop = StopWorkloadProcess.create(sessionId, processId);

            log.debug("Going to stop process {} on node {}", processId, id);
            executor.runSyncWithTimeout(stop, Coordination.<Command>doNothing(), timeoutsConfiguration.getWorkloadStopTimeout());
            log.debug("Process {} is stopped on node {}", processId, id);
    
    
            if (classesUrl != null) {
                RemoveUrlClassLoader removeUrlClassLoaderCommand = RemoveUrlClassLoader.create(sessionId);
                log.info("Sending command to remove a custom class loader to node {}", id);
                executor.runSyncWithTimeout(removeUrlClassLoaderCommand,
                                            Coordination.<Command>doNothing(),
                                            timeoutsConfiguration.getWorkloadStopTimeout());
                log.info("A custom class loader has been removed from node {}", id);
            }
        }

        log.debug("Workload stopped");
        progress = Progress.STOPPED;
    }

    private void changeWorkload(NodeId node, WorkloadConfiguration newConfiguration) {
        String processId = processes.get(node);
        RemoteExecutor remote = remotes.get(node);
        remote.runSyncWithTimeout(ChangeWorkloadConfiguration.create(sessionId, processId, newConfiguration), Coordination.<Command>doNothing(), timeoutsConfiguration.getWorkloadPollingTimeout());
        threads.put(node, newConfiguration.getThreads());
        delays.put(node, newConfiguration.getDelay());
    }

    private void startWorkload(NodeId node, WorkloadConfiguration configuration) {
        ScenarioContext scenarioContext = new ScenarioContext(taskId, task.getName(), task.getVersion(), configuration);
        Integer nodePoolSize = poolSize.get(node);
        if (nodePoolSize == null) {
            nodePoolSize = 1;
        }
        StartWorkloadProcess start = StartWorkloadProcess.create(sessionId, scenarioContext, nodePoolSize);
        start.setScenarioFactory(task.getScenarioFactory());
        start.setCollectors(task.getCollectors());
        start.setValidators(task.getValidators());
        start.setListeners(task.getListeners());

        log.debug("Going to start process {} on node {}", start, node);

        RemoteExecutor remote = remotes.get(node);
        String processId = remote.runSyncWithTimeout(start, Coordination.<StartWorkloadProcess>doNothing(), timeoutsConfiguration.getWorkloadStartTimeout());

        log.debug("Process with id {} is started on node {}", processId, node);
        processes.put(node, processId);
        threads.put(node, configuration.getThreads());
        delays.put(node, configuration.getDelay());
    }

    private static enum Progress {
        IDLE, STARTED, STOPPED
    }
}
