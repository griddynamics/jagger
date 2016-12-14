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

package com.griddynamics.jagger;

import com.griddynamics.jagger.coordinator.zookeeper.IZookeeper;
import com.griddynamics.jagger.coordinator.zookeeper.Zoo;
import com.griddynamics.jagger.coordinator.zookeeper.ZooKeeperFactory;
import com.griddynamics.jagger.exception.TechnicalException;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static com.griddynamics.jagger.coordinator.zookeeper.Zoo.znode;

public class ZooKeeperServer implements AttendantServer, Runnable {
    private static final Logger log = LoggerFactory.getLogger(ZooKeeperServer.class);

    private ZooKeeperServerWrapper zooKeeperServer;
    private Zoo zoo;

    private Properties startupProperties;

    private String endpoint;
    private int sessionTimeout;
    private String rootNode;

    @Override
    public void run() {
        log.info("Starting ZooKeeper...");
        QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
        try {
            quorumConfiguration.parseProperties( startupProperties );
        } catch(Exception e) {
            throw new TechnicalException(e);
        }

        zooKeeperServer = new ZooKeeperServerWrapper();
        final ServerConfig configuration = new ServerConfig();
        configuration.readFrom( quorumConfiguration );

        new Thread() {
            public void run() {
                try {
                    zooKeeperServer.runFromConfig(configuration);
                } catch (IOException e) {
                    log.error("ZooKeeper Failed", e);
                }
            }
        }.start();
    }

    @Override
    public void terminate() {
        log.debug("termination signal received");
        if(zooKeeperServer != null) {
            try {
                if (zoo != null) {
                    zoo.root().child(rootNode).removeWithChildren();
                }
                zooKeeperServer.shutdown();
            } catch (Exception e) {
                log.warn("Error during zookeeper termination. Message: {}", e.getMessage());
            }
        }
    }

    public void initialize() {
        ZooKeeperFactory zooKeeperFactory = new ZooKeeperFactory();
        zooKeeperFactory.setConnectString(endpoint);
        zooKeeperFactory.setSessionTimeout(sessionTimeout);
        log.info("Connect to {} endpoint with timeout {}", endpoint, sessionTimeout);

        IZookeeper zooKeeper = null;
        try {
            zooKeeper = zooKeeperFactory.create();
            zoo = new Zoo(zooKeeperFactory.create());
            // TODO: timeout only 40000. svi.
            if (zoo.root().hasChild(rootNode)) {
                log.info("ZNode [" + rootNode + "] was found.");
                zoo.root().child(rootNode).removeWithChildren();
                log.info("ZNode [" + rootNode + "] with children nodes were removed.");
            }

            zoo.root().createChild(znode().withPath(rootNode));
            log.info("ZNode [" + rootNode + "] was created.");
        } finally {
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    public void setStartupProperties(Properties startupProperties) {
        this.startupProperties = startupProperties;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setRootNode(String rootNode) {
        this.rootNode = rootNode.substring(1, rootNode.length());
    }
}
