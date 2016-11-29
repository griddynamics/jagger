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

import com.griddynamics.jagger.coordinator.AbstractRemoteExecutor;
import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.NodeCommandExecutionListener;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.async.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static com.griddynamics.jagger.coordinator.zookeeper.Zoo.znode;

public class ZooKeeperRemoteExecutor extends AbstractRemoteExecutor {
    private static final Logger log = LoggerFactory.getLogger(ZooKeeperRemoteExecutor.class);

    private final NodeId nodeId;
    private final ZNode rootNode;

    public ZooKeeperRemoteExecutor(NodeId nodeId, ZNode rootNode) {
        this.nodeId = nodeId;
        this.rootNode = rootNode;
    }

    @Override
    public <C extends Command<R>, R extends Serializable> void run(final C command, final NodeCommandExecutionListener<C> listener, final AsyncCallback<R> callback) {
        ZNode commandNode = rootNode.child(nodeId.getType().name().toLowerCase()).child(nodeId.getIdentifier()).child(command.getClass().getName());
        ZNode queueNode = commandNode.child("queue");
        ZNode resultNode = commandNode.child("result");

        final ZNode outputNode = resultNode.createChild(znode().persistentSequential());


        outputNode.addNodeWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                log.debug("command {} execution done", command);
                CommandExecutionResult result = outputNode.getObject(CommandExecutionResult.class);
                switch (result.getStatus()) {
                    case SUCCEEDED:
                        log.debug("success");
                        callback.onSuccess((R) result.getResult());
                        break;
                    case FAILED:
                        Throwable e = result.getException();
                        log.error("fail", e);
                        callback.onFailure(e);
                        break;
                    default:
                        throw new IllegalStateException("Unknown status");
                }

                outputNode.removeWithChildren();

            }
        });
        queueNode.createChild(
                znode()
                        .persistentSequential()
                        .withDataObject(new QueueEntry<C, R>(command, listener, outputNode.getPath()))
        );
        log.debug("command {} is ready to be executed", command);
    }

}
