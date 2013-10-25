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
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.*;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.util.Nothing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public abstract class AbstractDistributor<T extends Task> implements TaskDistributor<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractDistributor.class);

    @Override
    public Service distribute(final ExecutorService executor, final String sessionId, final String taskId, final Multimap<NodeType, NodeId> availableNodes, final Coordinator coordinator, final T task, final DistributionListener listener, NodeContext nodeContext) {
        Set<Qualifier<?>> qualifiers = getQualifiers();

        final Map<NodeId, RemoteExecutor> remotes = Maps.newHashMap();

        for (NodeId nodeId : availableNodes.get(NodeType.KERNEL)) {

            boolean canRunTheCommand = coordinator.canExecuteCommands(nodeId, qualifiers);

            if (canRunTheCommand) {
                remotes.put(nodeId, coordinator.getExecutor(nodeId));
            } else {
                log.debug("command type {} are not supported by kernel {}",
                        qualifiers, nodeId);
            }
        }

        if (remotes.isEmpty()) {
            throw new NodeNotFound("Nodes not found to distribute the task");
        }

        final Service service = performDistribution(executor, sessionId, taskId, task, remotes, availableNodes, coordinator, nodeContext);
        return new ForwardingService() {

            @Override
            public ListenableFuture<State> start() {

                ListenableFuture<Nothing> runListener = Futures.makeListenable(executor.submit(new Callable<Nothing>() {
                    @Override
                    public Nothing call() {
                        listener.onDistributionStarted(sessionId, taskId, task, remotes.keySet());
                        return Nothing.INSTANCE;
                    }
                }));


                return Futures.chain(runListener, new Function<Nothing, ListenableFuture<State>>() {
                    @Override
                    public ListenableFuture<State> apply(Nothing input) {
                        return doStart();
                    }
                });
            }


            private ListenableFuture<State> doStart() {
                return super.start();
            }

            @Override
            protected Service delegate() {
                return service;
            }

            @Override
            public ListenableFuture<State> stop() {
                ListenableFuture<State> stop = super.stop();

                return Futures.chain(stop, new Function<State, ListenableFuture<State>>() {
                    @Override
                    public ListenableFuture<State> apply(final State input) {

                        final SettableFuture<State> result = SettableFuture.create();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    listener.onTaskDistributionCompleted(sessionId, taskId, task);
                                } finally {
                                    result.set(input);
                                }
                            }
                        });
                        return result;
                    }
                });
            }

        };
    }

    protected abstract Set<Qualifier<?>> getQualifiers();

    protected abstract Service performDistribution(ExecutorService executor, String sessionId, String taskId, T task, Map<NodeId, RemoteExecutor> remotes, Multimap<NodeType, NodeId> availableNodes, Coordinator coordinator, NodeContext nodeContext);
}
