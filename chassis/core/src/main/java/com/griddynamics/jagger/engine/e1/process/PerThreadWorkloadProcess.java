package com.griddynamics.jagger.engine.e1.process;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Service;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadConfiguration;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.util.Futures;
import com.griddynamics.jagger.util.TimeoutsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class PerThreadWorkloadProcess extends AbstractWorkloadProcess {

    private static final Logger log = LoggerFactory.getLogger(PerThreadWorkloadProcess.class);

    private int totalSamplesCountRequested;

    private final AtomicInteger leftSamplesCount = new AtomicInteger(-1);

    public PerThreadWorkloadProcess(String sessionId, StartWorkloadProcess command, NodeContext context, ThreadPoolExecutor executor, TimeoutsConfiguration timeoutsConfiguration) {
        super(executor, sessionId, command, context, timeoutsConfiguration);
    }

    @Override
    protected Collection<WorkloadService> getRunningWorkloadServiceCollection() {
        return Lists.newLinkedList();
    }

    @Override
    public void start() throws TechnicalException {
        log.debug("Going to execute command {}.", command);
        super.start();

    }

    @Override
    protected void doStart() {

        int delay = command.getScenarioContext().getWorkloadConfiguration().getDelay();

        totalSamplesCountRequested = command.getScenarioContext().getWorkloadConfiguration().getSamples();
        leftSamplesCount.set(totalSamplesCountRequested);
        for (int i = 0; i < command.getThreads(); i++) {
            startNewThread(delay);
        }

        log.debug("Threads are scheduled");
    }

    @Override
    protected WorkloadService getService(AbstractWorkloadService.WorkloadServiceBuilder builder) {
        return ( predefinedSamplesCount()) ? builder.buildServiceWithSharedSamplesCount(leftSamplesCount) : builder.buildInfiniteService();
    }

    private boolean predefinedSamplesCount() {
        return totalSamplesCountRequested != -1;
    }

    @Override
    public void changeConfiguration(WorkloadConfiguration configuration) {
        log.debug("Configuration change request received");

        for (Iterator<WorkloadService> it = threads.iterator(); it.hasNext(); ){
            WorkloadService workloadService = it.next();
            if (workloadService.state().equals(Service.State.TERMINATED)) {
                samplesCountStartedFromTerminatedThreads += workloadService.getStartedSamples();
                samplesCountFinishedFromTerminatedThreads += workloadService.getFinishedSamples();
                it.remove();
            }
        }

        final int threadDiff = configuration.getThreads() - threads.size();

        if (threadDiff < 0) {
            log.debug("Going to decrease thread count by {}", threadDiff);
            removeThreads(Math.abs(threadDiff));
        }

        if (totalSamplesCountRequested != configuration.getSamples()) {
            leftSamplesCount.addAndGet(configuration.getSamples() - totalSamplesCountRequested);
            totalSamplesCountRequested = configuration.getSamples();
        }

        int delay = configuration.getDelay();

        if (threadDiff > 0 && (!predefinedSamplesCount() || leftSamplesCount.get() > 0)) {
            log.debug("Going to increase thread count by {}", threadDiff);
            for (int i = threadDiff; i > 0; i--) {
                startNewThread(delay);
            }
        }


        log.debug("Delay should be changed to {}", delay);
        for (WorkloadService thread : threads) {
            thread.changeDelay(delay);
        }
    }

    private void removeThreads(int count) {
        Preconditions.checkState(!threads.isEmpty());
        Preconditions.checkState(threads.size() >= count);

        Collection<Future<Service.State>> futures = Lists.newLinkedList();

        Iterator<WorkloadService> iterator = threads.iterator();
        for (int i=0; i<count; i++){
            WorkloadService service = iterator.next();
            futures.add(service.stop());
        }

        for (Future<Service.State> future : futures){
            Futures.get(future, timeoutsConfiguration.getWorkloadStopTimeout());
        }
    }
}
