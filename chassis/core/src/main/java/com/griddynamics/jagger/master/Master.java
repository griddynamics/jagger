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

package com.griddynamics.jagger.master;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Service;
import com.griddynamics.jagger.agent.model.ManageAgent;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.engine.e1.process.Services;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.SessionExecutionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.reporting.ReportingServiceProvider;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.util.Futures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.*;

/**
 * Main thread of Master
 *
 * @author Alexey Kiselyov
 */
public class Master implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Master.class);

    @Autowired
    private ApplicationContext context;

    private Configuration configuration;
    private Coordinator coordinator;
    private DistributorRegistry distributorRegistry;
    private SessionIdProvider sessionIdProvider;
    private KeyValueStorage keyValueStorage;
    private ReportingServiceProvider reportingServiceProvider;
    private long reconnectPeriod;
    private Conditions conditions;
    private ExecutorService executor;
    private TaskIdProvider taskIdProvider;
    private Map<ManageAgent.ActionProp, Serializable> agentManagementProps;
    private MasterTimeoutConfiguration timeoutConfiguration;
    private boolean terminateConfiguration = false;
    private final Object terminateConfigurationLock = new Object();
    private CountDownLatch terminateConfigurationLatch = null;
    private final WeakHashMap<Service, Object> distributes = new WeakHashMap<Service, Object>();

    @Required
    public void setReconnectPeriod(long reconnectPeriod) {
        this.reconnectPeriod = reconnectPeriod;
    }

    @Required
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Required
    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Required
    public void setDistributorRegistry(DistributorRegistry distributorRegistry) {
        this.distributorRegistry = distributorRegistry;
    }

    @Required
    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    @Override
    public void run() {
        validateConfiguration();

        if (!keyValueStorage.isAvailable()) {
            keyValueStorage.initialize();
        }

        String sessionId = sessionIdProvider.getSessionId();
        String sessionComment = sessionIdProvider.getSessionComment();

        Multimap<NodeType, NodeId> allNodes = HashMultimap.create();
        allNodes.putAll(NodeType.MASTER, coordinator.getAvailableNodes(NodeType.MASTER));

        Map<NodeType, CountDownLatch> countDownLatchMap = Maps.newHashMap();
        CountDownLatch agentCountDownLatch = new CountDownLatch(
                conditions.isMonitoringEnable() ?
                        conditions.getMinAgentsCount() :
                        0
        );
        CountDownLatch kernelCountDownLatch = new CountDownLatch(conditions.getMinKernelsCount());
        countDownLatchMap.put(NodeType.AGENT, agentCountDownLatch);
        countDownLatchMap.put(NodeType.KERNEL, kernelCountDownLatch);

        new StartWorkConditions(allNodes, countDownLatchMap);

        try {
            agentCountDownLatch.await(timeoutConfiguration.getNodeAwaitTime(), TimeUnit.MILLISECONDS);
            kernelCountDownLatch.await(timeoutConfiguration.getNodeAwaitTime(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("CountDownLatch await interrupted", e);
        }

        for (SessionExecutionListener listener : configuration.getSessionExecutionListeners()) {
            listener.onSessionStarted(sessionId, allNodes);
        }

        Thread shutdownHook = new Thread(String.format("Shutdown hook for %s", getClass().toString())) {
            @Override
            public void run() {
                terminateConfiguration();
            }
        };
        terminateConfigurationLatch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        try {
            log.info("Configuration launched!!");

            runConfiguration(allNodes);

            log.info("Configuration work finished!!");

            for (SessionExecutionListener listener : configuration.getSessionExecutionListeners()) {
                listener.onSessionExecuted(sessionId, sessionComment);
            }

            log.info("Going to generate report");
            reportingServiceProvider.getReportingService(context).renderReport(true);
            log.info("Report generated");

            log.info("Going to stop all agents");
            finishAgentManagement(sessionId);
            log.info("Agents stopped");
        } finally {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (Exception e) {
            }
            terminateConfigurationLatch.countDown();
        }
    }

    private void finishAgentManagement(String sessionId) {
        for (NodeId agent : coordinator.getAvailableNodes(NodeType.AGENT)) {
            // async run
            coordinator.getExecutor(agent).run(new ManageAgent(sessionId, agentManagementProps),
                    Coordination.<ManageAgent>doNothing());
        }
    }

    private void validateConfiguration() {
        // TODO Auto-generated method stub
    }

    private void runConfiguration(Multimap<NodeType, NodeId> allNodes) {
        try {
            log.info("Execution started");
            for (Task task : configuration.getTasks()) {
                executeTask(task, allNodes);
                synchronized (terminateConfigurationLock) {
                    if (terminateConfiguration) {
                        throw new TerminateException("Execution terminated");
                    }
                }
            }
            log.info("Execution done");
        } catch (Exception e) {
            log.error("Exception while running configuration: {}",e);
        }
    }

    public void terminateConfiguration() {
        synchronized (terminateConfigurationLock) {
            terminateConfiguration = true;
        }

        Service[] distributes = this.distributes.keySet().toArray(new Service[0]);
        for (Service distribute : distributes) {
            distribute.stopAndWait();
        }

        try {
            terminateConfigurationLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeTask(Task task, Multimap<NodeType, NodeId> allNodes) throws TerminateException {
        log.debug("Distributing task {}", task);

        @SuppressWarnings("unchecked")
        TaskDistributor<Task> taskDistributor = (TaskDistributor<Task>) distributorRegistry.getTaskDistributor(task.getClass());

        Map<NodeId, RemoteExecutor> remotes = Maps.newHashMap();

        log.debug("Distributed task will be executed on {} nodes", remotes.size());

        String taskId = taskIdProvider.getTaskId();

        Service distribute = taskDistributor.distribute(executor, sessionIdProvider.getSessionId(), taskId, allNodes, coordinator, task, distributionListener());

        Future<Service.State> start;
        synchronized (terminateConfigurationLock) {
            if (!terminateConfiguration) {
                distributes.put(distribute, null);
                start = distribute.start();
            } else {
                throw new TerminateException("Execution terminated");
            }
        }

        Futures.get(start, timeoutConfiguration.getDistributionStartTime());

        Services.awaitTermination(distribute, timeoutConfiguration.getTaskExecutionTime());

        Future<Service.State> stop = distribute.stop();

        Futures.get(stop, timeoutConfiguration.getDistributionStopTime());
    }

    private DistributionListener distributionListener() {
        return CompositeDistributionListener.of(configuration.getDistributionListeners());
    }

    public void setSessionIdProvider(SessionIdProvider sessionIdProvider) {
        this.sessionIdProvider = sessionIdProvider;
    }

    public void setKeyValueStorage(KeyValueStorage keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
    }

    public void setReportingServiceProvider(ReportingServiceProvider reportingServiceProvider) {
        this.reportingServiceProvider = reportingServiceProvider;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void setTaskIdProvider(TaskIdProvider taskIdProvider) {
        this.taskIdProvider = taskIdProvider;
    }

    public void setAgentManagementProps(Map<ManageAgent.ActionProp, Serializable> agentManagementProps) {
        this.agentManagementProps = agentManagementProps;
    }

    public Map<ManageAgent.ActionProp, Serializable> getAgentManagementProps() {
        return this.agentManagementProps;
    }

    @Required
    public void setTimeoutConfiguration(MasterTimeoutConfiguration timeoutConfiguration) {
        this.timeoutConfiguration = timeoutConfiguration;
    }

    private class StartWorkConditions implements Runnable {

        private Multimap<NodeType, NodeId> allNodes;
        private Map<NodeType, CountDownLatch> nodesCountDowns;

        private StartWorkConditions(Multimap<NodeType, NodeId> allNodes, Map<NodeType, CountDownLatch> nodesCountDowns) {
            this.allNodes = allNodes;
            this.nodesCountDowns = nodesCountDowns;
            executor.execute(this);
        }

        @Override
        public void run() {
            try {
                boolean registrationCompleted;
                do {

                    for (NodeType nodeType : nodesCountDowns.keySet()) {
                        Collection<NodeId> availableNodes = coordinator.getAvailableNodes(nodeType);
                        for (NodeId availableNode : availableNodes) {
                            if (!allNodes.get(nodeType).contains(availableNode)) {
                                allNodes.get(nodeType).add(availableNode);
                                nodesCountDowns.get(nodeType).countDown();
                                log.debug("Node id {} with type {} added. Count left {}", new Object[]{
                                        availableNode,
                                        nodeType,
                                        nodesCountDowns.get(nodeType).getCount()}
                                );
                            }
                        }
                    }

                    registrationCompleted = leftToRegister() == 0;
                    if (!registrationCompleted) {
                        log.info("Waiting for nodes for {} ms", reconnectPeriod * 2);
                        for (NodeType nodeType : nodesCountDowns.keySet()) {
                            log.info("Left to register nodes of type {} - {}", nodeType, nodesCountDowns.get(nodeType).getCount());
                        }
                        Thread.sleep(reconnectPeriod * 2);
                    }
                } while (!registrationCompleted);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        private int leftToRegister() {
            int ret = 0;
            for (CountDownLatch countDownLatch : nodesCountDowns.values()) {
                ret += countDownLatch.getCount();
            }
            return ret;
        }


    }


}
