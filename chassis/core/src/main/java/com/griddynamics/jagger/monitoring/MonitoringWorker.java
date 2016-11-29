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

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.Nothing;
import com.griddynamics.jagger.util.Timeout;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Coordinator based API for monitoring of agents.
 *
 * @author Mairbek Khadikov
 */
public class MonitoringWorker extends ConfigurableWorker {
    private static final Logger log = LoggerFactory.getLogger(MonitoringWorker.class);

    private ExecutorService executor;
    private Coordinator coordinator;
    private long pollingInterval;
    private long profilerPollingInterval;
    private MonitoringProcessor monitoringProcessor;

    private LogWriter logWriter;
    private Map<String, MonitorProcess> processes = Maps.newConcurrentMap();
    private SessionFactory sessionFactory;
    private Timeout ttl;

    @Override
    public void configure() {
        onCommandReceived(StartMonitoring.class).execute(new CommandExecutor<StartMonitoring, String>() {
            @Override
            public Qualifier<StartMonitoring> getQualifier() {
                return Qualifier.of(StartMonitoring.class);
            }

            @Override
            public String execute(StartMonitoring command, NodeContext nodeContext) {
                MonitorProcess process = new MonitorProcess(command.getSessionId(), command.getAgentNode(),
                        nodeContext, coordinator, executor, pollingInterval, profilerPollingInterval, monitoringProcessor, command.getTaskId(),
                        logWriter, sessionFactory, ttl);

                String processId = generateProcessId();
                processes.put(processId, process);
                process.start();
                return processId;
            }
        });

        onCommandReceived(PollMonitoringStatus.class).execute(new CommandExecutor<PollMonitoringStatus, MonitoringStatus>() {
            @Override
            public Qualifier<PollMonitoringStatus> getQualifier() {
                return Qualifier.of(PollMonitoringStatus.class);
            }

            @Override
            public MonitoringStatus execute(PollMonitoringStatus command, NodeContext nodeContext) {
                String processId = command.getProcessId();

                MonitorProcess process = processes.get(processId);

                return process.getStatus();
            }
        });

        onCommandReceived(StopMonitoring.class).execute(new CommandExecutor<StopMonitoring, Nothing>() {
            @Override
            public Qualifier<StopMonitoring> getQualifier() {
                return Qualifier.of(StopMonitoring.class);
            }

            @Override
            public Nothing execute(StopMonitoring command, NodeContext nodeContext) {
                log.debug("StopMonitoring command received on node {}", nodeContext.getId());
                String processId = command.getProcessId();
                MonitorProcess process = processes.get(processId);

                log.debug("Going to stop MonitorProcess with id {}", processId);
                process.stop();

                return Nothing.INSTANCE;
            }
        });
    }

    private static String generateProcessId() {
        return "process-" + System.nanoTime();
    }

    @Required
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Required
    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Required
    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    @Required
    public void setMonitoringProcessor(MonitoringProcessor monitoringProcessor) {
        this.monitoringProcessor = monitoringProcessor;
    }

    @Required
    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setProfilerPollingInterval(long profilerPollingInterval) {
        this.profilerPollingInterval = profilerPollingInterval;
    }

    public final void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setTtl(Timeout ttl) {
        this.ttl = ttl;
    }

    public Timeout getTtl() {
        return ttl;
    }
}
