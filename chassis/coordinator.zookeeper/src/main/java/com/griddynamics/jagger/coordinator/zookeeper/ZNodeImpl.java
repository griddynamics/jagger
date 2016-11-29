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
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.griddynamics.jagger.util.SerializationUtils.deserialize;
import static com.griddynamics.jagger.util.SerializationUtils.serialize;

/**
 * Default implementation of {@link ZNode}
 */
public class ZNodeImpl implements ZNode {
    private final IZookeeper zooKeeper;
    private final String path;
    private Logger logger = LoggerFactory.getLogger(ZNodeImpl.class);

    public ZNodeImpl(IZookeeper zooKeeper, String path) {
        this.zooKeeper = zooKeeper;
        this.path = path;
    }

    @Override
    public void addNodeWatcher(Watcher watcher) {
        try {
            zooKeeper.exists(path, watcher);
        } catch (KeeperException e) {
            throw new ZooException("Exception during watcher add", e);
        } catch (InterruptedException e) {
            throw new ZooException("Exception during watcher add", e);
        }
    }

    @Override
    public <T> T getObject(Class<T> clazz) {
        Object object = deserialize(getData());
        if (!clazz.isInstance(object)) {
            logger.warn("getObject returns {} instead of {}", object != null ? object.getClass().getName() : "NULL", clazz.getName());
            throw new IllegalStateException(object + " is not a type of  " + clazz);
        }

        return clazz.cast(object);
    }

    @Override
    public String getString() {
        return new String(getData());

    }

    private byte[] getData() {
        try {
            return zooKeeper.getData(path, false, null);
        } catch (KeeperException e) {
            throw new ZooException("Exception during data retrieving", e);
        } catch (InterruptedException e) {
            throw new ZooException("Exception during data retrieving", e);
        }
    }

    @Override
    public void setString(String data) {
        setData(data.getBytes());
    }

    @Override
    public void setData(byte[] data) {
        try {
            zooKeeper.setData(path, data, -1);
        } catch (KeeperException e) {
            throw new ZooException("Exception during data filling", e);
        } catch (InterruptedException e) {
            throw new ZooException("Exception during data filling", e);
        }
    }

    @Override
    public void setObject(Object object) {
        setData(serialize(object));
    }

    @Override
    public boolean hasChild(String path, Watcher watcher) {
        try {
            return zooKeeper.exists(this.path + "/" + path, watcher) != null;
        } catch (KeeperException e) {
            throw new ZooException("Exception during existence check", e);
        } catch (InterruptedException e) {
            throw new ZooException("Exception during existence check", e);
        }
    }

    @Override
    public ZNode createChild(ZNodeParameters parameters) {
        String path = checkNotNull(parameters.getPath());
        parameters.withPath(this.path + "/" + path);
        return ZNodeCreator.of(zooKeeper).create(parameters);
    }

    @Override
    public ZNode child(String path) {
        return new ZNodeImpl(zooKeeper, this.path + "/" + path);
    }

    @Override
    public void removeChild(String path) {
        try {
            zooKeeper.delete(this.path + "/" + path, -1);
        } catch (InterruptedException e) {
            throw new ZooException("Exception during existence check", e);
        } catch (KeeperException e) {
            throw new ZooException("Exception during existence check", e);
        }
    }

    @Override
    public List<ZNode> children() {
        return children(null);
    }

    @Override
    public List<ZNode> children(Watcher watcher) {
        try {
            List<ZNode> result = new LinkedList<ZNode>();
            String nodePath = path;
            if (nodePath.length() == 0) {
            	nodePath = "/";
            }
			List<String> children = zooKeeper.getChildren(nodePath, watcher);
            for (String child : children) {
                result.add(new ZNodeImpl(zooKeeper, path + "/" + child));
            }
            return result;
        } catch (KeeperException e) {
            throw new ZooException("Exception during children checking", e);
        } catch (InterruptedException e) {
            throw new ZooException("Exception during children checking", e);
        }

    }

    @Override
    public List<ZNode> firstLevelChildren() {
        return filterFirstLevel(children());
    }

    private List<ZNode> filterFirstLevel(List<ZNode> children) {
        List<ZNode> result = Lists.newArrayList();
        int rootCount = getPath().split("/").length;

        for (ZNode child : children) {
            if (child.getPath().split("/").length == (rootCount + 1)) {
                result.add(child);
            }
        }
        return result;
    }

    @Override
    public List<ZNode> firstLevelChildren(Watcher watcher) {
        return filterFirstLevel(children(watcher));
    }

    // not the best solution!
    @Override
    public void remove() {
        try {
            zooKeeper.delete(this.path, -1);
        } catch (InterruptedException e) {
            throw new ZooException("Exception during node removal", e);
        } catch (KeeperException e) {
            throw new ZooException("Exception during node removal", e);
        }
    }

    @Override
    public void removeWithChildren() {

        for (ZNode zNode : children()) {
            zNode.removeWithChildren();
        }
       
        remove();
    }

    @Override
    public boolean hasChild(String childPath) {
        try {
            return zooKeeper.exists(this.path + "/" + childPath, false) != null;
        } catch (KeeperException e) {
            throw new ZooException("Exception child retrieval", e);
        } catch (InterruptedException e) {
            throw new ZooException("Exception child retrieval", e);
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getShortPath() {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    @Override
    public ZNodeLock obtainLock() {
        ZNodeLock lock = new DefaultZNodeLock(this);
        if (!lock.isLockable()) {
            lock.makeLockable();
        }

        return lock;
    }

    @Override
    public void addChildrenWatcher(Watcher watcher) {
        children(watcher);
    }

    @Override
    public boolean exists() {
        try {
            return zooKeeper.exists(this.path, false) != null;
        } catch (Exception e) {
            throw new ZooException("Exception during watcher add", e);
        }
    }

    @Override
	public String toString() {
		return "ZNodeImpl [path=" + path + "]";
	}
    
}
