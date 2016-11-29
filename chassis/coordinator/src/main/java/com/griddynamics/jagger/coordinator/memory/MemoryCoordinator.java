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

package com.griddynamics.jagger.coordinator.memory;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.coordinator.async.AsyncCallback;
import com.griddynamics.jagger.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Performs in memory coordination. <b>Works only for the local mode</b>.
 *
 * @author Alexey Kiselyov
 */
public class MemoryCoordinator implements Coordinator {

    private static final Logger log = LoggerFactory.getLogger(MemoryCoordinator.class);

    private Map<NodeId, Pair<NodeContext, Set<CommandExecutor<?, ?>>>> nodes = Maps.newConcurrentMap();
    private ExecutorService executorService;

    private static class InstanceHolder {
        private static MemoryCoordinator instance = new MemoryCoordinator();
    }

    private MemoryCoordinator() {
    }

    public static MemoryCoordinator getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public void registerNode(NodeContext nodeContext, Set<Worker> workers, final StatusChangeListener listener) {
        log.info("Going to register node {} with {} workers", nodeContext.getId(), workers.size());
        Set<CommandExecutor<?, ?>> executors = Sets.newHashSet();
        Set<Qualifier<?>> qualifiers = Sets.newHashSet();

        for (Worker worker : workers) {
            for (CommandExecutor<?, ?> executor : worker.getExecutors()) {
                Qualifier<?> qualifier = executor.getQualifier();
                if (qualifiers.contains(qualifier)) {
                    throw new CoordinatorException("Executor for qualifier " + qualifier + " is already registered");
                }

                executors.add(executor);
            }
        }
        nodes.put(nodeContext.getId(), Pair.of(nodeContext, executors));
    }

    @Override
    public RemoteExecutor getExecutor(final NodeId nodeId) throws CoordinatorException {
        final Pair<NodeContext, Set<CommandExecutor<?, ?>>> nodePair = nodes.get(nodeId);

        if (nodePair == null) {
            throw new CoordinatorException("Node " + nodeId.getIdentifier() + " hasn't registered");
        }

        return new AbstractRemoteExecutor() {
            @Override
            public <C extends Command<R>, R extends Serializable> void run(final C command, final NodeCommandExecutionListener<C> listener, final AsyncCallback<R> callback) {
                final NodeContext nodeContext = nodePair.getFirst();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCommandExecutionStarted(command, nodeContext);
                        try {
                            R execute = getCommandExecutor(command).execute(command, nodeContext);
                            listener.onCommandExecuted(command);
                            callback.onSuccess(execute);
                        } catch (Throwable throwable) {
                            log.error("Error during command execution!", throwable);
                            callback.onFailure(throwable);
                        }
                    }
                });
                return;
            }

            //create Future instance, which can interrupt coordinator thread by some reason(f.e. by time)
            @Override
            public <C extends Command<R>, R extends Serializable> Future<R> run(final C command, final NodeCommandExecutionListener<C> listener) {
                final NodeContext nodeContext = nodePair.getFirst();
                return executorService.submit(new Callable<R>() {
                    @Override
                    public R call() {
                        listener.onCommandExecutionStarted(command, nodeContext);
                        R execute = getCommandExecutor(command).execute(command, nodeContext);
                        listener.onCommandExecuted(command);
                        return execute;
                    }
                });
            }

            private <C extends Command<R>, R extends Serializable> CommandExecutor<C, R> getCommandExecutor(C command){
                for (CommandExecutor<?, ?> commandExecutor : nodePair.getSecond()) {
                    final CommandExecutor<C, R> executor = (CommandExecutor<C, R>) commandExecutor;

                    if (executor.getQualifier().equals(Qualifier.of(command))) {
                        return executor;
                    }
                }
                throw new CoordinatorException("Command " + command + " is not available on " + nodeId.getIdentifier());
            }
        };
    }

    @Override
    public boolean canExecuteCommands(NodeId nodeId, Set<Qualifier<?>> qualifiers) throws CoordinatorException {
        if (qualifiers == null || qualifiers.isEmpty()) {
            return true;
        }

        Set<CommandExecutor<?, ?>> commandExecutors = nodes.get(nodeId).getSecond();
        if (commandExecutors == null) throw new CoordinatorException("Node with id " + nodeId + " is not found");
        Set<Qualifier<?>> qualifiersCopy = Sets.newHashSet(qualifiers);
        for (CommandExecutor<?, ?> commandExecutor : commandExecutors) {
            if (qualifiersCopy.remove(commandExecutor.getQualifier()) && qualifiersCopy.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<NodeId> getAvailableNodes(final NodeType type) throws CoordinatorException {
        return Sets.filter(nodes.keySet(), new Predicate<NodeId>() {
            @Override
            public boolean apply(NodeId input) {
                return input.getType().equals(type);
            }
        });
    }

    @Override
    public void waitForReady() {
        // ready!!!
    }

    @Override
    public void initialize() {
        // init!
    }

    @Override
    public void waitForInitialization() {
        // ready!
    }

    @Required
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
