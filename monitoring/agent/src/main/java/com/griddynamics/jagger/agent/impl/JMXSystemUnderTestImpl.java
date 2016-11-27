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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.agent.model.*;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.util.ConfigurableExecutor;
import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.griddynamics.jagger.util.Units.bytesToMiB;

/**
 * User: vshulga
 * Date: 7/5/11
 * Time: 4:45 PM
 * <p/>
 * JMX implementation of service for retrieving information about GC.
 * Could be used for multiple systems monitoring on the same host (portsForMonitoring).
 */
public class JMXSystemUnderTestImpl implements SystemUnderTestService {

    private final static Logger log = LoggerFactory.getLogger(JMXSystemUnderTestImpl.class);

    private static final Collection<String> OLD_GEN_GC =
            ImmutableSet.of("MarkSweepCompact", "PS MarkSweep", "ConcurrentMarkSweep", "G1 Old Generation");

    private AgentContext context;
    private String name;
    private Map<String, MBeanServerConnection> connections = Maps.newHashMap();
    private ConfigurableExecutor executor;
    private JmxConnector jmxConnector;
    private Future<Map<String, MBeanServerConnection>> future;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Required
    public void setExecutor(ConfigurableExecutor executor) {
        this.executor = executor;
    }

    @Required
    public void setJmxConnector(JmxConnector jmxConnector) {
        this.jmxConnector = jmxConnector;
    }

    @Override
    public Map<String, SystemUnderTestInfo> getInfo() {

        if (connections.size() == 0) {
            connections = getEstablishedJmxConnections();
        }

        if (connections.size() > 0) {
            Map<String, SystemUnderTestInfo> result = Maps.newHashMap();

            for (String identifier : connections.keySet()) {
                result.put(identifier, analyzeJVM(identifier));
            }
            return result;
        }
        else {
            log.warn("JMX connection is not initialized. Skip");
            return null;
        }
    }

    @Override
    public Map<String, Map<String,String>> getSystemProperties() {

        if (connections.size() == 0) {
            connections = getEstablishedJmxConnections();
        }

        if (connections.size() > 0) {
            Map<String, Map<String,String>> result = Maps.newHashMap();
            try {
                for (String identifier : connections.keySet()){
                    MBeanServerConnection connection = connections.get(identifier);
                    RuntimeMXBean runtimeMXBean = ManagementFactory.newPlatformMXBeanProxy(connection,
                                                                                         ManagementFactory.RUNTIME_MXBEAN_NAME,
                                                                                         RuntimeMXBean.class);
                    result.put(identifier, runtimeMXBean.getSystemProperties());
                }
            }catch (IOException ex){
                log.error("Error in JMXSigarMonitorController.analyzeJVM", ex);
            }
            return result;
        }
        else {
            log.warn("JMX connection is not initialized. Skip");
            return null;
        }
    }

    @Override
    public void setContext(AgentContext context) {
        this.context = context;
    }

