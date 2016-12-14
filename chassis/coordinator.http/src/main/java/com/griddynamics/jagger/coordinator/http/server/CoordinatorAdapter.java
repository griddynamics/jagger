/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
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

package com.griddynamics.jagger.coordinator.http.server;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.SettableFuture;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.coordinator.async.AsyncCallback;
import com.griddynamics.jagger.coordinator.async.AsyncRunner;
import com.griddynamics.jagger.coordinator.async.FutureAsyncCallback;
import com.griddynamics.jagger.coordinator.http.AbstractProxyWorker;
import com.griddynamics.jagger.coordinator.http.DefaultPackExchanger;
import com.griddynamics.jagger.coordinator.http.PackExchanger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * Adapts {@link Coordinator} to {@link TransportHandler}.
 *
 * @author Mairbek Khadikov
 */
public class CoordinatorAdapter implements TransportHandler {
    private static final Logger log = LoggerFactory.getLogger(CoordinatorAdapter.class);
    private static final StatusChangeListener LOGGING_LISTENER = new StatusChangeListener() {
        @Override
        public void onNodeStatusChanged(NodeId nodeId, NodeStatus status) {
            log.debug("node {} status changed {}", nodeId, status);
        }

        @Override
        public void onCoordinatorDisconnected() {
            log.warn("Coordinator disconnected");
        }

        @Override
        public void onCoordinatorConnected() {
            log.debug("Coordinator connected");
        }
    };

    private final Map<NodeId, DefaultPackExchanger> exchangers = Maps.newConcurrentMap();
    private final Coordinator coordinator;
    private final Executor executor;

    public CoordinatorAdapter(Coordinator coordinator, Executor executor) {
        this.coordinator = coordinator;
        this.executor = executor;
    }

    @Override
    public PackExchanger getExchanger(NodeId node) {
        return exchangers.get(node);
    }

    @Override
    public void registerNode(final NodeId node, Set<Qualifier<Command<Serializable>>> qualifiers) {
        log.debug("Register node {} with qualifiers {} requested", node, qualifiers);

        Worker worker = createProxyWorker(qualifiers);

        coordinator.registerNode(Coordination.emptyContext(node), Sets.newHashSet(worker), LOGGING_LISTENER);

        exchangers.put(node, createPackRunner(node));

        log.debug("Node {} registered", node);
    }


    private DefaultPackExchanger createPackRunner(final NodeId node) {
        return new DefaultPackExchanger(executor, new AsyncRunner<Command<Serializable>, Serializable>() {
            @Override
            public void run(Command<Serializable> command, AsyncCallback<Serializable> callback) {
                log.debug("Going send command {} to delegate coordinator", command);
                RemoteExecutor remoteExecutor = coordinator.getExecutor(node);
                remoteExecutor.run(command, Coordination.doNothing(), callback);
            }
        });
    }

    private AbstractProxyWorker createProxyWorker(final Set<Qualifier<Command<Serializable>>> qualifiers) {
        return new AbstractProxyWorker(qualifiers) {

            @Override
            protected Serializable handleCommand(Command<Serializable> command, NodeContext nodeContext) {
                log.debug("Handling command {} for node {}", command, nodeContext);
                AsyncRunner<Command<Serializable>, Serializable> packExchanger = exchangers.get(nodeContext.getId());
                FutureAsyncCallback<Serializable> callback = FutureAsyncCallback.create();
                packExchanger.run(command, callback);

                SettableFuture<Serializable> future = callback.getFuture();
                try {
                    log.debug("Waiting for command completion");
                    return future.get();
                } catch (InterruptedException e) {
                    throw Throwables.propagate(e);
                } catch (ExecutionException e) {
                    throw Throwables.propagate(e);
                }
            }
        };
    }


}
