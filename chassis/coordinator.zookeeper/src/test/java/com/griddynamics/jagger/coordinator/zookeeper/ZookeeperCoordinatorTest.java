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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.coordinator.Qualifier;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import static com.griddynamics.jagger.coordinator.zookeeper.ZooMock.mockZnode;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ZookeeperCoordinatorTest {
    private ZookeeperCoordinator coordinator;

    private ZNode root;
    private ZNode master;
    private ZNode kernel;
    private ZNode agent;

    @BeforeMethod
    public void setUp() throws Exception {
        root = mockZnode();
        when(root.hasChild(CoordinationUtil.STATUSES_NODE_NAME)).thenReturn(true);
        master = mockZnode();
        when(root.child("master")).thenReturn(master);
        kernel = mockZnode();
        when(root.child("kernel")).thenReturn(kernel);
        agent = mockZnode();
        when(root.child("agent")).thenReturn(agent);

        coordinator = new ZookeeperCoordinator(root, Executors.newSingleThreadExecutor());
    }

    @Test
    public void nodeAvailability() throws Exception {
        ZNode availableNode = mockZnode();
        when(availableNode.hasChild("available")).thenReturn(true);
        when(availableNode.getShortPath()).thenReturn("av");

        ZNode notAvailableNode = mockZnode();
        when(notAvailableNode.hasChild("available")).thenReturn(false);
        when(notAvailableNode.getShortPath()).thenReturn("nav");
        List<ZNode> children = Lists.newLinkedList();
        children.add(availableNode);
        children.add(availableNode);
        when(kernel.children()).thenReturn(children);

        Collection<NodeId> availableNodes = coordinator.getAvailableNodes(NodeType.KERNEL);

        assertTrue(availableNodes.contains(NodeId.kernelNode("av")));
        assertFalse(availableNodes.contains(NodeId.kernelNode("nav")));
    }

    @Test
    public void nodeAvailabilityWhenNoNodeIsAvailable() throws Exception {
        List<ZNode> children = Lists.newLinkedList();
        when(kernel.children()).thenReturn(children);

        Collection<NodeId> availableNodes = coordinator.getAvailableNodes(NodeType.KERNEL);
        assertTrue(availableNodes.isEmpty());
    }

    @Test
    public void canExecuteCommandsWhen() throws Exception {
        ZNode node = mockZnode();
        ZNode firstCommandNode = mockZnode();
        ZNode secondCommandNode = mockZnode();

        NodeId id = NodeId.kernelNode("identifier");

        when(kernel.child(id.getIdentifier())).thenReturn(node);
        when(kernel.hasChild(id.getIdentifier())).thenReturn(true);
        when(node.hasChild("available")).thenReturn(true);

        when(node.child(TestCommandOne.class.getName())).thenReturn(firstCommandNode);
        when(node.hasChild(TestCommandOne.class.getName())).thenReturn(true);

        when(node.child(TestCommandTwo.class.getName())).thenReturn(secondCommandNode);
        when(node.hasChild(TestCommandTwo.class.getName())).thenReturn(true);

        Set<Qualifier<?>> qualifiers = Sets.newHashSet();
        qualifiers.add(Qualifier.of(TestCommandOne.class));
        qualifiers.add(Qualifier.of(TestCommandTwo.class));
        assertTrue(coordinator.canExecuteCommands(id, qualifiers));
    }

    private static class TestCommandOne implements Command<Serializable> {

        @Override
        public String getSessionId() {
            return null;
        }
    }

    private static class TestCommandTwo implements Command<Serializable> {

        @Override
        public String getSessionId() {
            return null;
        }

    }


}
