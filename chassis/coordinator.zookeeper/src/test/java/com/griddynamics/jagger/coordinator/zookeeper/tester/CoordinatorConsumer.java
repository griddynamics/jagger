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

import com.google.common.collect.Sets;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.coordinator.zookeeper.ZNode;
import com.griddynamics.jagger.coordinator.zookeeper.Zoo;
import com.griddynamics.jagger.coordinator.zookeeper.ZooKeeperFactory;
import com.griddynamics.jagger.coordinator.zookeeper.ZookeeperCoordinator;

import java.util.Set;
import java.util.concurrent.Executors;

public class CoordinatorConsumer {

    public static void main(String[] args) {
        ZooKeeperFactory zooKeeperFactory = new ZooKeeperFactory();
        zooKeeperFactory.setConnectString("localhost:2181");
        zooKeeperFactory.setSessionTimeout(1000000);
        Zoo zoo = new Zoo(zooKeeperFactory.create());
        ZNode root = zoo.root().child(args[0]);

        Coordinator coordinator = new ZookeeperCoordinator(root, Executors.newSingleThreadExecutor());

        Set<Worker> workers = Sets.newLinkedHashSet();
        workers.add(new PrintValWorker());
        NodeId id = NodeId.kernelNode("my-kernel");
        try {
            coordinator.registerNode(Coordination.emptyContext(id), workers, new StatusChangeListener() {
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

        while (true) {
        }

    }
}
