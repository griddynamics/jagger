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

package com.griddynamics.jagger.diagnostics.thread.sampling;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.griddynamics.jagger.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;

/**
 * @author Alexey Kiselyov
 *         Date: 30.08.11
 */
public class RemoteMultiThreadInfoProvider implements ThreadInfoProvider {
    private static final Logger log = LoggerFactory.getLogger(RemoteMultiThreadInfoProvider.class);
    private static final String JMX_URL_TEMPLATE = "service:jmx:rmi:///jndi/rmi://%s/jmxrmi";

    private String jmxServices;
    private Map<String, JMXConnector> connector = Maps.newConcurrentMap();
    private Map<String, MBeanServerConnection> mbs = Maps.newConcurrentMap();


    @Override
    public Set<String> getIdentifiersSuT() {
        return Sets.newHashSet(this.jmxServices.split(","));
    }

    @Override
    public Map<String, ThreadInfo[]> getThreadInfo() {
        long startTimeLog = System.currentTimeMillis();
        Map<String, ThreadInfo[]> result = Maps.newHashMap();
        for (String serviceURL : this.connector.keySet()) {
            try {
                ObjectName srvThrdName = new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME);
                long[] threadIDs = (long[]) this.mbs.get(serviceURL).getAttribute(srvThrdName, "AllThreadIds");
                MBeanServerConnection mBeanServerConnection = this.mbs.get(serviceURL);
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

    public String getJmxServices() {
        return jmxServices;
    }

    @Required
    public void setJmxServices(String jmxServices) {

        if (this.jmxServices != null && this.jmxServices.equals(jmxServices))
            return; // nothing to do. no changes


        try {
            if (this.jmxServices != null) {
                for (JMXConnector jmxConnector : connector.values()) {
                    try {
                        jmxConnector.close();
                    } catch (IOException e) {
                        log.error("Can't close old jmx connection {}", jmxConnector.getConnectionId(), e);
                    }
                }
                this.connector.clear();
                this.mbs.clear();
            }
            this.jmxServices = jmxServices;

            Set<String> jmxServicesSet = getIdentifiersSuT();
            for (String service : jmxServicesSet) {

                JMXConnector connect = JMXConnectorFactory.connect(
                        new JMXServiceURL(String.format(JMX_URL_TEMPLATE, service)));
                this.connector.put(service, connect);
                this.mbs.put(service, connect.getMBeanServerConnection());
            }
        } catch (MalformedURLException e) {
            log.error("MalformedURLException", e);
            throw new TechnicalException(e);
        } catch (IOException e) {
            log.error("IOException", e);
        }
    }
}
