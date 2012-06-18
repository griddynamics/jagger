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

package com.griddynamics.jagger.engine.e1.process;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeProcess;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadConfiguration;
import com.griddynamics.jagger.invoker.Scenario;
import com.griddynamics.jagger.util.Futures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A process that performs remote service invocation in specified thread number.
 */
public class WorkloadProcess implements NodeProcess<Integer> {

    private static final Logger log = LoggerFactory.getLogger(WorkloadProcess.class);
    private static final int START_TIMEOUT = 30000;
    private static final int STOP_TIMEOUT = 3600000;

    private final String sessionId;
    private final StartWorkloadProcess command;
    private final NodeContext context;
    private final ExecutorService executor;

    private List<WorkloadService> threads;

    private volatile int delay;

    private int samplesCountDoneFromTerminatedThreads = 0;

    private int totalSamplesCountRequested;

    private final AtomicInteger leftSamplesCount = new AtomicInteger(-1);

    public WorkloadProcess(String sessionId, StartWorkloadProcess command, NodeContext context, ExecutorService executor) {
        this.sessionId = sessionId;
        this.command = command;
        this.context = context;
        this.executor = executor;
    }

    public void start() {
        threads = Lists.newLinkedList();
        delay = command.getScenarioContext().getWorkloadConfiguration().getDelay();

        log.debug("Going to execute command {}.", command);

        totalSamplesCountRequested = command.getScenarioContext().getWorkloadConfiguration().getSamples();
        leftSamplesCount.set(totalSamplesCountRequested);
        for (int i = 0; i < command.getThreads(); i++) {
            addThread();
        }

        log.debug("Threads are scheduled");
    }

    public void stop() {

        log.debug("Going to terminate");
        List<ListenableFuture<Service.State>> futures = Lists.newLinkedList();
        for (WorkloadService thread : threads) {
            ListenableFuture<Service.State> stop = thread.stop();
            futures.add(stop);
        }

        for (ListenableFuture<Service.State> future : futures) {
            Service.State state = Futures.get(future, STOP_TIMEOUT);
            log.debug("stopped workload thread with status {}", state);
        }
        log.debug("All threads were terminated");
        executor.shutdown();
        log.debug("Shutting down executor");
    }

    public Integer getStatus() {
        Integer result = samplesCountDoneFromTerminatedThreads;
        for (WorkloadService thread : threads) {
            result += thread.getSamples();
        }
        return result;
    }

    public void changeConfiguration(WorkloadConfiguration configuration) {
        log.debug("Configuration change request received");

        for (Iterator<WorkloadService> it = threads.iterator(); it.hasNext(); ){
            WorkloadService workloadService = it.next();
            if (workloadService.state().equals(Service.State.TERMINATED)) {
                samplesCountDoneFromTerminatedThreads += workloadService.getSamples();
                it.remove();
            }
        }

        final int threadDiff = configuration.getThreads() - threads.size();

        if (threadDiff < 0) {
            log.debug("Going to decrease thread count by {}", threadDiff);
            for (int i = threadDiff; i < 0; i++) {
                removeThread();
            }
        }

        if (totalSamplesCountRequested != configuration.getSamples()) {
            leftSamplesCount.addAndGet(configuration.getSamples() - totalSamplesCountRequested);
            totalSamplesCountRequested = configuration.getSamples();
        }

        if (threadDiff > 0 && (!predefinedSamplesCount() || leftSamplesCount.get() > 0)) {
            log.debug("Going to increase thread count by {}", threadDiff);
            for (int i = threadDiff; i > 0; i--) {
                addThread();
            }
        }

        delay = configuration.getDelay();
        log.debug("Delay should be changed to {}", delay);
        for (WorkloadService thread : threads) {
            thread.changeDelay(delay);
        }
    }

    private void addThread() {
        log.debug("Adding new workload thread");
        Scenario<Object, Object, Object> scenario = command.getScenarioFactory().get(context);

        List<ScenarioCollector<?, ?, ?>> collectors = Lists.newLinkedList();
        for (KernelSideObjectProvider<ScenarioCollector<Object, Object, Object>> provider : command.getCollectors()) {
            collectors.add(provider.provide(sessionId, command.getTaskId(), context));
        }

        WorkloadService.WorkloadServiceBuilder builder = WorkloadService
                .builder(scenario)
                .addCollectors(collectors)
                .useExecutor(executor);
        WorkloadService thread = ( predefinedSamplesCount()) ? builder.buildServiceWithSharedSamplesCount(leftSamplesCount) : builder.buildInfiniteService();
        log.debug("Starting workload");
        Future<Service.State> future = thread.start();
        Service.State state = Futures.get(future, START_TIMEOUT);
        log.debug("Workload thread with is started with state {}", state);
        threads.add(thread);
    }

    private boolean predefinedSamplesCount() {
        return totalSamplesCountRequested != -1;
    }

    private void removeThread() {
        Preconditions.checkState(!threads.isEmpty());

        Iterator<WorkloadService> iterator = threads.iterator();

        if (!iterator.hasNext()) {
            log.debug("Cannot remove task. No tasks started.");
            return;
        }

        WorkloadService thread = iterator.next();
        Future<Service.State> stop = thread.stop();
        Futures.get(stop, STOP_TIMEOUT);
        iterator.remove();
    }
}
