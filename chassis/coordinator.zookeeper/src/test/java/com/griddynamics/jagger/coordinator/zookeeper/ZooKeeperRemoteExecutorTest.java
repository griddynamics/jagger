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

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.coordinator.async.Async;
import org.apache.zookeeper.Watcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;

import static com.griddynamics.jagger.coordinator.zookeeper.Zoo.znode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class ZooKeeperRemoteExecutorTest {
    public static final String IDENTIFIER = "identifier";

    private ZooKeeperRemoteExecutor remoteExecutor;
    private ZNode rootNode;
    private ZNode kernelsNode;
    private ZNode kernelNode;
    private ZNode commandNode;
    private ZNode queueNode;
    private ZNode resultNode;
    private NodeId id = NodeId.of(NodeType.KERNEL, IDENTIFIER);

    @BeforeMethod
    public void setUp() throws Exception {
        rootNode = mock(ZNode.class);
        kernelsNode = mock(ZNode.class);
        kernelNode = mock(ZNode.class);
        commandNode = mock(ZNode.class);
        queueNode = mock(ZNode.class);
        resultNode = mock(ZNode.class);
        when(rootNode.child(NodeType.KERNEL.name().toLowerCase())).thenReturn(kernelsNode);
        when(kernelsNode.child(IDENTIFIER)).thenReturn(kernelNode);
        when(kernelNode.child(TestCommand.class.getName())).thenReturn(commandNode);
        when(commandNode.child("queue")).thenReturn(queueNode);
        when(commandNode.child("result")).thenReturn(resultNode);


        remoteExecutor = new ZooKeeperRemoteExecutor(id, rootNode);
    }

    @Test
    public void testScheduling() throws Exception {
        ZNode outputNode = mock(ZNode.class);

        when(resultNode.createChild(znode().persistentSequential())).thenReturn(outputNode);
        String path = "/out/1";
        when(outputNode.getPath()).thenReturn(path);

        TestCommand command = new TestCommand();
        QueueEntry<TestCommand, Serializable> entry = new QueueEntry<TestCommand, Serializable>(command, null, path);

        remoteExecutor.run(command, null, Async.doNothing());

        verify(outputNode).addNodeWatcher(any(Watcher.class));
        verify(queueNode).createChild(znode().persistentSequential().withDataObject(entry));
    }

    public static class TestCommand implements Command<Serializable> {

        @Override
        public String getSessionId() {
            return null;
        }

    }
}
