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

package com.griddynamics.jagger.kernel.agent.worker;

import com.google.common.collect.Sets;
import com.griddynamics.jagger.agent.model.GetSystemInfo;
import com.griddynamics.jagger.agent.model.ManageAgent;
import com.griddynamics.jagger.agent.model.SystemInfo;
import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.CommandExecutor;
import com.griddynamics.jagger.coordinator.ConfigurableWorker;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.Qualifier;
import com.griddynamics.jagger.coordinator.VoidResult;
import com.griddynamics.jagger.kernel.agent.KernelAgent;
import com.griddynamics.jagger.kernel.agent.KernelAgentLauncher;
import com.griddynamics.jagger.kernel.agent.model.MonitoringInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KernelAgentWorker extends ConfigurableWorker {
    private static final Logger log = LoggerFactory.getLogger(KernelAgentWorker.class);

    private MonitoringInfoService monitoringInfoService;
    private final KernelAgent agent;
    private KernelAgentLauncher kernelAgentLauncher;

    public KernelAgentWorker(KernelAgent agent) {
        this.agent = agent;
    }

    @Required
    public void setKernelAgentLauncher(KernelAgentLauncher kernelAgentLauncher) {
        this.kernelAgentLauncher = kernelAgentLauncher;
    }

    public Set<Qualifier<Command<Serializable>>> getQualifiers() {
        Set<Qualifier<Command<Serializable>>> qualifiers = Sets.newHashSet();
        for (CommandExecutor commandExecutor : getExecutors()) {
            qualifiers.add(commandExecutor.getQualifier());
        }
        return qualifiers;
    }

    @Override
    public void configure() {
        onCommandReceived(GetSystemInfo.class).execute(new CommandExecutor<GetSystemInfo, ArrayList<SystemInfo>>() {
            @Override
            public Qualifier<GetSystemInfo> getQualifier() {
                return Qualifier.of(GetSystemInfo.class);
            }

            @Override
            public ArrayList<SystemInfo> execute(GetSystemInfo command, NodeContext nodeContext) {
                long startTime = System.currentTimeMillis();
                log.debug("start GetSystemInfo on agent {}", nodeContext.getId());
                ArrayList<SystemInfo> systemInfo = getSystemInfo();
                log.debug("finish GetSystemInfo on agent {} time {} ms", nodeContext.getId(), System.currentTimeMillis() - startTime);
                return systemInfo;
            }
        });
        onCommandReceived(ManageAgent.class).execute(
                new CommandExecutor<ManageAgent, VoidResult>() {
                    @Override
                    public Qualifier<ManageAgent> getQualifier() {
                        return Qualifier.of(ManageAgent.class);
                    }

                    @Override
                    public VoidResult execute(final ManageAgent command, NodeContext nodeContext) {
                        long startTime = System.currentTimeMillis();
                        log.debug("start ManageAgent on agent {} : action {} ", nodeContext.getId(), command.toString());

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    log.debug("Try to manage agent {}", agent.getNodeContext().getId().getIdentifier());
                                    synchronized (agent) {
                                        if (agent.isUnderManagement()) {
                                            log.warn("Agent {} is under management already", agent.getNodeContext().getId().getIdentifier());
                                            return;
                                        } else
                                            agent.markAsUnderManagement();
                                    }
                                    Long waitBefore = Long.parseLong(ManageAgent.extractParameter(command.getParams(),
                                            ManageAgent.ActionProp.WAIT_BEFORE).toString());
                                    log.info("Waiting before action {} millis", waitBefore);
                                    kernelAgentLauncher.getAgentLatch().await(waitBefore, TimeUnit.MILLISECONDS);
                                    kernelAgentLauncher.setAlive(!Boolean.parseBoolean(ManageAgent.extractParameter(command.getParams(),
                                            ManageAgent.ActionProp.HALT).toString()));
                                    agent.unmarkAsUnderManagement();
                                    kernelAgentLauncher.getAgentLatch().countDown();
                                    log.info("Free latch for agent {}", agent.getNodeContext().getId().getIdentifier());
                                } catch (InterruptedException e) {
                                    log.error("InterruptedException", e);// nothing to do
                                }
                            }
                        }.start();
                        log.info("finish ManageAgent on agent {} time {} ms", nodeContext.getId(),
                                System.currentTimeMillis() - startTime);
                        return VoidResult.emptyInstance();
                    }
                }

        );
    }

    private ArrayList<SystemInfo> getSystemInfo() {
        SystemInfo systemInfo = this.monitoringInfoService.getSystemInfo();
        systemInfo.setNodeId(this.agent.getNodeContext().getId());
        return new ArrayList<SystemInfo>(Collections.singletonList(systemInfo));
    }

    public void setMonitoringInfoService(MonitoringInfoService monitoringInfoService) {
        this.monitoringInfoService = monitoringInfoService;
    }
}