    public void init() {
        future = executor.submit(new Callable<Map<String, MBeanServerConnection>>() {

                @Override
                public Map<String, MBeanServerConnection> call() throws Exception {
                    return jmxConnector.connect(name);
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


    private SystemUnderTestInfo analyzeJVM(String identifier) {
        SystemUnderTestInfo result = new SystemUnderTestInfo(identifier);
        long minor_time = 0;
        long major_time = 0;
        long minor_units = 0;
        long major_units = 0;
        try {
            MBeanServerConnection connection = connections.get(identifier);


            // File descriptors
            try {
                UnixOperatingSystemMXBean unixOperatingSystemMXBean = ManagementFactory.newPlatformMXBeanProxy(connection,
                        ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, UnixOperatingSystemMXBean.class);
                result.putSysUTEntry(DefaultMonitoringParameters.OPEN_FILE_DESCRIPTOR_COUNT,
                        (double) unixOperatingSystemMXBean.getOpenFileDescriptorCount());
            } catch (Exception e) {
                log.warn("Can not get count of open file descriptors from '{}' ", identifier, e);
            }

            // Heap
            MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(connection,
                    ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

            result.putSysUTEntry(DefaultMonitoringParameters.HEAP_MEMORY_MAX, bytesToMiB(heapMemoryUsage.getMax()));
            result.putSysUTEntry(DefaultMonitoringParameters.HEAP_MEMORY_COMMITTED, bytesToMiB(heapMemoryUsage.getCommitted()));
            result.putSysUTEntry(DefaultMonitoringParameters.HEAP_MEMORY_USED, bytesToMiB(heapMemoryUsage.getUsed()));
            result.putSysUTEntry(DefaultMonitoringParameters.HEAP_MEMORY_INIT, bytesToMiB(heapMemoryUsage.getInit()));

            MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
            result.putSysUTEntry(DefaultMonitoringParameters.NON_HEAP_MEMORY_MAX, bytesToMiB(nonHeapMemoryUsage.getMax()));
            result.putSysUTEntry(DefaultMonitoringParameters.NON_HEAP_MEMORY_COMMITTED, bytesToMiB(nonHeapMemoryUsage.getCommitted()));
            result.putSysUTEntry(DefaultMonitoringParameters.NON_HEAP_MEMORY_USED, bytesToMiB(nonHeapMemoryUsage.getUsed()));
            result.putSysUTEntry(DefaultMonitoringParameters.NON_HEAP_MEMORY_INIT, bytesToMiB(nonHeapMemoryUsage.getInit()));

            // Garbage collection
            Set<ObjectName> srvMemMgrNames = connection.queryNames(
                    new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*"), null);
            for (ObjectName gcMgr : srvMemMgrNames) {
                try {
                    GarbageCollectorMXBean gcMgrBean =
                            ManagementFactory.newPlatformMXBeanProxy(connection, gcMgr.toString(),
                                    GarbageCollectorMXBean.class);

                    if (gcMgrBean.isValid()) {
                        boolean majorCollector = OLD_GEN_GC.contains(gcMgrBean.getName());
                        if (majorCollector) {
                            major_units += gcMgrBean.getCollectionCount();
                            major_time += gcMgrBean.getCollectionTime();
                        } else {
                            minor_units += gcMgrBean.getCollectionCount();
                            minor_time += gcMgrBean.getCollectionTime();
                        }
                    }
                } catch (IOException e) {
                    log.error("Error in JMXSigarMonitorController.analyzeJVM", e);
                }
            }
            result.putSysUTEntry(DefaultMonitoringParameters.JMX_GC_MAJOR_TIME, (double) major_time);
            result.putSysUTEntry(DefaultMonitoringParameters.JMX_GC_MAJOR_UNIT, (double) major_units);
            result.putSysUTEntry(DefaultMonitoringParameters.JMX_GC_MINOR_TIME, (double) minor_time);
            result.putSysUTEntry(DefaultMonitoringParameters.JMX_GC_MINOR_UNIT, (double) minor_units);

            // Threads
            ThreadMXBean threadMXBean = ManagementFactory.newPlatformMXBeanProxy(connection,
                    ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
            result.putSysUTEntry(DefaultMonitoringParameters.THREAD_COUNT, (double) threadMXBean.getThreadCount());
            result.putSysUTEntry(DefaultMonitoringParameters.THREAD_PEAK_COUNT, (double) threadMXBean.getPeakThreadCount());

            // Custom
            if (context != null) {
                List<JmxMetric> jmxMetricList = (List<JmxMetric>) context.getProperty(AgentContext.AgentContextProperty.JMX_METRICS);
                if (jmxMetricList != null) {
                    for (JmxMetric metric : jmxMetricList) {
                        try {
                            Object num = connection.getAttribute(metric.getObjectName(), metric.getAttributeName());
                            if (num instanceof Number) {
                                result.putSysUTEntry(metric.getParameter(), ((Number) num).doubleValue());
                            } else {
                                log.warn("Return value from MBean: '{}'; attribute: '{}'; is not a Number",
                                        metric.getObjectName(), metric.getAttributeName());
                            }
                        } catch (Exception e) {
                            log.error("Can not get from MBean: '{}'; attribute: '{}'", new Object[] {metric.getObjectName(), metric.getAttributeName(), e});
                        }
                    }
                }
            }

        } catch (Exception ee) {
            log.error("Error in JMXSigarMonitorController.analyzeJVM", ee);
        }

        return result;

    }
}
