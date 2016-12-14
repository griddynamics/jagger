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
package com.griddynamics.jagger.monitoring;

import com.google.common.collect.*;
import com.griddynamics.jagger.coordinator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class MonitoringController {
    private Logger log = LoggerFactory.getLogger(MonitoringController.class);

    private final String sessionId;
    private final String taskId;
    private final Multimap<NodeType, NodeId> availableNodes;

    private final Coordinator coordinator;
    private Multimap<NodeId, String> activeProcesses = HashMultimap.<NodeId, String>create();
    private Map<String, NodeId> agentMapping = Maps.newHashMap();
    private final Collection<NodeId> capableNodes;
    private final long ttl;

    public MonitoringController(String sessionId, String taskId, Multimap<NodeType, NodeId> availableNodes,
                                Coordinator coordinator, Collection<NodeId> capableNodes, long ttl) {
        this.sessionId = sessionId;
        this.taskId = taskId;
        this.availableNodes = availableNodes;
        this.coordinator = coordinator;
        this.capableNodes = capableNodes;
        this.ttl = ttl;
    }

    public void startMonitoring() {
        log.debug("Start of monitoring requested. Task id {}", taskId);

        // todo use nodes on start
        Collection<NodeId> agents = availableNodes.get(NodeType.AGENT);
        log.debug("Available agents {}", agents);


        Iterator<NodeId> iterator = capableNodes.iterator();
        for (NodeId agent : agents) {
            if (!iterator.hasNext()) {
                iterator = capableNodes.iterator();
            }

            NodeId kernel = iterator.next();
            RemoteExecutor remote = coordinator.getExecutor(kernel);

            log.info("Agent {} will be monitored by kernel {}", agent, kernel);
            log.debug("Start monitoring command is sending");
            String processId = remote.runSyncWithTimeout(StartMonitoring.create(sessionId, agent, taskId),
                    Coordination.<StartMonitoring>doNothing(), ttl * 2);
            log.debug("Start monitoring command is sent. Process with id {} started", processId);

            activeProcesses.put(kernel, processId);
            agentMapping.put(processId, agent);
        }

    }

    public Map<NodeId, MonitoringStatus> getStatus() {
        Map<NodeId, MonitoringStatus> result = Maps.newHashMap();
        for (NodeId nodeId : capableNodes) {
            RemoteExecutor remote = coordinator.getExecutor(nodeId);
            Collection<String> processes = activeProcesses.get(nodeId);
            for (String processId : processes) {
                remote.runSyncWithTimeout(PollMonitoringStatus.create(sessionId, processId),
                        Coordination.<Command>doNothing(), ttl * 2);
            }
        }
        return result;
    }

    public void stopMonitoring() {
        log.info("Stop monitoring requested");
        ExecutorService threadsStopper = Executors.newCachedThreadPool();
        Collection<Callable<Void>> stopList = Sets.newLinkedHashSet();
        for (final NodeId kernel : activeProcesses.keySet()) {
            Collection<String> processes = activeProcesses.get(kernel);
            for (final String processId : processes) {
                stopList.add(
                        new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                try {
                                    RemoteExecutor remote = coordinator.getExecutor(kernel);

                                    log.info("Going to send stop monitoring command to kernel {}", kernel);
                                    remote.runSyncWithTimeout(StopMonitoring.create(sessionId, processId),
                                            Coordination.doNothing(), 2*ttl);
                                    log.info("Command stop monitoring successfully sent");

                                    NodeId agent = agentMapping.get(processId);
                                    log.info("Agent {} monitoring is stopped on kernel {}", agent, kernel);
                                } catch (Throwable e) {
                                    log.error("Stop monitoring failed for kernel {}, process {}", kernel.getIdentifier(),
                                            processId);
                                    return null;
                                }
                                return null;
                            }
                        });
            }
        }
        try {
            threadsStopper.invokeAll(stopList);
            threadsStopper.shutdown();
        } catch (InterruptedException e) {
            log.error("Stop monitoring processes pool failed");
        }
        for (NodeId kernel : Sets.newHashSet(activeProcesses.keySet())) {
            Collection<String> processes = activeProcesses.get(kernel);
            for (String processId : processes) {
                agentMapping.remove(processId);
            }
            activeProcesses.removeAll(kernel);
        }
        log.info("All monitoring processes were stopped successful");
    }
}
