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

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.scenario.Flushable;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.invoker.Invokers;
import com.griddynamics.jagger.invoker.Scenario;
import com.griddynamics.jagger.util.NewThreadExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static com.griddynamics.jagger.util.TimeUtils.sleepMillis;

public abstract class WorkloadService extends AbstractExecutionThreadService {
    private static final Logger log = LoggerFactory.getLogger(WorkloadService.class);

    private final Executor executor;
    private final Scenario<?, ?, ?> scenario;
    private final List<? extends Flushable> collectors;

    private final AtomicInteger delay = new AtomicInteger(0);
    private final AtomicInteger startedSamples = new AtomicInteger(0);
    private final AtomicInteger finishedSamples = new AtomicInteger(0);

    public static WorkloadServiceBuilder builder(Scenario<Object, Object, Object> scenario) {
        return new WorkloadServiceBuilder(scenario);
    }

    private WorkloadService(Executor executor, Scenario<?, ?, ?> scenario, List<? extends Flushable> collectors) {
        this.executor = executor;
        this.scenario = scenario;
        this.collectors = collectors;
    }

    @Override
    protected void run() throws Exception {
        try {
            while (isRunning() && !terminationRequired()) {
                log.debug("Scenario {} doTransaction called", scenario);
                startedSamples.incrementAndGet();
                scenario.doTransaction();
                log.debug("Sleep between invocations for {}", delay);
                sleepMillis(delay.get());
                finishedSamples.incrementAndGet();
            }
        } catch (Throwable error) {
            log.error("Error during the invocation", error);
        }
    }

    @Override
    protected void shutDown() throws Exception {
        try {
            for (Flushable collector : collectors) {
                collector.flush();
            }
        } catch (Throwable error) {
            log.error("Error during flushing", error);
        }
    }

    @Override
    protected Executor executor() {
        return executor;
    }

    protected abstract boolean terminationRequired();


    public Integer getStartedSamples() {
        return startedSamples.get();
    }

    public Integer getFinishedSamples() {
        return finishedSamples.get();
    }

    public void changeDelay(int delay) {
        this.delay.set(delay);
    }

    public static class WorkloadServiceBuilder {
        private final Scenario<Object, Object, Object> scenario;
        private Executor executor = NewThreadExecutor.INSTANCE;
        private ImmutableList.Builder<ScenarioCollector<?, ?, ?>> collectors = ImmutableList.builder();
        private ImmutableList.Builder<Validator> validators = ImmutableList.builder();
        private ImmutableList.Builder<InvocationListener> listeners = ImmutableList.builder();

        private WorkloadServiceBuilder(Scenario<Object, Object, Object> scenario) {
            this.scenario = scenario;
        }

        public WorkloadServiceBuilder addCollectors(List<ScenarioCollector<?, ?, ?>> collectors) {
            this.collectors.addAll(collectors);
            return this;
        }

        public WorkloadServiceBuilder addCollector(ScenarioCollector<?, ?, ?> collector) {
            collectors.add(collector);
            return this;
        }

        public WorkloadServiceBuilder addValidators(List<Validator> validators) {
            this.validators.addAll(validators);
            return this;
        }

        public WorkloadServiceBuilder addListeners(List<InvocationListener<?, ?, ?>> listeners) {
            this.listeners.addAll(listeners);
            return this;
        }

        public WorkloadServiceBuilder useExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public WorkloadService buildInfiniteService() {
            ImmutableList<ScenarioCollector<?, ?, ?>> collectorsList = collectors.build();
            ImmutableList<Validator> validatorList = validators.build();
            ImmutableList<InvocationListener> listenersList = listeners.build();

            scenario.setInvocationListener(Invokers.validateListener(validatorList, (Iterable) collectorsList, (List)listenersList));
            return new InfiniteWorkloadService(Invokers.mergeFlushElements(validatorList, collectorsList));
        }

        public WorkloadService buildServiceWithPredefinedSamples(final int samples) {
            ImmutableList<ScenarioCollector<?, ?, ?>> collectorsList = collectors.build();
            ImmutableList<Validator> validatorList = validators.build();
            ImmutableList<InvocationListener> listenersList = listeners.build();

            scenario.setInvocationListener(Invokers.validateListener(validatorList, (Iterable) collectorsList, (List)listenersList));
            return new PredefinedSamplesWorkloadService(Invokers.mergeFlushElements(validatorList, collectorsList), samples);
        }

        public WorkloadService buildServiceWithSharedSamplesCount(final AtomicInteger samples) {
            ImmutableList<ScenarioCollector<?, ?, ?>> collectorsList = collectors.build();
            ImmutableList<Validator> validatorList = validators.build();
            ImmutableList<InvocationListener> listenersList = listeners.build();

            scenario.setInvocationListener(Invokers.validateListener(validatorList, (Iterable) collectorsList, (List)listenersList));
            return new SharedSamplesCountWorkloadService(Invokers.mergeFlushElements(validatorList, collectorsList), samples);
        }

        private class InfiniteWorkloadService extends WorkloadService {
            
            private InfiniteWorkloadService(ImmutableList<? extends Flushable> list) {
                super(WorkloadServiceBuilder.this.executor, WorkloadServiceBuilder.this.scenario, list);
            }

            @Override
            protected boolean terminationRequired() {
                return false;
            }
        }

        private class PredefinedSamplesWorkloadService extends WorkloadService {

            private final int samples;

            private PredefinedSamplesWorkloadService(ImmutableList<? extends Flushable> list, int samples) {
                super(WorkloadServiceBuilder.this.executor, WorkloadServiceBuilder.this.scenario, list);
                this.samples = samples;
            }

            @Override
            protected boolean terminationRequired() {
                return getFinishedSamples() >= samples;
            }
        }

        private class SharedSamplesCountWorkloadService extends WorkloadService {
            
            private final AtomicInteger samplesLeft;

            private SharedSamplesCountWorkloadService(ImmutableList<? extends Flushable> list, AtomicInteger samples) {
                super(WorkloadServiceBuilder.this.executor, WorkloadServiceBuilder.this.scenario, list);
                this.samplesLeft = samples;
            }

            @Override
            protected boolean terminationRequired() {
                int s;
                while (true) {
                    s =  samplesLeft.get();
                    if (s > 0) {
                        if (samplesLeft.compareAndSet(s, --s) ) {
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
    }
}