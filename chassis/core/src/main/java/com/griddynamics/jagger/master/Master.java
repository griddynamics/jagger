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
import com.griddynamics.jagger.master.configuration.*;
import com.griddynamics.jagger.monitoring.reporting.DynamicPlotGroups;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.fs.logging.LogReader;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.Futures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

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


    private Configuration configuration;
    private Coordinator coordinator;
    private DistributorRegistry distributorRegistry;
    private SessionIdProvider sessionIdProvider;
    private KeyValueStorage keyValueStorage;
    private ReportingService reportingService;
    private long reconnectPeriod;
    private Conditions conditions;
    private ExecutorService executor;
    private TaskIdProvider taskIdProvider;
    private Map<ManageAgent.ActionProp, Serializable> agentStopManagementProps;
    private MasterTimeoutConfiguration timeoutConfiguration;
    private boolean terminateConfiguration = false;
    private final Object terminateConfigurationLock = new Object();
    private CountDownLatch terminateConfigurationLatch = null;
    private final WeakHashMap<Service, Object> distributes = new WeakHashMap<Service, Object>();
    private DynamicPlotGroups dynamicPlotGroups;
    private LogWriter logWriter;
    private LogReader logReader;


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
    public void setDynamicPlotGroups(DynamicPlotGroups dynamicPlotGroups) {
        this.dynamicPlotGroups = dynamicPlotGroups;
    }

    @Required
    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    public LogWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    public LogReader getLogReader() {
        return logReader;
    }

    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
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

        NodeContextBuilder contextBuilder = Coordination.contextBuilder(NodeId.masterNode());
        contextBuilder
                .addService(LogWriter.class, getLogWriter())
                .addService(LogReader.class, getLogReader())
                .addService(KeyValueStorage.class, keyValueStorage);
        NodeContext context = contextBuilder.build();

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

        if (configuration.getMonitoringConfiguration() != null) {
            dynamicPlotGroups.setJmxMetricGroups(configuration.getMonitoringConfiguration().getMonitoringSutConfiguration().getJmxMetricGroups());
		    Map<ManageAgent.ActionProp, Serializable>  agentStartManagementProps = Maps.newHashMap();

		    agentStartManagementProps.put(
		            ManageAgent.ActionProp.SET_JMX_METRICS, dynamicPlotGroups.getJmxMetrics());
		    processAgentManagement(sessionId, agentStartManagementProps);
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

            SessionExecutionStatus status=runConfiguration(allNodes, context);

            log.info("Configuration work finished!!");

            for (SessionExecutionListener listener : configuration.getSessionExecutionListeners()) {
                if(listener instanceof SessionListener){
                    ((SessionListener)listener).onSessionExecuted(sessionId, sessionComment, status);
                } else {
                    listener.onSessionExecuted(sessionId, sessionComment);
                }
            }

            log.info("Going to generate report");
            if (configuration.getReport()!=null){
                configuration.getReport().renderReport(true);
            }else{
                reportingService.renderReport(true);
            }

            log.info("Report generated");

            log.info("Going to stop all agents");
            processAgentManagement(sessionId, agentStopManagementProps);
            log.info("Agents stopped");
        } finally {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (Exception e) {
            }
            terminateConfigurationLatch.countDown();
        }
    }

    private void processAgentManagement(String sessionId, Map<ManageAgent.ActionProp, Serializable> agentManagementProps) {
        for (NodeId agent : coordinator.getAvailableNodes(NodeType.AGENT)) {
            // async run
            coordinator.getExecutor(agent).run(new ManageAgent(sessionId, agentManagementProps),
                    Coordination.<ManageAgent>doNothing());
        }
    }

    private void validateConfiguration() {
        // TODO Auto-generated method stub
    }

    private SessionExecutionStatus runConfiguration(Multimap<NodeType, NodeId> allNodes, NodeContext nodeContext) {
        SessionExecutionStatus status = SessionExecutionStatus.EMPTY;
        try {
            log.info("Execution started");
            for (Task task : configuration.getTasks()) {
                synchronized (terminateConfigurationLock) {
                    if (terminateConfiguration) {
                        throw new TerminateException("Execution terminated");
                    }
                }

                try{
                    executeTask(task, allNodes, nodeContext);
                } catch (Exception e){
                    status = SessionExecutionStatus.TASK_FAILED;
                    log.error("Exception during execute task: {}", e);
                }
            }
            log.info("Execution done");
        } catch (Exception e) {
            status = SessionExecutionStatus.TERMINATED;
            log.error(" Exception while running configuration: {}",e);
        }
        return status;
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

    private void executeTask(Task task, Multimap<NodeType, NodeId> allNodes, NodeContext nodeContext) throws TerminateException {
        log.debug("Distributing task {}", task);

        @SuppressWarnings("unchecked")
        TaskDistributor<Task> taskDistributor = (TaskDistributor<Task>) distributorRegistry.getTaskDistributor(task.getClass());

        Map<NodeId, RemoteExecutor> remotes = Maps.newHashMap();

        log.debug("Distributed task will be executed on {} nodes", remotes.size());

        String taskId = taskIdProvider.getTaskId();

        Service distribute = taskDistributor.distribute(executor, sessionIdProvider.getSessionId(), taskId, allNodes, coordinator, task, distributionListener(), nodeContext);
        try{
            Future<Service.State> start;
            synchronized (terminateConfigurationLock) {
                distributes.put(distribute, null);
                start = distribute.start();
            }
            Futures.get(start, timeoutConfiguration.getDistributionStartTime());
            Services.awaitTermination(distribute, timeoutConfiguration.getTaskExecutionTime());
        } finally {
            Future<Service.State> stop = distribute.stop();
            Futures.get(stop, timeoutConfiguration.getDistributionStopTime());
        }

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

    public void setReportingService(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void setTaskIdProvider(TaskIdProvider taskIdProvider) {
        this.taskIdProvider = taskIdProvider;
    }

    public Map<ManageAgent.ActionProp, Serializable> getAgentStopManagementProps() {
        return agentStopManagementProps;
    }

    public void setAgentStopManagementProps(Map<ManageAgent.ActionProp, Serializable> agentStopManagementProps) {
        this.agentStopManagementProps = agentStopManagementProps;
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
