package com.griddynamics.jagger.monitoring;

import com.griddynamics.jagger.agent.model.GetSystemInfo;
import com.griddynamics.jagger.agent.model.SystemInfo;
import com.griddynamics.jagger.coordinator.Coordination;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeProcess;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.storage.fs.logging.LogProcessor;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.TimeUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @author Vladimir Kondrashchenko
 */
public abstract class AbstractMonitorProcess extends LogProcessor implements NodeProcess<MonitoringStatus> {

    protected static final Logger log = LoggerFactory.getLogger(AbstractMonitorProcess.class);

    protected final String sessionId;
    protected final NodeId agentId;
    protected final NodeContext nodeContext;
    protected final Coordinator coordinator;
    protected final ExecutorService executor;
    protected final long pollingInterval;
    protected final MonitoringProcessor monitoringProcessor;
    protected final String taskId;
    private volatile boolean alive;
    protected LogWriter logWriter;
    protected CountDownLatch latch;
    protected final long ttl;

    protected AbstractMonitorProcess(String sessionId, NodeId agentId, NodeContext nodeContext, Coordinator coordinator,
                                     ExecutorService executor, long pollingInterval,
                                     MonitoringProcessor monitoringProcessor, String taskId, LogWriter logWriter,
                                     SessionFactory sessionFactory, long ttl) {
        this.sessionId = sessionId;
        this.agentId = agentId;
        this.nodeContext = nodeContext;
        this.coordinator = coordinator;
        this.executor = executor;
        this.pollingInterval = pollingInterval;
        this.monitoringProcessor = monitoringProcessor;
        this.taskId = taskId;
        this.logWriter = logWriter;
        this.setSessionFactory(sessionFactory);
        this.ttl = ttl;
    }

    protected void doBeforeMonitoring(final RemoteExecutor remote) { };

    protected void doAfterMonitoring(final RemoteExecutor remote) { };

    @Override
    public void start() throws TechnicalException {
        log.info("Kernel {} has started monitoring on agent {} by task id {}", new Object[]{nodeContext.getId(), agentId, taskId});
        alive = true;

        final RemoteExecutor remote = coordinator.getExecutor(agentId);

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    doBeforeMonitoring(remote);

                    while (alive) {
                        long startTime = System.currentTimeMillis();
                        log.debug("try getting GetSystemInfo on kernel {} from {}", nodeContext.getId(), agentId);
                        try {
                            ArrayList<SystemInfo> info = remote.runSyncWithTimeout(new GetSystemInfo(sessionId), Coordination.<GetSystemInfo>doNothing(), ttl);
                            log.debug("GetSystemInfo got on kernel {} from {} time {} ms",
                                    new Object[]{nodeContext.getId(), agentId, System.currentTimeMillis() - startTime});
                            for (SystemInfo systemInfo : info) {
                                monitoringProcessor.process(sessionId, taskId, agentId, systemInfo);
                            }
                            log.debug("monitoring logged to file storage on kernel {}", nodeContext.getId());
                        } catch (Throwable e) {
                            // ignore this poll
                        }
                        TimeUtils.sleepMillis(pollingInterval);
                    }
                    log.debug("try to flush monitoring on kernel {}", nodeContext.getId());
                    logWriter.flush();
                    log.debug("monitoring flushed on kernel {}", nodeContext.getId());
                    log.debug("manage monitoring has done on agent {} from kernel {}", agentId, nodeContext.getId());

                    doAfterMonitoring(remote);

                } finally {
                    log.debug("releasing a latch");
                    if (latch != null) {
                        log.debug("latch is available");
                        latch.countDown();
                    }
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
        log.debug("Stop of monitoring requested. agent {}", agentId);
        latch = new CountDownLatch(1);
        alive = false;
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.warn("Interrupted {}", e);
        }
        log.info("Kernel {} has stopped monitoring on agent {}", nodeContext.getId(), agentId);
    }

    public long getTtl() {
        return ttl;
    }
}
