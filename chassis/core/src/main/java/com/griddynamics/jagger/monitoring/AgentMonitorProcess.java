package com.griddynamics.jagger.monitoring;

import com.griddynamics.jagger.agent.model.GetCollectedProfileFromSuT;
import com.griddynamics.jagger.agent.model.ManageCollectionProfileFromSuT;
import com.griddynamics.jagger.coordinator.Coordination;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.coordinator.VoidResult;
import com.griddynamics.jagger.diagnostics.thread.sampling.ProfileDTO;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.SerializationUtils;
import org.hibernate.SessionFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author Vladimir Kondrashchenko
 */
public class AgentMonitorProcess extends AbstractMonitorProcess {

    public static final String PROFILER_MARKER = "PROFILER";

    private final long profilerPollingInterval;

    private final ThreadLocal<VoidResult> startPoolingResult = new ThreadLocal<VoidResult>();

    public AgentMonitorProcess(String sessionId, NodeId agentId, NodeContext nodeContext, Coordinator coordinator,
                               ExecutorService executor, long pollingInterval, MonitoringProcessor monitoringProcessor,
                               long profilerPollingInterval, String taskId, LogWriter logWriter, SessionFactory sessionFactory, long ttl) {
        super(sessionId, agentId, nodeContext, coordinator, executor, pollingInterval, monitoringProcessor, taskId,
                logWriter, sessionFactory, ttl);

        this.profilerPollingInterval = profilerPollingInterval;
    }

    @Override
    protected void doBeforeMonitoring(final RemoteExecutor remote) {
        VoidResult voidResult = remote.runSyncWithTimeout(new ManageCollectionProfileFromSuT(sessionId,
                ManageCollectionProfileFromSuT.ManageHotSpotMethodsFromSuT.START_POLLING, profilerPollingInterval), Coordination.<ManageCollectionProfileFromSuT>doNothing(), ttl);

        if (voidResult.hasException()) {
            log.error("Remote exception raised during staring profiling from SuT", voidResult.getException());
        }

        startPoolingResult.set(voidResult);
    }

    @Override
    protected void doAfterMonitoring(final RemoteExecutor remote) {
        VoidResult voidResult = startPoolingResult.get();

        if (voidResult != null && !voidResult.hasException()) {
            log.debug("try to manage monitoring on agent {} from kernel {}", agentId, nodeContext.getId());
            voidResult = remote.runSyncWithTimeout(new ManageCollectionProfileFromSuT(sessionId, ManageCollectionProfileFromSuT.ManageHotSpotMethodsFromSuT.STOP_POLLING,
                    profilerPollingInterval), Coordination.<ManageCollectionProfileFromSuT>doNothing(), ttl);
            if (voidResult.hasException())
                log.error("Remote exception raised during stopping profiling from SuT", voidResult.getException());
            log.debug("try to get collected profiler from agent {} from kernel {}", agentId, nodeContext.getId());
            final ProfileDTO profileDTO =
                    remote.runSyncWithTimeout(GetCollectedProfileFromSuT.create(sessionId), Coordination.<GetCollectedProfileFromSuT>doNothing(), ttl);
            log.debug("got collected profiler from agent {} from kernel {}", agentId, nodeContext.getId());
            logWriter.log(sessionId, taskId + "/" + PROFILER_MARKER, agentId.getIdentifier(), SerializationUtils.toString(profileDTO));
            log.debug("Profiler {} received from agent {} and has been written to FileStorage", profileDTO, agentId);
            logWriter.flush();
            log.debug("Flushing performed on kernel {}", nodeContext.getId());

        } else {
            log.warn("Collection profiling from SuT didn't started.");
        }
    }
}
