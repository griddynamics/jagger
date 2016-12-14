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

package com.griddynamics.jagger.agent.worker;

import com.google.common.collect.Sets;
import com.griddynamics.jagger.agent.Agent;
import com.griddynamics.jagger.agent.AgentStarter;
import com.griddynamics.jagger.agent.Profiler;
import com.griddynamics.jagger.util.GeneralInfoCollector;
import com.griddynamics.jagger.agent.model.*;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.diagnostics.thread.sampling.ProfileDTO;
import com.griddynamics.jagger.diagnostics.thread.sampling.RuntimeGraph;
import com.griddynamics.jagger.util.GeneralNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AgentWorker extends ConfigurableWorker {
    private static final Logger log = LoggerFactory.getLogger(AgentWorker.class);

    private MonitoringInfoService monitoringInfoService;
    private GeneralInfoCollector generalInfoCollector;
    private Profiler profiler;
    private final Agent agent;
    private Boolean profilerEnabled;

    private ArrayList<JmxMetric> jmxMetricList = null;

    public AgentWorker(Agent agent) {
        this.agent = agent;
    }

    public Profiler getProfiler() {
        return profiler;
    }

    public Boolean getProfilerEnabled() {
        return profilerEnabled;
    }

    public void setProfilerEnabled(Boolean profilerEnabled) {
        this.profilerEnabled = profilerEnabled;
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
        onCommandReceived(GetCollectedProfileFromSuT.class).execute(
                new CommandExecutor<GetCollectedProfileFromSuT, ProfileDTO>() {
                    @Override
                    public Qualifier<GetCollectedProfileFromSuT> getQualifier() {
                        return Qualifier.of(GetCollectedProfileFromSuT.class);
                    }

                    @Override
                    public ProfileDTO execute(GetCollectedProfileFromSuT command, NodeContext nodeContext) {
                        String hostAddress;
                        long startTime = System.currentTimeMillis();
                        try {
                            hostAddress = InetAddress.getLocalHost().getHostAddress();
                        } catch (UnknownHostException e) {
                            hostAddress = "UNKNOWN";
                        }
                        ProfileDTO profileDTO = new ProfileDTO(hostAddress, Collections.EMPTY_MAP);
                        if (profilerEnabled) {
                            log.debug("start GetCollectedProfileFromSuT on agent {}", nodeContext.getId());
                            Map<String, RuntimeGraph> runtimeGraphs = profiler.getSamplingProfiler().getRuntimeGraph();
                            profileDTO = new ProfileDTO(hostAddress, runtimeGraphs);
                            log.debug("finish GetCollectedProfileFromSuT on agent {} time {} ms", nodeContext.getId(), System.currentTimeMillis() - startTime);
                        }
                        return profileDTO;
                    }
                });
        onCommandReceived(ManageCollectionProfileFromSuT.class).execute(
                new CommandExecutor<ManageCollectionProfileFromSuT, VoidResult>() {
                    @Override
                    public Qualifier<ManageCollectionProfileFromSuT> getQualifier() {
                        return Qualifier.of(ManageCollectionProfileFromSuT.class);
                    }

                    @Override
                    public VoidResult execute(final ManageCollectionProfileFromSuT command, NodeContext nodeContext) {
                        VoidResult voidResult = new VoidResult();
                        if (profilerEnabled) {
                            long startTime = System.currentTimeMillis();
                            log.debug("start ManageCollectionProfileFromSuT on agent {}", nodeContext.getId());
                            try {
                                profiler.manageRuntimeGraphsCollection(command.getAction(),
                                        Collections.<String, Object>singletonMap(Profiler.POLL_INTERVAL, command.getProfilerPollingInterval()));
                            } catch (Exception e) {
                                voidResult.setException(e);
                            }
                            log.debug("finish ManageCollectionProfileFromSuT on agent {} time {} ms", nodeContext.getId(),
                                    System.currentTimeMillis() - startTime);
                        }
                        return voidResult;
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

                                    jmxMetricList = (ArrayList) ManageAgent.extractParameter(command.getParams(),
                                            ManageAgent.ActionProp.SET_JMX_METRICS);
                                    AgentContext agentContext = new AgentContext();
                                    agentContext.setProperty(AgentContext.AgentContextProperty.JMX_METRICS, jmxMetricList);
                                    monitoringInfoService.setContext(agentContext);

                                    log.info("Waiting before action {} millis", waitBefore);
                                    AgentStarter.agentLatch.await(waitBefore, TimeUnit.MILLISECONDS);
                                    AgentStarter.alive.set(!Boolean.parseBoolean(ManageAgent.extractParameter(command.getParams(),
                                            ManageAgent.ActionProp.HALT).toString()));
                                    agent.unmarkAsUnderManagement();
                                    AgentStarter.agentLatch.countDown();
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
        onCommandReceived(GetGeneralNodeInfo.class).execute(
                new CommandExecutor<GetGeneralNodeInfo, GeneralNodeInfo>() {
            @Override
            public Qualifier<GetGeneralNodeInfo> getQualifier() {
                return Qualifier.of(GetGeneralNodeInfo.class);
            }

            @Override
            public GeneralNodeInfo execute(GetGeneralNodeInfo command, NodeContext nodeContext) {
                long startTime = System.currentTimeMillis();
                log.debug("start GetGeneralNodeInfo on agent {}", nodeContext.getId());
                GeneralNodeInfo generalNodeInfo = generalInfoCollector.getGeneralNodeInfo();
                log.debug("finish GetGeneralNodeInfo on agent {} time {} ms", nodeContext.getId(), System.currentTimeMillis() - startTime);
                return generalNodeInfo;
            }
        });

    }

    private ArrayList<SystemInfo> getSystemInfo() {
        SystemInfo systemInfo = this.monitoringInfoService.getSystemInfo();
        systemInfo.setNodeId(this.agent.getNodeContext().getId());
        return new ArrayList<SystemInfo>(Collections.singletonList(systemInfo));
    }

    public void setMonitoringInfoService(MonitoringInfoService monitoringInfoService) {
        this.monitoringInfoService = monitoringInfoService;
    }

    @Required
    public void setProfiler(Profiler profiler) {
        this.profiler = profiler;
    }

    public void setGeneralInfoCollector(GeneralInfoCollector generalInfoCollector) {
        this.generalInfoCollector = generalInfoCollector;
    }
}
