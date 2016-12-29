package com.griddynamics.jagger.engine.e1.process;

import com.google.common.util.concurrent.Service;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadConfiguration;
import com.griddynamics.jagger.util.Futures;
import com.griddynamics.jagger.util.TimeoutsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Workload process that perform invocation with given period.
 */
public class PeriodWorkloadProcess extends AbstractWorkloadProcess {

    private PeriodSingleTaskScheduler loopExecutor = new PeriodSingleTaskScheduler();


    Logger log = LoggerFactory.getLogger(PeriodWorkloadProcess.class);

    public PeriodWorkloadProcess(String sessionId, StartWorkloadProcess command, NodeContext context, ThreadPoolExecutor executor, TimeoutsConfiguration timeoutsConfiguration) {
        super(executor, sessionId, command, context, timeoutsConfiguration);
    }

    @Override
    protected Collection<WorkloadService> getRunningWorkloadServiceCollection() {
        return new LinkedBlockingQueue<WorkloadService>();
    }

    @Override
    protected void doStart() {
        long period = command.getScenarioContext().getWorkloadConfiguration().getPeriod();
        long delay = command.getScenarioContext().getWorkloadConfiguration().getDelay();

        // start scheduling task with given period.
        loopExecutor.scheduleAtFixedRate(() -> startNewThread(0), delay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void startNewThread(int delay) {

        for (WorkloadService thread : threads) {
            Future<Service.State> future = thread.start();
            if (future != null) {
                Service.State state = Futures.get(future, timeoutsConfiguration.getWorkloadStartTimeout());
                log.debug("Workload thread is started with state {}. Total threads number - {}", state, executor.getActiveCount());
                return;
            }
        }

        if (executor.getActiveCount() >= executor.getMaximumPoolSize()) {
            log.warn("Thread pool(size={}) is full. Skip adding new thread.", executor.getPoolSize());
            return;
        }

        super.startNewThread(delay);
    }

    @Override
    protected WorkloadService getService(AbstractWorkloadService.WorkloadServiceBuilder serviceBuilder) {
        // return workload service that should execute 1 sample on demand
        return serviceBuilder.buildInvokeOnDemandWorkloadService();
    }


    @Override
    public void stop() {
        // do not stat new workload service
        loopExecutor.clear();
        loopExecutor.shutdown();

        super.stop();
    }


    @Override
    public void changeConfiguration(WorkloadConfiguration configuration) {
        loopExecutor.scheduleAtFixedRate(() -> startNewThread(0), configuration.getDelay(), configuration.getPeriod(), TimeUnit.MILLISECONDS);
    }


}
