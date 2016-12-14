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

package com.griddynamics.jagger.coordinator.zookeeper;

import static com.griddynamics.jagger.coordinator.zookeeper.Zoo.znode;

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.CommandExecutor;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.CoordinatorException;
import com.griddynamics.jagger.coordinator.NodeCommandExecutionListener;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeStatus;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.coordinator.Qualifier;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.coordinator.StatusChangeListener;
import com.griddynamics.jagger.coordinator.Worker;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZookeeperCoordinator implements Coordinator {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperCoordinator.class);

    private static final long INITIALIZATION_SLEEP_PERIOD = 1000L;

    private final Object lock = new Object();

    private final ZNode rootNode;

    private final Executor executor;


    public ZookeeperCoordinator(ZNode rootNode, Executor executor) {
        this.rootNode = rootNode;
        this.executor = executor;
    }

    @Override
    public void registerNode(NodeContext nodeContext, Set<Worker> workers, final StatusChangeListener listener) throws CoordinatorException {
        log.info("Going to register node {} with {} workers", nodeContext.getId(), workers.size());

        ZNode typeNode = rootNode.child(CoordinationUtil.nodeNameOf(nodeContext.getId().getType()));
        if (typeNode.hasChild(nodeContext.getId().getIdentifier())) {
            typeNode.child(nodeContext.getId().getIdentifier()).removeWithChildren();
        }
        ZNode node = typeNode.createChild(znode().withPath(nodeContext.getId().getIdentifier()));

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

        for (CommandExecutor<?, ?> executor : executors) {
            registerExecutor(nodeContext, executor, node);
        }

        rootNode.addNodeWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.Disconnected) {
                    listener.onCoordinatorDisconnected();
                }

                if (event.getState() == Event.KeeperState.SyncConnected) {
                    listener.onCoordinatorConnected();
                }
            }
        });


        ZNode statuses = rootNode.child(CoordinationUtil.STATUSES_NODE_NAME);

        statuses.createChild(znode().ephemeralSequential().withDataObject(nodeContext.getId()));

        Lock lock = new ReentrantLock();

        lock.lock();
        try {
            Collection<NodeId> nodeIds = Sets.newHashSet();
            StatusWatcher statusWatcher = new StatusWatcher(statuses, lock, nodeIds, listener);
            List<ZNode> nodes = statuses.children(statusWatcher);
            for (ZNode zNode : nodes) {
                nodeIds.add(zNode.getObject(NodeId.class));
            }
        } finally {
            lock.unlock();
        }

        node.createChild(znode().withPath(CoordinationUtil.AVAILABLE_NODE_NAME));
    }

    @Override
    public RemoteExecutor getExecutor(NodeId nodeId) {
        return new ZooKeeperRemoteExecutor(nodeId, rootNode);
    }

    @Override
    public boolean canExecuteCommands(NodeId nodeId, Set<Qualifier<?>> qualifiers) {
        ZNode typeNode = rootNode.child(CoordinationUtil.nodeNameOf(nodeId.getType()));
        String identifier = nodeId.getIdentifier();
        if (!typeNode.hasChild(identifier)) {
            throw new CoordinatorException("Node with id " + nodeId + " is not found");
        }

        ZNode node = typeNode.child(identifier);

        if (!node.hasChild(CoordinationUtil.AVAILABLE_NODE_NAME)) {
            return false;
        }
        for (Qualifier<?> qualifier : qualifiers) {
            if (!node.hasChild(nodeNameOf(qualifier))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<NodeId> getAvailableNodes(NodeType type) {
        Set<NodeId> result = Sets.newHashSet();
        ZNode typeNode = rootNode.child(CoordinationUtil.nodeNameOf(type));
        for (ZNode node : typeNode.children()) {
            if (node.hasChild(CoordinationUtil.AVAILABLE_NODE_NAME)) {
                result.add(NodeId.of(type, node.getShortPath()));
            }
        }
        return result;
    }

    @Override
    public void waitForReady() {
        while (true) {
            try {
                rootNode.exists();
                break;
            } catch (Throwable e) {
                // do nothing
            }
            try {
                Thread.sleep(INITIALIZATION_SLEEP_PERIOD);
                log.info("Znode structure is not initialized. Waiting {} ms", INITIALIZATION_SLEEP_PERIOD);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted", e);
            }
        }
    }

    private <C extends Command<R>, R extends Serializable> void registerExecutor(final NodeContext nodeContext, final CommandExecutor<C, R> executor, ZNode node) {
        final ZNode executorNode = node.createChild(znode().withPath(nodeNameOf(executor.getQualifier())));
        final ZNode queueNode = executorNode.createChild(znode().withPath("queue"));
        executorNode.createChild(znode().withPath("result"));

        log.debug("Created znodes for executor {}", executorNode.getPath());

        queueNode.addChildrenWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() != Event.EventType.NodeChildrenChanged) {
                    return;
                }

                synchronized (lock) {
                    if (log.isDebugEnabled()) {
                        log.debug("Children changed {} event type {}", queueNode.getPath(), event.getType());
                    }

                    List<QueueEntry<C, R>> entries = getEntries(queueNode, this);

                    for (final QueueEntry<C, R> entry : entries) {
                        Runnable run = new Runnable() {

                            @Override
                            public void run() {
                                executeCommand(executor, executorNode, entry, nodeContext);
                            }
                        };

                        ZookeeperCoordinator.this.executor.execute(run);
                    }
                }

            }
        });
    }

    private static String nodeNameOf(Qualifier<?> qualifier) {
        return qualifier.getClazz().getName();
    }

    private static <C extends Command<R>, R extends Serializable> void executeCommand(CommandExecutor<C, R> executor, ZNode executorNode, final QueueEntry<C, R> entry, final NodeContext nodeContext) {
        String relativePath = entry.getResultPath().substring(executorNode.getPath().length() + 1);
        final ZNode output = executorNode.child(relativePath);
        final NodeCommandExecutionListener<C> listener = entry.getListener();

        try {
            C command = entry.getCommand();
            listener.onCommandExecutionStarted(command, nodeContext);
            R result = executor.execute(command, nodeContext);
            log.debug("Command {} executed", command);
            listener.onCommandExecuted(command);
            output.setObject(CommandExecutionResult.success(result));
        } catch (Throwable throwable) {
            // todo add fail event
            log.error("error during task execution", throwable);
            output.setObject(CommandExecutionResult.fail(throwable));
        }
    }

    private static <C extends Command<R>, R extends Serializable> List<QueueEntry<C, R>> getEntries(ZNode queueNode, Watcher watcher) {
        List<QueueEntry<C, R>> result = Lists.newLinkedList();
        List<ZNode> children = queueNode.firstLevelChildren(watcher);

        Collections.sort(children, new Comparator<ZNode>() {
            @Override
            public int compare(ZNode first, ZNode second) {
                return first.getPath().compareTo(second.getPath());
            }
        });

        for (ZNode child : children) {
            QueueEntry<C, R> entry = child.getObject(QueueEntry.class);
            child.remove();
            result.add(entry);
        }
        return result;
    }

    @Override
    public void initialize() {
        log.info("Going to initialize required znode structure in zookeeper");
        for (NodeType type : NodeType.values()) {
            String child = CoordinationUtil.nodeNameOf(type);
            if (!rootNode.hasChild(child)) {
                rootNode.createChild(znode().withPath(child));
                log.info("Created Zookeeper node {}", child);
            }
        }
    
        if (!rootNode.hasChild(CoordinationUtil.STATUSES_NODE_NAME)) {
            rootNode.createChild(znode().withPath(CoordinationUtil.STATUSES_NODE_NAME));
            log.info("Created Zookeeper node {}", CoordinationUtil.STATUSES_NODE_NAME);
        }
        log.info("Successfully initialized");
    }

    @Override
    public void waitForInitialization() {
        log.info("Waiting for coordination znode structure structure initialization");
        while (true) {
            boolean initialized = rootNode.exists() && rootNode.hasChild(CoordinationUtil.STATUSES_NODE_NAME);

            if (initialized) {
                log.info("Coordination znode structure initialized");
                break;
            }

            try {
                Thread.sleep(INITIALIZATION_SLEEP_PERIOD);
                log.info("Znode structure is not initialized. Waiting {} ms", INITIALIZATION_SLEEP_PERIOD);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted", e);
            }
        }
    }

    private static class StatusWatcher implements Watcher {
        private final ZNode node;
        private final Lock lock;
        private final StatusChangeListener statusChangeListener;
        private Collection<NodeId> currentIds;

        private StatusWatcher(ZNode node, Lock lock, Collection<NodeId> currentIds, StatusChangeListener statusChangeListener) {
            this.node = node;
            this.lock = lock;
            this.currentIds = currentIds;
            this.statusChangeListener = statusChangeListener;
        }

        @Override
        public void process(WatchedEvent event) {
            if (event.getType() != Event.EventType.NodeChildrenChanged) {
                return;
            }


            Runnable runnable = new Runnable() {
                public void run() {
                    lock.lock();

                    try {
                        List<ZNode> children = node.children();
                        Collection<NodeId> newIds = Sets.newHashSet();
                        for (ZNode child : children) {
                            newIds.add(child.getObject(NodeId.class));
                        }

                        Collection<NodeId> copy = Sets.newHashSet(newIds);
                        newIds.removeAll(currentIds);
                        currentIds.removeAll(copy);

                        for (NodeId newId : newIds) {
                            statusChangeListener.onNodeStatusChanged(newId, NodeStatus.AVAILABLE);
                        }

                        for (NodeId newId : currentIds) {
                            statusChangeListener.onNodeStatusChanged(newId, NodeStatus.DISCONNECTED);
                        }

                        currentIds = copy;
                    } finally {
                        lock.unlock();
                    }
                }
            };

            new Thread(runnable).run();
            node.addChildrenWatcher(this);
        }
    }
}
