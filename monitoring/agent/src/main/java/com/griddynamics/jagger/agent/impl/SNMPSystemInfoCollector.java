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

package com.griddynamics.jagger.agent.impl;

import com.griddynamics.jagger.agent.model.CpuData;
import com.griddynamics.jagger.agent.model.DisksData;
import com.griddynamics.jagger.agent.model.SystemInfoCollector;
import com.griddynamics.jagger.agent.model.TcpData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dkotlyarov
 */
public class SNMPSystemInfoCollector implements SystemInfoCollector {
    private final static Logger logger = LoggerFactory.getLogger(SNMPSystemInfoCollector.class);

    private SNMPProvider provider;

    public SNMPSystemInfoCollector() {
    }

    public SNMPProvider getProvider() {
        return provider;
    }

    public void setProvider(SNMPProvider provider) {
        this.provider = provider;
    }

    @Override
    public List<String> getCPUInfo() {
        return new ArrayList<String>();
    }

    @Override
    public Map<String, String> getCPULoadInfo() {
        return new HashMap<String, String>();
    }

    @Override
    public Map<String, String> getMemInfo() {
        Map<String, String> result = new HashMap<String, String>();
        try {
            long bInKb = 1024;

            long swap = provider.getAsLong(new OID(".1.3.6.1.4.1.2021.4.3.0")) * bInKb;
            long ram = provider.getAsLong(new OID(".1.3.6.1.4.1.2021.4.5.0")) * bInKb;
            long total = ram + swap;
            long ramFree = provider.getAsLong(new OID(".1.3.6.1.4.1.2021.4.6.0")) * bInKb;
            long totalFree = provider.getAsLong(new OID(".1.3.6.1.4.1.2021.4.11.0")) * bInKb;

            result.put("Total", String.valueOf(total));
            result.put("Ram", String.valueOf(ram));
            result.put("Used", String.valueOf(total - totalFree));
            result.put("Free", String.valueOf(totalFree));
            result.put("UsedPercent", String.valueOf(0));
            result.put("FreePercent", String.valueOf(0));
            result.put("ActualUsed", String.valueOf(ram - ramFree));
            result.put("ActualFree", String.valueOf(ramFree));
        } catch (Exception e) {
            logger.warn("exception during getMemInfo", e);
        }
        logger.trace("getMemInfo: {}", result);
        return result;
    }

    @Override
    public Map<String, String> getNetworkInfo() {
        return new HashMap<String, String>();
    }

    @Override
    public TcpData getTcpData() {
        return new TcpData();
    }

    @Override
    public CpuData getCpuData() {
        CpuData data = new CpuData();

        data.setCpuStateIdle(getCPUStateIdle());
        data.setCpuStateSys(getCPUStateSys());
        data.setCpuStateUser(getCPUStateUser());
        data.setCpuStateWait(getCPUStateWait());

        return data;
    }

    @Override
    public DisksData getDisksData() {
        return new DisksData();
    }

    private double getCPUStateSys() {
        double result = 0;
        try {
            return Double.parseDouble(provider.getAsString(new OID(".1.3.6.1.4.1.2021.11.10.0"))) / 100.0;
        } catch (Exception e) {
            logger.warn("Exception during getCPUStateSys", e);
        }
        logger.trace("getCPUStateSys: {}", result);
        return result;
    }

    private double getCPUStateUser() {
        double result = 0;
        try {
            return Double.parseDouble(provider.getAsString(new OID(".1.3.6.1.4.1.2021.11.9.0"))) / 100.0;
        } catch (Exception e) {
            logger.warn("Exception during getCPUStateSys", e);
        }
        logger.trace("getCPUStateUser: {}", result);
        return result;
    }

    private double getCPUStateWait() {
        double result = 0;
        try {
            return Double.parseDouble(provider.getAsString(new OID(".1.3.6.1.4.1.2021.11.51.0"))) / 100.0;
        } catch (Exception e) {
            logger.warn("Exception during getCPUStateWait", e);
        }
        logger.trace("getCPUStateWait: {}", result);
        return result;
    }

    private double getCPUStateIdle() {
        double result = 0;
        try {
            return Double.parseDouble(provider.getAsString(new OID(".1.3.6.1.4.1.2021.11.11.0"))) / 100.0;
        } catch (Exception e) {
            logger.warn("Exception during getCPUStateIdle", e);
        }
        logger.trace("getCPUStateIdle: {}", result);
        return result;
    }

    @Override
    public double[] getLoadAverage() {
        try {
            return new double[] {Double.parseDouble(provider.getAsString(new OID(".1.3.6.1.4.1.2021.10.1.3.1"))),
                                 Double.parseDouble(provider.getAsString(new OID(".1.3.6.1.4.1.2021.10.1.3.2"))),
                                 Double.parseDouble(provider.getAsString(new OID(".1.3.6.1.4.1.2021.10.1.3.3")))};
        } catch (IOException e) {
            logger.warn("Exception during load average polling", e);
            return new double[] {0, 0, 0};
        }
    }
}
