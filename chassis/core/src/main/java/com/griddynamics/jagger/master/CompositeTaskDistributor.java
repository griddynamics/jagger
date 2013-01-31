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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.util.Futures;
import com.griddynamics.jagger.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Responsible for composite task distribution.
 *
 * @author Mairbek Khadikov
 */
public class CompositeTaskDistributor implements TaskDistributor<CompositeTask> {
    private static Logger log = LoggerFactory.getLogger(CompositeTaskDistributor.class);
    private static final int START_TIMEOUT = 300000;
    private static final int TERMINATION_TIMEOUT = 1800000;

    private DistributorRegistry distributorRegistry;
    private TaskIdProvider taskIdProvider;

    public void setDistributorRegistry(DistributorRegistry distributorRegistry) {
        this.distributorRegistry = distributorRegistry;
    }

    public void setTaskIdProvider(TaskIdProvider taskIdProvider) {
        this.taskIdProvider = taskIdProvider;
    }

    @Override
    public Service distribute(final ExecutorService executor, final String sessionId, final String taskId, final Multimap<NodeType, NodeId> availableNodes, final Coordinator coordinator, final CompositeTask task, final DistributionListener listener) {
        log.debug("Composite task {} with id {} distribute configuration started", task, taskId);

        Function<CompositableTask, Service> convertToRunnable = new Function<CompositableTask, Service>() {
            @Override
            @SuppressWarnings("unchecked")
            public Service apply(CompositableTask task) {
                TaskDistributor taskDistributor = distributorRegistry.getTaskDistributor(task.getClass());
                task.setParentTaskId(taskId);
                String childTaskId = taskIdProvider.getTaskId();
                return taskDistributor.distribute(executor, sessionId, childTaskId, availableNodes, coordinator, task, listener);
            }
        };

        final List<Service> leading = Lists.newLinkedList(Lists.transform(task.getLeading(), convertToRunnable));
        final List<Service> attendant = Lists.newLinkedList(Lists.transform(task.getAttendant(), convertToRunnable));

        return new AbstractDistributionService(executor) {

            final List<Future<State>> leadingTerminateFutures = Lists.newLinkedList();

            @Override
            protected void run() throws Exception {


                List<Future<State>> futures = Lists.newLinkedList();
                for (Service service : Iterables.concat(leading, attendant)) {
                    ListenableFuture<State> future = service.start();
                    futures.add(future);
                }

                for (Future<State> future : futures) {
                    State state = Futures.get(future, START_TIMEOUT);
                    log.debug("Service started with state {}", state);
                }

                while (isRunning()) {
                    if (activeLeadingTasks() == 0) {
                        break;
                    }

                    TimeUtils.sleepMillis(500);
                }
            }

            private int activeLeadingTasks() {
                int result = 0;

                Iterator<Service> it = leading.iterator();
                while (it.hasNext()) {
                    Service service = it.next();
                    if (service.state() == State.TERMINATED || service.state() == State.FAILED) {
                        log.debug("State {}", service.state());
                        leadingTerminateFutures.addAll(requestTermination(Collections.singleton(service), true));
                        it.remove();
                    } else {
                        result++;
                    }
                }

                return result;
            }

            @Override
            protected void shutDown() throws Exception {
                stopAll();
                super.shutDown();
            }

            private void awaitLeading(List<Future<State>> leadingFutures) {
                for (Future<State> future : leadingFutures) {
                    Futures.get(future, TERMINATION_TIMEOUT);
                }
            }

            private List<Future<State>> requestTermination(Iterable<Service> services, boolean leading) {
                List<Future<State>> leadingFutures = Lists.newLinkedList();
                for (Service service : services) {
                    if (service.state() == State.FAILED) {
                        if (leading) {
                            throw new IllegalStateException("Failed to run child distributor: " + service);
                        } else {
                            log.warn("Attendant service {} failed", service);
                            continue;
                        }
                    }

                    leadingFutures.add(service.stop());
                }
                return leadingFutures;
            }

            private void stopAll() {
                List<Future<State>> leadingFutures = requestTermination(leading, true);
                List<Future<State>> attendantFutures = requestTermination(attendant, false);
                leadingFutures.addAll(leadingTerminateFutures);

                awaitLeading(leadingFutures);
                awaitAttendant(attendantFutures);
            }

            private void awaitAttendant(List<Future<State>> attendantFutures) {
                for (Future<State> future : attendantFutures) {
                    try {
                        future.get(TERMINATION_TIMEOUT, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        log.warn("Attendant task timeout", e);
                    } catch (InterruptedException e) {
                        log.warn("Interrupted", e);
                    } catch (ExecutionException e) {
                        log.warn("Attendant task failed", e);
                    }
                }
            }

            @Override
            public String toString() {
                return CompositeTaskDistributor.class.getName() + " distributor";
            }
        };
    }
}
