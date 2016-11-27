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

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Adapts {@link org.apache.zookeeper.ZooKeeper} to {@link IZookeeper}.
 */
public class DefaultZookeeper implements IZookeeper {

    private static final Logger log = LoggerFactory.getLogger(DefaultZookeeper.class);

    private String connectString;
    private int sessionTimeout;
    private Watcher watcher;
    private long sessionId;
    private byte[] sessionPassword;

    private int reconnectPeriod;

    private ZooKeeper delegate;

    public DefaultZookeeper(String connectString, int sessionTimeout, Watcher watcher, int reconnectPeriod) throws IOException {
        this(connectString, sessionTimeout, watcher, 0, null, reconnectPeriod);
    }

    public DefaultZookeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPassword, int reconnectPeriod) throws IOException {
        this.connectString = connectString;
        this.sessionTimeout = sessionTimeout;
        this.watcher = watcher;
        this.sessionId = sessionId;
        this.sessionPassword = sessionPassword;

        this.reconnectPeriod = reconnectPeriod;

        getDelegate();
    }

    private ZooKeeper getDelegate() {

        while(!isDelegateAlive()) {
            try {
                if(sessionPassword == null && sessionId == 0) {
                    delegate = new ZooKeeper(connectString, sessionTimeout, watcher);
                }else {
                    delegate = new ZooKeeper(connectString, sessionTimeout, watcher, sessionId, sessionPassword);
                }
            } catch (Exception e) {
                log.error("Failed to connect to ZooKeeper");
                try {
                    Thread.sleep(reconnectPeriod);
                } catch (InterruptedException ie) {}
            }
        }

        return delegate;
    }

    private boolean isDelegateAlive() {
        if(delegate == null) {
            return false;
        } else {
            try {
                ZooKeeper.States state = delegate.getState();
                return state.isAlive();
            } catch (Exception e) {
                return false;
            }
        }
    }

    @Override
    public long getSessionId() {
        return getDelegate().getSessionId();
    }

    @Override
    public byte[] getSessionPasswd() {
        return getDelegate().getSessionPasswd();
    }

    @Override
    public int getSessionTimeout() {
        return getDelegate().getSessionTimeout();
    }

    @Override
    public void addAuthInfo(String scheme, byte[] auth) {
        getDelegate().addAuthInfo(scheme, auth);
    }

    @Override
    public void register(Watcher watcher) {
        getDelegate().register(watcher);
    }

    @Override
    public void close() throws InterruptedException {
        getDelegate().close();
    }

    @Override
    public String create(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws KeeperException, InterruptedException {
        return getDelegate().create(path, data, acl, createMode);
    }

    @Override
    public void create(String path, byte[] data, List<ACL> acl, CreateMode createMode, AsyncCallback.StringCallback cb, Object ctx) {
        getDelegate().create(path, data, acl, createMode, cb, ctx);
    }

    @Override
    public void delete(String path, int version) throws InterruptedException, KeeperException {
        getDelegate().delete(path, version);
    }

    @Override
    public void delete(String path, int version, AsyncCallback.VoidCallback cb, Object ctx) {
        getDelegate().delete(path, version, cb, ctx);
    }

    @Override
    public Stat exists(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return getDelegate().exists(path, watcher);
    }

    @Override
    public Stat exists(String path, boolean watch) throws KeeperException, InterruptedException {
        return getDelegate().exists(path, watch);
    }

    @Override
    public void exists(String path, Watcher watcher, AsyncCallback.StatCallback cb, Object ctx) {
        getDelegate().exists(path, watcher, cb, ctx);
    }

    @Override
    public void exists(String path, boolean watch, AsyncCallback.StatCallback cb, Object ctx) {
        getDelegate().exists(path, watch, cb, ctx);
    }

    @Override
    public byte[] getData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return getDelegate().getData(path, watcher, stat);
    }

    @Override
    public byte[] getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return getDelegate().getData(path, watch, stat);
    }

    @Override
    public void getData(String path, Watcher watcher, AsyncCallback.DataCallback cb, Object ctx) {
        getDelegate().getData(path, watcher, cb, ctx);
    }

    @Override
    public void getData(String path, boolean watch, AsyncCallback.DataCallback cb, Object ctx) {
        getDelegate().getData(path, watch, cb, ctx);
    }

    @Override
    public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
        return getDelegate().setData(path, data, version);
    }

    @Override
    public void setData(String path, byte[] data, int version, AsyncCallback.StatCallback cb, Object ctx) {
        getDelegate().setData(path, data, version, cb, ctx);
    }

    @Override
    public List<ACL> getACL(String path, Stat stat) throws KeeperException, InterruptedException {
        return getDelegate().getACL(path, stat);
    }

    @Override
    public void getACL(String path, Stat stat, AsyncCallback.ACLCallback cb, Object ctx) {
        getDelegate().getACL(path, stat, cb, ctx);
    }

    @Override
    public Stat setACL(String path, List<ACL> acl, int version) throws KeeperException, InterruptedException {
        return getDelegate().setACL(path, acl, version);
    }

    @Override
    public void setACL(String path, List<ACL> acl, int version, AsyncCallback.StatCallback cb, Object ctx) {
        getDelegate().setACL(path, acl, version, cb, ctx);
    }

    @Override
    public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return getDelegate().getChildren(path, watcher);
    }

    @Override
    public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {
        return getDelegate().getChildren(path, watch);
    }

    @Override
    public void getChildren(String path, Watcher watcher, AsyncCallback.ChildrenCallback cb, Object ctx) {
        getDelegate().getChildren(path, watcher, cb, ctx);
    }

    @Override
    public void getChildren(String path, boolean watch, AsyncCallback.ChildrenCallback cb, Object ctx) {
        getDelegate().getChildren(path, watch, cb, ctx);
    }

    @Override
    public List<String> getChildren(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return getDelegate().getChildren(path, watcher, stat);
    }

    @Override
    public List<String> getChildren(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return getDelegate().getChildren(path, watch, stat);
    }

    @Override
    public void getChildren(String path, Watcher watcher, AsyncCallback.Children2Callback cb, Object ctx) {
        getDelegate().getChildren(path, watcher, cb, ctx);
    }

    @Override
    public void getChildren(String path, boolean watch, AsyncCallback.Children2Callback cb, Object ctx) {
        getDelegate().getChildren(path, watch, cb, ctx);
    }

    @Override
    public void sync(String path, AsyncCallback.VoidCallback cb, Object ctx) {
        getDelegate().sync(path, cb, ctx);
    }

    @Override
    public ZooKeeper.States getState() {
        return getDelegate().getState();
    }

    @Override
    public String toString() {
        return "Wrapper for " + getDelegate().toString();
    }
}
