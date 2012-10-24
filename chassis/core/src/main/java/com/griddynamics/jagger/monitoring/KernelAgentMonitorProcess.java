package com.griddynamics.jagger.monitoring;

import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import org.hibernate.SessionFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author Vladimir Kondrashchenko
 */
public class KernelAgentMonitorProcess extends AbstractMonitorProcess {

    public KernelAgentMonitorProcess(String sessionId, NodeId agentId, NodeContext nodeContext, Coordinator coordinator,
                                     ExecutorService executor, long pollingInterval, MonitoringProcessor monitoringProcessor,
                                     String taskId, LogWriter logWriter, SessionFactory sessionFactory, long ttl) {
        super(sessionId, agentId, nodeContext, coordinator, executor, pollingInterval, monitoringProcessor,
                taskId, logWriter, sessionFactory, ttl);
    }
}
