package com.griddynamics.jagger.engine.e1.process;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.ProviderUtil;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.NodeSideInitializable;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.invoker.Scenario;
import com.griddynamics.jagger.util.Futures;
import com.griddynamics.jagger.util.TimeoutsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Abstract workload process to simplify status collection process.
 */
public abstract class AbstractWorkloadProcess implements WorkloadProcess {

    // list of current active workload services
    protected Collection<WorkloadService> threads;

    // executor to execute workload services
    protected final ThreadPoolExecutor executor;
    protected final String sessionId;
    protected final StartWorkloadProcess command;
    protected final NodeContext context;
    protected final TimeoutsConfiguration timeoutsConfiguration;

    protected volatile int samplesCountStartedFromTerminatedThreads = 0;
    protected volatile int samplesCountFinishedFromTerminatedThreads = 0;
    protected volatile long emptyTransactionsFromTerminatedThreads = 0;

    private static final Logger log = LoggerFactory.getLogger(AbstractWorkloadProcess.class);

    public AbstractWorkloadProcess(ThreadPoolExecutor executor, String sessionId, StartWorkloadProcess command, NodeContext context, TimeoutsConfiguration timeoutsConfiguration) {
        this.executor = executor;
        this.sessionId = sessionId;
        this.command = command;
        this.context = context;
        this.timeoutsConfiguration = timeoutsConfiguration;
    }

    @Override
    public void start() throws TechnicalException {
        threads = getRunningWorkloadServiceCollection();

        for (KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>> provider : command.getCollectors()) {
            if (provider instanceof NodeSideInitializable) {
                ((NodeSideInitializable) provider).init(sessionId, command.getTaskId(), context);
            }
        }

        for (Provider<InvocationListener<Object, Object, Object>> provider : command.getListeners()){
            ProviderUtil.injectContext(provider, sessionId, command.getTaskId(), context, JaggerPlace.INVOCATION_LISTENER);
        }

        doStart();
    }


    /**
     * @return implementation of collection to be used to collect statistics for status.
     */
    protected abstract Collection<WorkloadService> getRunningWorkloadServiceCollection();

    /**
     * Actually starts workload after initialising providers.
     */
    protected abstract void doStart();


    /**
     * Stops all current running workload services, and shutdown workload executor.
     */
    public void stop() {

        log.debug("Going to terminate");
        List<ListenableFuture<Service.State>> futures = Lists.newLinkedList();
        for (WorkloadService thread : threads) {
            ListenableFuture<Service.State> stop = thread.stop();
            futures.add(stop);
        }

        for (ListenableFuture<Service.State> future : futures) {
            Service.State state = Futures.get(future, timeoutsConfiguration.getWorkloadStopTimeout());
            log.debug("stopped workload thread with status {}", state);
        }
        log.debug("All threads were terminated");
        executor.shutdown();
        log.debug("Shutting down executor");
    }


    @Override
    public WorkloadStatus getStatus() {
        int started = samplesCountStartedFromTerminatedThreads;
        int finished = samplesCountFinishedFromTerminatedThreads;
        long emptyTrn = emptyTransactionsFromTerminatedThreads;
        int runningThreads = 0;
        
        for (WorkloadService thread : threads) {
            started += thread.getStartedSamples();
            finished += thread.getFinishedSamples();
            emptyTrn += thread.getEmptyTransactions();
            if (thread.isRunning()) {
                runningThreads ++;
            }
        }
        return new WorkloadStatus(started, finished, runningThreads, emptyTrn);
    }

    /**
     * Common method to add new workload service with all listeners.
     *
     * @param delay delay in milliseconds that will be used in {@code WorkloadService}
     */
    protected void startNewThread(int delay) {

        log.debug("Adding new workload thread");
        Scenario<Object, Object, Object> scenario = command.getScenarioFactory().get(context, command.getKernelInfo());

        List<InvocationListener<?, ?, ?>> listeners = Lists.newArrayList();
        for (Provider<InvocationListener<Object, Object, Object>> listener : command.getListeners()){
            listeners.add(listener.provide());
        }

        List<ScenarioCollector<?, ?, ?>> collectors = Lists.newLinkedList();
        for (KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>> provider : command.getCollectors()) {
            collectors.add(provider.provide(sessionId, command.getTaskId(), context));
        }

        List<Validator> validators = Lists.newLinkedList();
        for (KernelSideObjectProvider<Validator> provider : command.getValidators()){
            validators.add(provider.provide(sessionId, command.getTaskId(), context));
        }

        AbstractWorkloadService.WorkloadServiceBuilder builder = AbstractWorkloadService
                .builder(scenario)
                .addCollectors(collectors)
                .addValidators(validators)
                .addListeners(listeners)
                .useExecutor(executor);
        WorkloadService thread = getService(builder);
        thread.changeDelay(delay);
        log.debug("Starting workload");
        Future<Service.State> future = thread.start();
        Service.State state = Futures.get(future, timeoutsConfiguration.getWorkloadStartTimeout());
        log.debug("Workload thread was started with state {}", state);
        threads.add(thread);
    }


    /**
     * Determines type of workload service to be executed.
     *
     * @return workload service to be started while adding new thread.
     */
    protected abstract WorkloadService getService(AbstractWorkloadService.WorkloadServiceBuilder serviceBuilder);
}
