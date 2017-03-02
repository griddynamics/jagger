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

package com.griddynamics.jagger.master;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Service;
import com.griddynamics.jagger.agent.model.ManageAgent;
import com.griddynamics.jagger.coordinator.Coordination;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.engine.e1.ProviderUtil;
import com.griddynamics.jagger.engine.e1.aggregator.session.GeneralNodeInfoAggregator;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioInfo;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioListener;
import com.griddynamics.jagger.engine.e1.process.Services;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.services.SessionMetaDataStorage;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.SessionExecutionListener;
import com.griddynamics.jagger.master.configuration.SessionExecutionStatus;
import com.griddynamics.jagger.master.configuration.SessionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.monitoring.reporting.DynamicPlotGroups;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.fs.logging.LogReader;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.Futures;
import com.griddynamics.jagger.util.GeneralNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
    private TaskExecutionStatusProvider taskExecutionStatusProvider;
    private Map<ManageAgent.ActionProp, Serializable> agentStopManagementProps;
    private MasterTimeoutConfiguration timeoutConfiguration;
    // made volatile just in case somebody will try to access it outside of synchronized block
    private volatile boolean isTerminated = false;
    private CountDownLatch terminateConfigurationLatch = new CountDownLatch(1);
    private final WeakHashMap<Service, Object> distributes = new WeakHashMap<Service, Object>();
    private DynamicPlotGroups dynamicPlotGroups;
    private LogWriter logWriter;
    private LogReader logReader;
    private GeneralNodeInfoAggregator generalNodeInfoAggregator;
    private SessionMetaDataStorage metaDataStorage;
    private DatabaseService databaseService;
    private DecisionMakerDistributionListener decisionMakerDistributionListener;

    private Thread shutdownHook = new Thread(new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                isTerminated = true;
                for (Service distribute : distributes.keySet()) {
                    distribute.stopAndWait();
                }
            }
            try {
                terminateConfigurationLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }, String.format("Shutdown hook for %s", getClass().toString()));
    
    public SessionIdProvider getSessionIdProvider() {
        return sessionIdProvider;
    }
    
    @Required
    public void setReconnectPeriod(long reconnectPeriod) {
        this.reconnectPeriod = reconnectPeriod;
    }

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

    public void setMetaDataStorage(SessionMetaDataStorage metaDataStorage) {
        this.metaDataStorage = metaDataStorage;
    }

    public void setGeneralNodeInfoAggregator(GeneralNodeInfoAggregator generalNodeInfoAggregator) {
        this.generalNodeInfoAggregator = generalNodeInfoAggregator;
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Required
    public void setDecisionMakerDistributionListener(DecisionMakerDistributionListener decisionMakerDistributionListener) {
        this.decisionMakerDistributionListener = decisionMakerDistributionListener;
    }

    @PostConstruct
    public void init() {
        if (!keyValueStorage.isAvailable()) {
            keyValueStorage.initialize();
            keyValueStorage.setSessionId(sessionIdProvider.getSessionId());
        }

        metaDataStorage.setComment(sessionIdProvider.getSessionComment());
    }

    @Override
    public void run() {
        final String sessionId = sessionIdProvider.getSessionId();
        Multimap<NodeType, NodeId> allNodes = HashMultimap.create();
        allNodes.putAll(NodeType.MASTER, coordinator.getAvailableNodes(NodeType.MASTER));
        NodeContext context = Coordination.contextBuilder(NodeId.masterNode())
                .addService(LogWriter.class, getLogWriter())
                .addService(LogReader.class, getLogReader())
                .addService(KeyValueStorage.class, keyValueStorage)
                .addService(SessionMetaDataStorage.class, metaDataStorage)
                .addService(DatabaseService.class, databaseService)
                .build();
        // add additional listener to configuration
        // done here (not in spring like other listeners), because we need to set context to this listener
        decisionMakerDistributionListener.setNodeContext(context);
        configuration.getDistributionListeners().add(decisionMakerDistributionListener);

        CountDownLatch agentCountDownLatch = new CountDownLatch(
                conditions.isMonitoringEnable() ?
                        conditions.getMinAgentsCount() :
                        0
        );
        CountDownLatch kernelCountDownLatch = new CountDownLatch(conditions.getMinKernelsCount());
        Map<NodeType, CountDownLatch> countDownLatchMap = Maps.newHashMap();
        countDownLatchMap.put(NodeType.AGENT, agentCountDownLatch);
        countDownLatchMap.put(NodeType.KERNEL, kernelCountDownLatch);

        new StartWorkConditions(allNodes, countDownLatchMap);
        try {
            agentCountDownLatch.await(timeoutConfiguration.getNodeAwaitTime().getValue(), TimeUnit.MILLISECONDS);
            kernelCountDownLatch.await(timeoutConfiguration.getNodeAwaitTime().getValue(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("CountDownLatch await interrupted", e);
        }

        for (SessionExecutionListener listener : configuration.getSessionExecutionListeners()) {
            listener.onSessionStarted(sessionId, allNodes);
        }

        try {
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            log.info("Configuration launched!!");


            LoadScenarioListener
                    loadScenarioListener = LoadScenarioListener.Composer.compose(ProviderUtil.provideElements(configuration.getLoadScenarioListeners(),
                    sessionId,
                    "session",
                    context,
                    JaggerPlace.LOAD_SCENARIO_LISTENER));
            // collect information about environment on kernel and agent nodes
            Map<NodeId, GeneralNodeInfo> generalNodeInfo = generalNodeInfoAggregator.getGeneralNodeInfo(sessionId, coordinator);
            LoadScenarioInfo loadScenarioInfo = new LoadScenarioInfo(sessionId, generalNodeInfo);
            long startTime = System.currentTimeMillis();

            loadScenarioListener.onStart(loadScenarioInfo);
            // tests execution
            SessionExecutionStatus status = runConfiguration(allNodes, context);
            loadScenarioInfo.setDuration(System.currentTimeMillis() - startTime);
            log.info("Configuration work finished!!");
            loadScenarioListener.onStop(loadScenarioInfo);

            for (SessionExecutionListener listener : configuration.getSessionExecutionListeners()) {
                if (listener instanceof SessionListener) {
                    ((SessionListener) listener).onSessionExecuted(sessionId, metaDataStorage.getComment(), status);
                } else {
                    listener.onSessionExecuted(sessionId, metaDataStorage.getComment());
                }
            }
            log.info("Going to stop all agents");
            processAgentManagement(sessionId, agentStopManagementProps);
            log.info("Agents stopped");

            log.info("Going to generate report");
            if (configuration.getReport() != null) {
                configuration.getReport().renderReport(true);
            } else {
                reportingService.renderReport(true);
            }
            log.info("Report generated");
        } finally {
            try {
                keyValueStorage.deleteAll();
                log.info("Temporary data for session {} deleted ", sessionId);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
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

    private SessionExecutionStatus runConfiguration(Multimap<NodeType, NodeId> allNodes, NodeContext nodeContext) {
        SessionExecutionStatus status = SessionExecutionStatus.EMPTY;
        try {
            log.info("Execution started");
            for (Task task : configuration.getTasks()) {
                try {
                    executeTask(task, allNodes, nodeContext);
                } catch (RuntimeException e) {
                    // catching here only unchecked exceptions
                    // only checked exception could be thrown - TerminateException - handled by outer try/catch block
                    status = SessionExecutionStatus.TASK_FAILED;
                    log.error("Exception during execute task: {}", e);
                }
            }
            log.info("Execution done");
        } catch (TerminateException e) {
            status = SessionExecutionStatus.TERMINATED;
            log.error(" Exception while running configuration: {}", e);
        }
        return status;
    }

    private void executeTask(Task task, Multimap<NodeType, NodeId> allNodes, NodeContext nodeContext) throws TerminateException {
        log.debug("Distributing task {}", task);

        @SuppressWarnings("unchecked")
        TaskDistributor<Task> taskDistributor = (TaskDistributor<Task>) distributorRegistry.getTaskDistributor(task.getClass());
        task.setNumber(taskIdProvider.getTaskId());
        String taskId = taskIdProvider.stringify(task.getNumber());
        taskExecutionStatusProvider.setStatus(taskId, TaskData.ExecutionStatus.QUEUED);
        Service distribute = taskDistributor.distribute(executor, sessionIdProvider.getSessionId(), taskId, allNodes, coordinator, task, distributionListener(), nodeContext);
        try {
            synchronized (this) {
                if (isTerminated) { // if it was terminated by shutdown hook
                    throw new TerminateException("Execution terminated");
                }
                distributes.put(distribute, null);
            }
            Futures.get(distribute.start(), timeoutConfiguration.getDistributionStartTime());
            Services.awaitTermination(distribute, timeoutConfiguration.getTaskExecutionTime().getValue());
        } finally {
            Futures.get(distribute.stop(), timeoutConfiguration.getDistributionStopTime());
        }
    }

    private DistributionListener distributionListener() {
        return CompositeDistributionListener.of(Iterables.concat(Arrays.asList(createFlushListener()),
                configuration.getDistributionListeners()
        ));
    }

    // provide listener, which will flush temporary data in LogStorage
    // it guarantees that all data will be recorded
    private DistributionListener createFlushListener() {
        return new DistributionListener() {
            @Override
            public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
                //nothing
            }

            @Override
            public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
                logWriter.flush();
            }
        };
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

    @Required
    public void setTaskExecutionStatusProvider(TaskExecutionStatusProvider taskExecutionStatusProvider) {
        this.taskExecutionStatusProvider = taskExecutionStatusProvider;
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
                                log.debug("Node id {} with type {} added. Count left {}", new Object[] {
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
