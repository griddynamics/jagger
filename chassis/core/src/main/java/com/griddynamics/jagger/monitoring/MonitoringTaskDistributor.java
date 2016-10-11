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
package com.griddynamics.jagger.monitoring;

import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.coordinator.Qualifier;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.master.AbstractDistributor;
import com.griddynamics.jagger.master.TaskExecutionStatusProvider;
import com.griddynamics.jagger.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class MonitoringTaskDistributor extends AbstractDistributor<MonitoringTask> {
    private static Logger log = LoggerFactory.getLogger(MonitoringTaskDistributor.class);
    private long ttl;

    private TaskExecutionStatusProvider taskExecutionStatusProvider;

    @Override
    protected Set<Qualifier<?>> getQualifiers() {
        Set<Qualifier<?>> qualifiers = Sets.newHashSet();
        qualifiers.add(Qualifier.of(StartMonitoring.class));
        qualifiers.add(Qualifier.of(StopMonitoring.class));
        qualifiers.add(Qualifier.of(PollMonitoringStatus.class));
        return qualifiers;
    }

    @Override
    protected Service performDistribution(final ExecutorService executor, final String sessionId, final String taskId, final MonitoringTask task,
                                          final Map<NodeId, RemoteExecutor> remotes, final Multimap<NodeType, NodeId> availableNodes,
                                          final Coordinator coordinator, final NodeContext nodeContext) {

        return new AbstractExecutionThreadService() {
            @Override
            protected void run() throws Exception {
    
                MonitoringController monitoringController = null;
                try {
                    taskExecutionStatusProvider.setStatus(taskId, TaskData.ExecutionStatus.IN_PROGRESS);
                    MonitoringTerminationStrategy terminationStrategy = task.getTerminationStrategy().get();

                    monitoringController =
                            new MonitoringController(sessionId, task.getParentTaskId(), availableNodes, coordinator, remotes.keySet(), ttl);
                    monitoringController.startMonitoring();

                    while (true) {
                        if (!isRunning()) {
                            log.info("Going to terminate work {}. Requested from outside", task.getTaskName());
                            break;
                        }

                        Map<NodeId, MonitoringStatus> status = monitoringController.getStatus();

                        if (terminationStrategy.isTerminationRequired(status)) {
                            log.info("Going to terminate work {}. According to termination strategy",task.getTaskName());
                            break;
                        }

                        // todo mairbek: configure
                        TimeUtils.sleepMillis(500);
                    }

                    taskExecutionStatusProvider.setStatus(taskId, TaskData.ExecutionStatus.SUCCEEDED);
                } catch (Exception e) {
                    taskExecutionStatusProvider.setStatus(taskId, TaskData.ExecutionStatus.FAILED);
                    log.error("Monitoring task error: ", e);
                } finally {
                    if (monitoringController != null) {
                        log.debug("Going to stop monitoring");
                        monitoringController.stopMonitoring();
                        log.debug("Monitoring stopped");
                    }
                }
            }

            @Override
            public String toString() {
                return MonitoringTask.class.getName() + " distributor";
            }

        };
    }

    public void setTaskExecutionStatusProvider(TaskExecutionStatusProvider taskExecutionStatusProvider) {
        this.taskExecutionStatusProvider = taskExecutionStatusProvider;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getTtl() {
        return ttl;
    }
}
