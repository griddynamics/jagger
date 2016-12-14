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

package com.griddynamics.jagger.util;

import com.google.common.collect.Maps;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Map;

/**
 * @author Nikolay Musienko
 *         Date: 13.08.13
 */
public class AgentUtils {

    private static final String JMX_SERVICE_SEPARATOR = ",";

    public static Map<String, JMXConnector> getJMXConnectors(final String[] jmxServices, final String name, final String urlFormat) throws IOException {
        Map<String, JMXConnector> connectors = Maps.newHashMap();
        JMXConnector connector;
        for (String service : jmxServices) {
            int start_prefix = service.lastIndexOf('{');
            String prefix = "";
            String host = service;
            if (start_prefix > 0) {
                int end_prefix = service.lastIndexOf('}');
                if (end_prefix > start_prefix) {
                    prefix = service.substring(start_prefix + 1, end_prefix).trim();
                    if (!prefix.isEmpty()) {
                        prefix += "-";
                    }
                    host = service.substring(0, start_prefix);
                }
            }
            connector = JMXConnectorFactory.connect(new JMXServiceURL(String.format(urlFormat, host)));
            connectors.put(prefix + name + host, connector);
        }
        return connectors;
    }

    public static Map<String, MBeanServerConnection> getMBeanConnections(final Map<String, JMXConnector> connectors) throws IOException {
        Map<String, MBeanServerConnection> mBeanConnections = Maps.newConcurrentMap();
        for (String service : connectors.keySet()) {
            mBeanConnections.put(service, connectors.get(service).getMBeanServerConnection());
        }
        return mBeanConnections;
    }

    public static String[] splitServices(final String services) {
        if (services == null) {
            return new String[0];
        }
        return services.split(JMX_SERVICE_SEPARATOR);
    }
}
