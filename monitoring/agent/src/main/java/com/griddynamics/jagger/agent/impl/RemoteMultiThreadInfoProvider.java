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

package com.griddynamics.jagger.agent.impl;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.diagnostics.thread.sampling.ThreadInfoProvider;
import com.griddynamics.jagger.util.ConfigurableExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author Alexey Kiselyov
 *         Date: 30.08.11
 */
public class RemoteMultiThreadInfoProvider implements ThreadInfoProvider {
    private static final Logger log = LoggerFactory.getLogger(RemoteMultiThreadInfoProvider.class);



    private ConfigurableExecutor executor;
    private JmxConnector jmxConnector;
    private Future<Map<String, MBeanServerConnection>> future;

    private Map<String, MBeanServerConnection> connections = Maps.newConcurrentMap();

    @Override
    public Set<String> getIdentifiersSuT() {
        return connections.keySet();
    }

    @Override
    public Map<String, ThreadInfo[]> getThreadInfo() {
        if (connections.size() == 0) {
            connections = getEstablishedJmxConnections();
        }

        if (connections.size() > 0) {

            long startTimeLog = System.currentTimeMillis();
            Map<String, ThreadInfo[]> result = Maps.newHashMap();
            for (String serviceURL : this.connections.keySet()) {
                try {
                    ObjectName srvThrdName = new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME);
                    long[] threadIDs = (long[]) this.connections.get(serviceURL).getAttribute(srvThrdName, "AllThreadIds");
                    MBeanServerConnection mBeanServerConnection = this.connections.get(serviceURL);
                    CompositeData[] compositeDatas = (CompositeData[]) (mBeanServerConnection.invoke(srvThrdName,
                            "getThreadInfo", new Object[]{threadIDs, Integer.MAX_VALUE}, new String[]{"[J", "int"}));
                    ThreadInfo[] threadInfos = new ThreadInfo[compositeDatas.length];
                    for (int i = 0; i < compositeDatas.length; i++) {
                        threadInfos[i] = ThreadInfo.from(compositeDatas[i]);
                    }
                    result.put(serviceURL, threadInfos);
                } catch (JMException e) {
                    log.error("JMException", e);
                } catch (IOException e) {
                    log.error("IOException", e);
                }
            }
            log.debug("collected threadInfos through jmx for profiling on agent: time {} ms", System.currentTimeMillis() - startTimeLog);
            return result;
        }
        else {
            log.warn("JMX connection is not initialized. Skip");
            return Collections.emptyMap();
        }
    }

    public void init(){
        future = executor.submit(new Callable<Map<String, MBeanServerConnection>>() {

            @Override
            public Map<String, MBeanServerConnection> call() throws Exception {
                return jmxConnector.connect("");
            }
        });
    }

    private Map<String, MBeanServerConnection> getEstablishedJmxConnections() {
        Map<String, MBeanServerConnection> result;

        if ((future == null) || (!future.isDone())) {
            return Collections.emptyMap();
        }

        try {
            result = future.get();
        } catch (Exception ex) {
            // connection failed
            future = null;
            log.error("Failed to establish JMX connection");
            return Collections.emptyMap();
        }

        return result;
    }

    @Required
    public void setExecutor(ConfigurableExecutor executor) {
        this.executor = executor;
    }

    @Required
    public void setJmxConnector(JmxConnector jmxConnector) {
        this.jmxConnector = jmxConnector;
    }
}
