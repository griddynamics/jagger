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

import com.griddynamics.jagger.agent.model.SystemInfo;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeProcess;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import org.hibernate.SessionFactory;

import java.util.concurrent.ExecutorService;

/**
 * Utility methods for monitoring.
 *
 * @author Mairbek Khadikov
 */
public class Monitoring {

    private Monitoring() {
    }

    public static NodeProcess<MonitoringStatus> createProcess(String sessionId, NodeId agentId, NodeContext nodeContext,
                                               Coordinator coordinator, ExecutorService executor, long pollingInterval,
                                               long profilerPollingInterval, MonitoringProcessor monitoringProcessor,
                                               String taskId, LogWriter logWriter, SessionFactory sessionFactory, long ttl) {
        switch (agentId.getType()) {
            case AGENT:
                return new AgentMonitorProcess(sessionId, agentId, nodeContext, coordinator, executor,
                        pollingInterval, monitoringProcessor, profilerPollingInterval,
                        taskId, logWriter, sessionFactory, ttl);
            case KERNEL_AGENT:
                return new KernelAgentMonitorProcess(sessionId, agentId, nodeContext, coordinator, executor,
                        pollingInterval, monitoringProcessor,
                        taskId, logWriter, sessionFactory, ttl);
            default:
                throw new IllegalArgumentException(agentId.getType() + " not allowed for monitoring.");

        }
    }

    public static MonitoringProcessor compose(Iterable<MonitoringProcessor> processes) {
        return new CompositeMonitoringProcessor(processes);
    }

    public static class CompositeMonitoringProcessor implements MonitoringProcessor {
        private final Iterable<MonitoringProcessor> processes;

        public CompositeMonitoringProcessor(Iterable<MonitoringProcessor> processes) {
            this.processes = processes;
        }

        @Override
        public void process(String sessionId, String taskId, NodeId agentId, SystemInfo systemInfo) {
            for (MonitoringProcessor processor : processes) {
                processor.process(sessionId, taskId, agentId, systemInfo);
            }
        }

    }
}

