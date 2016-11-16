/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

package com.griddynamics.jagger.storage.fs.hdfs;

import com.griddynamics.jagger.AttendantServer;
import com.griddynamics.jagger.storage.fs.hdfs.utils.HadoopUtils;
import com.griddynamics.jagger.util.BlockingBean;
import com.griddynamics.jagger.util.ThreadExecutorUtil;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class HDFSDatanodeServer implements BlockingBean, AttendantServer {

    private static Logger log = LoggerFactory.getLogger(HDFSDatanodeServer.class);

    private DataNode dataNode;

    private volatile boolean ready = false;

    private static HDFSDatanodeServer instance;
    private final static Object lock = new Object();
    private Properties startupProperties;

    public static HDFSDatanodeServer create() throws Exception {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new HDFSDatanodeServer();
                    try {
                        instance.start();
                    } catch (Exception e) {
                        log.warn("Error during start. Thread id {}", Thread.currentThread().getId(), e);
                    }
                }
            }
        }
        return instance;
    }

    public void start() throws Exception {
        ThreadExecutorUtil.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                log.info("Starting DataNode...");

                while (!ready) {
                    try {
                        if (startupProperties != null) {
                            dataNode = DataNode.createDataNode(null, HadoopUtils.toConfiguration(startupProperties));
                            ready = true;
                        } else Thread.sleep(10000);
                    } catch (Exception e) {
                        log.warn("Failed start DataNode: {}", e);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                            log.warn("Interrupted");
                            return;
                        }
                    }
                }

                /*new Thread() {
                    public void run() {
                        dataNode.run();
                    }
                }.start();*/

                log.info("DataNode started");
            }
        });
    }

    public void shutdown() {
        if (dataNode != null) {
            dataNode.shutdown();
        } else {
            ready = true;
        }
    }

    public void setStartupProperties(Properties startupProperties) {
        this.startupProperties = startupProperties;
    }

    @Override
    public boolean isBlock() {
        return !ready;
    }

    @Override
    public void waitForReady() {
    }

    @Override
    public void initialize() {
        //
    }

    @Override
    public void run() {
        //
    }

    @Override
    public void terminate() {
        //
    }
}
