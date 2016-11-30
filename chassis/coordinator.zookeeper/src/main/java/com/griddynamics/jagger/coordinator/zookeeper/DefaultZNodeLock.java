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

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.griddynamics.jagger.coordinator.zookeeper.Zoo.znode;

/**
 * Lock implementation as described at {@see http://zookeeper.apache.org/doc/r3.1.2/recipes.html#sc_recipes_Locks}.
 * <span color="#ff0000">In progress. May cause a deadlocks!</span>
 */
public class DefaultZNodeLock implements ZNodeLock {
    private final ZNode node;

    private final String lockPath;

    private ZNode lockNode;

    public DefaultZNodeLock(ZNode node) {
        this(node, "lock");
    }

    public DefaultZNodeLock(ZNode node, String lockPath) {
        this.node = node;
        this.lockPath = lockPath;
    }

    private ZNode lockDir() {
        return node.child(lockPath);
    }

    @Override
    public void makeLockable() {
        node.createChild(znode().withPath(lockPath));
    }

    @Override
    public boolean isLockable() {
        return node.hasChild(lockPath);
    }

    @Override
    public void lock() {
        while (true) {
            ZNode node = lockDir().createChild(znode().ephemeralSequential());


            final String currentNodeName = node.getShortPath();
            int currentFlag = Integer.valueOf(currentNodeName);

            final List<ZNode> children = lockDir().children();
            int lowestNodeVal = Integer.MAX_VALUE;
            int nextNodeFlag = -1;
            String nextNodePath = null;

            for (ZNode child : children) {
                String childPath = child.getShortPath();
                int childFlag = Integer.valueOf(childPath);

                if (childFlag < lowestNodeVal) {
                    lowestNodeVal = childFlag;
                }

                if (childFlag < currentFlag && childFlag > nextNodeFlag) {
                    nextNodeFlag = childFlag;
                    nextNodePath = childPath;
                }
            }

            if (currentFlag == lowestNodeVal) {
                lockNode = node;
                break;
            }

            final CountDownLatch signal = new CountDownLatch(1);
            boolean hasChild = lockDir().hasChild(nextNodePath, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    signal.countDown();
                }
            });

            if (hasChild) {
                lockNode = node;

                try {
                    signal.await();
                    break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            node.remove();
        }
    }

    @Override
    public void unlock() {
        if (lockNode == null) {
            return;
        }

        lockNode.remove();

    }
}
