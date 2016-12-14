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
import com.griddynamics.jagger.agent.model.*;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import java.util.Collections;
import java.util.Map;

import static com.griddynamics.jagger.util.Units.bytesToMiB;
import static com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters.*;

/**
 * User: dkotlyarov
 */
public class SNMPJVMSystemUnderTestImpl implements SystemUnderTestService {
    private final static Logger log = LoggerFactory.getLogger(SNMPJVMSystemUnderTestImpl.class);

    // systemIdentifier -> SNMPProvider
    private Map<String, SNMPProvider> snmpProviders;

    // Parameter -> OID
    private Map<DefaultMonitoringParameters, String> snmpOIDs;

    public SNMPJVMSystemUnderTestImpl() {
    }

    @Override
    public Map<String, SystemUnderTestInfo> getInfo() {
        Map<String, SystemUnderTestInfo> result = Maps.newHashMap();

        for (String systemIdentifier : snmpProviders.keySet()) {
            result.put(systemIdentifier, pollSystem(systemIdentifier));
        }
        return result;
    }

    @Override
    public void setContext(AgentContext context) {
        //nothing to do...
    }

    public void setSnmpProviders(Map<String, SNMPProvider> snmpProviders) {
        this.snmpProviders = snmpProviders;
    }

    public void setSnmpOIDs(Map<DefaultMonitoringParameters, String> snmpOIDs) {
        this.snmpOIDs = snmpOIDs;
    }

    private SystemUnderTestInfo pollSystem(String systemIdentifier) {
        SystemUnderTestInfo result = new SystemUnderTestInfo(systemIdentifier);

        SNMPProvider snmpProvider = snmpProviders.get(systemIdentifier);

        try {
            pollJVMMetric(result, snmpProvider, HEAP_MEMORY_MAX, true);
            pollJVMMetric(result, snmpProvider, HEAP_MEMORY_COMMITTED, true);
            pollJVMMetric(result, snmpProvider, HEAP_MEMORY_USED, true);
            pollJVMMetric(result, snmpProvider, HEAP_MEMORY_INIT, true);

            pollJVMMetric(result, snmpProvider, NON_HEAP_MEMORY_MAX, true);
            pollJVMMetric(result, snmpProvider, NON_HEAP_MEMORY_COMMITTED, true);
            pollJVMMetric(result, snmpProvider, NON_HEAP_MEMORY_USED, true);
            pollJVMMetric(result, snmpProvider, NON_HEAP_MEMORY_INIT, true);

            pollJVMMetric(result, snmpProvider, JMX_GC_MAJOR_TIME, false);
            pollJVMMetric(result, snmpProvider, JMX_GC_MAJOR_UNIT, false);
            pollJVMMetric(result, snmpProvider, JMX_GC_MINOR_TIME, false);
            pollJVMMetric(result, snmpProvider, JMX_GC_MINOR_UNIT, false);
        } catch (Exception e) {
            log.error("Failed to collect metrics from " + systemIdentifier, e);
        }

        return result;
    }

    private void pollJVMMetric(SystemUnderTestInfo result, SNMPProvider snmpProvider, DefaultMonitoringParameters key, boolean castToMB) {
        try {
            long metric = snmpProvider.getAsLong(new OID(snmpOIDs.get(key)));
            result.putSysUTEntry(key, castToMB ? bytesToMiB(metric) : (double)metric);
        } catch(Exception e) {
            log.error("Failed to collect metrics from " + snmpProvider.getAddress(), e);
        }
    }

    @Override
    public Map<String, Map<String, String>> getSystemProperties() {
        log.warn("System properties a not available for SNMP");
        return Collections.EMPTY_MAP;
    }
}
