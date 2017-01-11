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

package com.griddynamics.jagger.coordinator.zookeeper.tester;

import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.coordinator.zookeeper.ZNode;
import com.griddynamics.jagger.coordinator.zookeeper.Zoo;
import com.griddynamics.jagger.coordinator.zookeeper.ZooKeeperFactory;
import com.griddynamics.jagger.coordinator.zookeeper.ZookeeperCoordinator;
import com.griddynamics.jagger.util.UrlClassLoaderHolder;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CoordinatorProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ZooKeeperFactory zooKeeperFactory = new ZooKeeperFactory();
        zooKeeperFactory.setConnectString("localhost:2181");
        zooKeeperFactory.setSessionTimeout(1000);
        Zoo zoo = new Zoo(zooKeeperFactory.create());

        ZNode root = zoo.root().child(args[0]);

        Coordinator coordinator = new ZookeeperCoordinator(root, Executors.newSingleThreadExecutor(), new UrlClassLoaderHolder());

        NodeId nodeId = NodeId.masterNode("master");
        Set<Worker> workers = Collections.singleton(Coordination.emptyWorker());
        try {
            coordinator.registerNode(Coordination.emptyContext(nodeId), workers, new StatusChangeListener() {
                @Override
                public void onNodeStatusChanged(NodeId nodeId, NodeStatus status) {
                    System.out.println(nodeId + " " + status);
                }

                @Override
                public void onCoordinatorDisconnected() {
                    System.out.println("Coordinator disconnected");
                }

                @Override
                public void onCoordinatorConnected() {
                    System.out.println("Coordinator connected");
                }
            });
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Set<NodeId> availableNodes = null;
        try {
            availableNodes = coordinator.getAvailableNodes(NodeType.KERNEL);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        int n = 0;
        for (int i = 0; i < 1000; i++) {
            for (NodeId id : availableNodes) {
                System.out.println("Scheduled " + n);
                final int finalN = n;
//                coordinator.sendCommand(id, new PrintVal("" + n), Lists.<KernelCommandExecutionListener<PrintVal>>newLinkedList(), new AsyncCallback() {
//                    @Override
//                    public void onSuccess() {
//                        System.out.println("Success! " + finalN);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//
//                    }
//                });
                RemoteExecutor executor = null;
                try {
                    executor = coordinator.getExecutor(id);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                Future<String> s = executor.run(new PrintVal("" + n), Coordination.<PrintVal>doNothing());
                System.out.println(s.get());
                n++;
            }
        }

        while (true) {
        }

    }

}
