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
package com.griddynamics.jagger.kernel.agent.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.agent.model.SystemUnderTestInfo;
import com.griddynamics.jagger.agent.model.coherence.CoherenceCacheMetricSource;
import com.griddynamics.jagger.agent.model.coherence.CoherenceCacheMonitoringParameter;
import com.griddynamics.jagger.agent.model.coherence.CoherenceMonitoringParametersProvider;
import com.griddynamics.jagger.agent.model.coherence.CoherenceNode;
import com.griddynamics.jagger.agent.model.coherence.CoherenceNodeMonitoringParameter;
import com.griddynamics.jagger.kernel.agent.model.SystemUnderTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import javax.management.InvalidAttributeValueException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Vladimir Kondrashchenko
 */
public class CoherenceSystemUnderTestServiceImpl implements SystemUnderTestService, InitializingBean {

    private static Logger log = LoggerFactory.getLogger(CoherenceSystemUnderTestServiceImpl.class);

    private static final String JMX_URL_TEMPLATE = "service:jmx:rmi:///jndi/rmi://%s/jmxrmi";
    private static final String COHERENCE_CACHE_SERVICE_NAMES_TEMPLATE = "Coherence:type=Cache,service=%s,name=*,nodeId=%d,tier=%s,*";
    private static final String COHERENCE_SERVICE_NAMES_TEMPLATE = "Coherence:type=Service,name=%s,nodeId=%d";
    private static final String COHERENCE_NODE_NAMES_TEMPLATE = "Coherence:type=Node,nodeId=%d";
    private static final String COHERENCE_NODES = "Coherence:type=Node,*";

    private CoherenceMonitoringParametersProvider monitoringParametersProvider;
    private String jmxServices;

    private String jmxHostPort;
    private MBeanServerConnection mBeanServerConnection;
    private Iterable<MonitoringParameter> monitoringParameters;

    public void setMonitoringParametersProvider(CoherenceMonitoringParametersProvider monitoringParametersProvider) {
        this.monitoringParametersProvider = monitoringParametersProvider;
    }

    @Required
    public void setJmxServices(String jmxServices) {
        this.jmxServices = jmxServices;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!isEnabled()) return;

        String[] jmxServicePorts = this.jmxServices.split(";");
        if (jmxServicePorts.length < 1) {
            throw new IllegalArgumentException("No JMX services specified: " + jmxServices);
        }

        jmxHostPort = jmxServicePorts[0];
        String jmxServiceUrl = String.format(JMX_URL_TEMPLATE, jmxHostPort);
        log.info("Accessing Coherence JMX server at {}.", jmxServiceUrl);

        JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(jmxServiceUrl));
        mBeanServerConnection = connector.getMBeanServerConnection();

        monitoringParametersProvider.setCoherenceNodes(getCoherenceNodes(mBeanServerConnection));
        monitoringParameters = monitoringParametersProvider.generateMonitoringParameters();
    }

    @Override
    public Map<String, SystemUnderTestInfo> getInfo() {
        if (!isEnabled()) {
            return Maps.newHashMap();
        }

        long startTime = System.currentTimeMillis();
        String sutUrl = "Coherence metrics (url = " + jmxHostPort + ")";
        SystemUnderTestInfo systemUnderTestInfo = new SystemUnderTestInfo(sutUrl);
        for (MonitoringParameter monitoringParameter : monitoringParameters) {
            try {
                Double result = null;
                if (monitoringParameter instanceof CoherenceCacheMonitoringParameter) {
                    CoherenceCacheMonitoringParameter coherenceCacheMonitoringParameter = (CoherenceCacheMonitoringParameter) monitoringParameter;
                    if (coherenceCacheMonitoringParameter.getMetricSource() == CoherenceCacheMetricSource.CACHE) {
                        result = collectCacheMetricValue(coherenceCacheMonitoringParameter);
                    } else if (coherenceCacheMonitoringParameter.getMetricSource() == CoherenceCacheMetricSource.SERVICE) {
                        result = collectServiceMetricValue(coherenceCacheMonitoringParameter);
                    } else {
                        log.error("{} metric source is not supported.", coherenceCacheMonitoringParameter.getMetricSource());
                    }
                } else if (monitoringParameter instanceof CoherenceNodeMonitoringParameter) {
                    CoherenceNodeMonitoringParameter coherenceNodeMonitoringParameter = (CoherenceNodeMonitoringParameter) monitoringParameter;
                    result = collectNodeMetricValue(coherenceNodeMonitoringParameter);
                } else {
                    log.error("{} monitoring parameter is not supported.", monitoringParameter.getClass().getCanonicalName());
                }

                if (result != null) {
                    systemUnderTestInfo.putSysUTEntry(monitoringParameter, result);
                }
            } catch (Exception e) {
                log.error("Unable to collect metrics for monitoring param " + monitoringParameter, e);
            }
        }
        log.info("Collected {} Coherence metrics in {} ms.", systemUnderTestInfo.getSysUTInfo().size(),(System.currentTimeMillis() - startTime));
        Map<String, SystemUnderTestInfo> returnValue = Maps.newHashMap();
        returnValue.put(sutUrl, systemUnderTestInfo);
        return returnValue;
    }

    private Collection<CoherenceNode> getCoherenceNodes(MBeanServerConnection mBeanServerConnection) {
        Collection<CoherenceNode> nodes = Lists.newArrayList();

        try {
            ObjectName nodesObjectName = new ObjectName(COHERENCE_NODES);
            Set<ObjectName> nodeObjectNames = mBeanServerConnection.queryNames(nodesObjectName, null);

            for (ObjectName nodeObjectName : nodeObjectNames) {
                log.info("Found Coherence node {}.", nodeObjectName);

                int id = ((Number) mBeanServerConnection.getAttribute(nodeObjectName, "Id")).intValue();
                String name = (String) mBeanServerConnection.getAttribute(nodeObjectName, "MemberName");
                String role = (String) mBeanServerConnection.getAttribute(nodeObjectName, "RoleName");

                CoherenceNode node = new CoherenceNode(id, name, role);
                nodes.add(node);
            }
        } catch (Exception e) {
            log.error("Unable to retrieve Coherence nodes list.", e);
        }

        return nodes;
    }

    private double getCoherenceCacheMetric(MBeanServerConnection mBeanServerConnection, CoherenceNode node,
                                                String cacheServiceName, String metricAttributeName) throws Exception {
        String tier = node.isStorage() ? "back" : "front";
        String objectName = String.format(COHERENCE_CACHE_SERVICE_NAMES_TEMPLATE, cacheServiceName, node.getId(), tier);

        return getCoherenceMetric(mBeanServerConnection, objectName, metricAttributeName);
    }

    private double getCoherenceServiceMetric(MBeanServerConnection mBeanServerConnection, CoherenceNode node,
                                                String cacheServiceName, String metricAttributeName) throws Exception {
        String objectName = String.format(COHERENCE_SERVICE_NAMES_TEMPLATE, cacheServiceName, node.getId());

        return getCoherenceMetric(mBeanServerConnection, objectName, metricAttributeName);
    }

    private double getCoherenceNodeMetric(MBeanServerConnection mBeanServerConnection, CoherenceNode node, String metricAttributeName) throws Exception {
        String objectName = String.format(COHERENCE_NODE_NAMES_TEMPLATE, node.getId());

        return getCoherenceMetric(mBeanServerConnection, objectName, metricAttributeName);
    }

    private double getCoherenceMetric(MBeanServerConnection mBeanServerConnection, String objectName, String metricAttributeName) throws Exception {
        ObjectName cacheServiceObjectName = new ObjectName(objectName);
        Set<ObjectName> cacheObjectNames = mBeanServerConnection.queryNames(cacheServiceObjectName, null);

        double result = 0;
        for (ObjectName cacheObjectName : cacheObjectNames) {
            Object value = mBeanServerConnection.getAttribute(cacheObjectName, metricAttributeName);
            if (value instanceof Number) {
                double longValue = ((Number) value).doubleValue();
                result += longValue;
            } else {
                throw new InvalidAttributeValueException("Value of '" + cacheObjectName + "' is expected to a number, but it is '" + value + "'.");
            }
        }
        return result;
    }

    private double collectCacheMetricValue(CoherenceCacheMonitoringParameter monitoringParameter) throws Exception {
        double result = 0;
        for (String cacheNameInDomain : monitoringParameter.getCacheNames()) {
            result += getCoherenceCacheMetric(mBeanServerConnection, monitoringParameter.getCoherenceNode(),
                    cacheNameInDomain, monitoringParameter.getMetricAttributeName());
        }
        return monitoringParameter.isAverageValue() ? (result / monitoringParameter.getCacheNames().size()) : result;
    }

    private double collectServiceMetricValue(CoherenceCacheMonitoringParameter monitoringParameter) throws Exception {
        double result = 0;
        for (String cacheNameInDomain : monitoringParameter.getCacheNames()) {
            result += getCoherenceServiceMetric(mBeanServerConnection, monitoringParameter.getCoherenceNode(),
                    cacheNameInDomain, monitoringParameter.getMetricAttributeName());
        }
        return monitoringParameter.isAverageValue() ? (result / monitoringParameter.getCacheNames().size()) : result;
    }

    private double collectNodeMetricValue(CoherenceNodeMonitoringParameter monitoringParameter) throws Exception {
        return getCoherenceNodeMetric(mBeanServerConnection, monitoringParameter.getCoherenceNode(), monitoringParameter.getMetricAttributeName());
    }

    public boolean isEnabled() {
        return monitoringParametersProvider != null;
    }
}
