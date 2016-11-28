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

import com.google.common.base.Throwables;
import com.griddynamics.jagger.agent.model.GetCollectedProfileFromSuT;
import com.griddynamics.jagger.agent.model.GetSystemInfo;
import com.griddynamics.jagger.agent.model.ManageCollectionProfileFromSuT;
import com.griddynamics.jagger.agent.model.SystemInfo;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.diagnostics.thread.sampling.ProfileDTO;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.storage.fs.logging.LogProcessor;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.SerializationUtils;
import com.griddynamics.jagger.util.TimeUtils;
import com.griddynamics.jagger.util.Timeout;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static com.griddynamics.jagger.agent.model.ManageCollectionProfileFromSuT.ManageHotSpotMethodsFromSuT;

/**
 * Perform monitoring of one agent.
 *
 * @author Mairbek Khadikov
 */
public class MonitorProcess extends LogProcessor implements NodeProcess<MonitoringStatus> {
    public static final String PROFILER_MARKER = "PROFILER";

    private static final Logger log = LoggerFactory.getLogger(MonitorProcess.class);

    private final String sessionId;
    private final NodeId agentId;
    private final NodeContext nodeContext;
    private final Coordinator coordinator;
    private final ExecutorService executor;
    private final long pollingInterval;
    private final long profilerPollingInterval;
    private final MonitoringProcessor monitoringProcessor;
    private final String taskId;
    private volatile boolean alive;
    private LogWriter logWriter;
    private CountDownLatch latch;
    private final Timeout ttl;

    /*package*/ MonitorProcess(String sessionId, NodeId agentId, NodeContext nodeContext, Coordinator coordinator,
                               ExecutorService executor, long pollingInterval, long profilerPollingInterval,
                               MonitoringProcessor monitoringProcessor, String taskId, LogWriter logWriter,
                               SessionFactory sessionFactory, Timeout ttl) {
        this.sessionId = sessionId;
        this.agentId = agentId;
        this.nodeContext = nodeContext;
        this.coordinator = coordinator;
        this.executor = executor;
        this.pollingInterval = pollingInterval;
        this.monitoringProcessor = monitoringProcessor;
        this.taskId = taskId;
        this.logWriter = logWriter;
        this.profilerPollingInterval = profilerPollingInterval;
        this.setSessionFactory(sessionFactory);
        this.ttl = ttl;
    }

    @Override
    public void start() throws TechnicalException {
        log.info("Kernel {} has started monitoring on agent {} by task id {}", new Object[]{nodeContext.getId(), agentId, taskId});
        alive = true;

        final RemoteExecutor remote = coordinator.getExecutor(agentId);

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    VoidResult voidResult = remote.runSyncWithTimeout(new ManageCollectionProfileFromSuT(sessionId,
                            ManageHotSpotMethodsFromSuT.START_POLLING, profilerPollingInterval), Coordination.<ManageCollectionProfileFromSuT>doNothing(), ttl);
                    while (alive) {
                        long startTime = System.currentTimeMillis();
                        log.debug("try getting GetSystemInfo on kernel {} from {}", nodeContext.getId(), agentId);
                        try {
                            ArrayList<SystemInfo> info = remote.runSyncWithTimeout(new GetSystemInfo(sessionId), Coordination.<GetSystemInfo>doNothing(), ttl);
                            if (voidResult.hasException())
                                log.error("Remote exception raised during staring profiling from SuT", voidResult.getException());
                            log.debug("GetSystemInfo got on kernel {} from {} time {} ms",
                                    new Object[]{nodeContext.getId(), agentId, System.currentTimeMillis() - startTime});
                            for (SystemInfo systemInfo : info) {
                                monitoringProcessor.process(sessionId, taskId, agentId, nodeContext, systemInfo);
                            }
                            log.debug("monitoring logged to file storage on kernel {}", nodeContext.getId());
                        } catch (Throwable e) {
                            log.error("Ignore GetSystemInfo from agent " + agentId + " due to error", e);
                        }
                        TimeUtils.sleepMillis(pollingInterval);
                    }
                    log.debug("try to flush monitoring on kernel {}", nodeContext.getId());
                    logWriter.flush();
                    log.debug("monitoring flushed on kernel {}", nodeContext.getId());
                    if (!voidResult.hasException()) {
                        log.debug("try to manage monitoring on agent {} from kernel {}", agentId, nodeContext.getId());

                        try {
                            voidResult = remote.runSyncWithTimeout(new ManageCollectionProfileFromSuT(sessionId, ManageHotSpotMethodsFromSuT.STOP_POLLING,
                                    profilerPollingInterval), Coordination.<ManageCollectionProfileFromSuT>doNothing(), ttl);
                            log.debug("manage monitoring has done on agent {} from kernel {}", agentId, nodeContext.getId());
                            if (voidResult.hasException())
                                log.error("Remote exception raised during stopping profiling from SuT", voidResult.getException());
                            log.debug("try to get collected profiler from agent {} from kernel {}", agentId, nodeContext.getId());

                            try {
                                final ProfileDTO profileDTO =
                                        remote.runSyncWithTimeout(GetCollectedProfileFromSuT.create(sessionId), Coordination.<GetCollectedProfileFromSuT>doNothing(), ttl);
                                if (profileDTO.getRuntimeGraphs().isEmpty()) {
                                    log.info("Profiler of {} turned off. There is no profiler data for recording", agentId);
                                } else {
                                    log.debug("got collected profiler from agent {} from kernel {}", agentId, nodeContext.getId());
                                    logWriter.log(sessionId, taskId + "/" + PROFILER_MARKER, agentId.getIdentifier(), SerializationUtils.toString(profileDTO));
                                    log.debug("Profiler {} received from agent {} and has been written to FileStorage", profileDTO, agentId);
                                    logWriter.flush();
                                    log.debug("Flushing performed on kernel {}", nodeContext.getId());
                                }
                            } catch (Throwable e) {
                                log.error("Get collected profile failed for agent " + agentId + "\n" + Throwables.getStackTraceAsString(e));
                            }
                        } catch (Throwable e) {
                            log.error("Stop polling failed for agent " + agentId + "\n" + Throwables.getStackTraceAsString(e));
                        }
                    } else {
                        log.warn("Collection profiling from SuT didn't start");
                    }
                } catch (Throwable e) {
                    log.error("Start polling failed for agent " + agentId + "\n" + Throwables.getStackTraceAsString(e));
                } finally {
                    log.debug("releasing a latch");
                    if (latch != null) {
                        log.debug("latch is available");
                        latch.countDown();
                    }
                    alive = false;
                    log.debug("latch released");

                }
            }
        };

        executor.execute(runnable);
    }

    @Override
    public MonitoringStatus getStatus() {
        return MonitoringStatus.PROGRESS;
    }

    @Override
    public void stop() {
        log.info("Stop of monitoring requested. agent {}", agentId);
        if (alive) {
            latch = new CountDownLatch(1);
            alive = false;
            try {
                latch.await();
            } catch (InterruptedException e) {
                log.warn("Interrupted {}", e);
            }
            log.info("Kernel {} has stopped monitoring on agent {}", nodeContext.getId(), agentId);
        } else {
            log.warn("Monitoring on agent {} is not running.  Skipping StopMonitoring", agentId);
        }
    }

    public Timeout getTtl() {
        return ttl;
    }
}
